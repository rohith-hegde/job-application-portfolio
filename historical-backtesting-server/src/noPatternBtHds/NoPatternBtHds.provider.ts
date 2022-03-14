import {
	CandlePositionFinder,
	CandleSegment,
	DGroupBacktestParams,
	DSingleBacktestParams,
	DTradeResult,
	DTradeResultGroupResponse,
	DTradeResultResponse,
	Exchange,
	DTradeResultPerformanceInfo,
	DDataSelectParamsByIdQuery,
	DTradeEntryRequest,
	GroupedSampleResults,
	MapGroupedSampleResults,
	DGroupedSampleResults,
	DCompoundExitStrategy
} from "tradingbot-common-classes";
import { NoPatternBtProvider } from "../noPatternBt/NoPatternBt.models";
import {
	ApiClientOptions,
	CacheOptions,
	HistoricalData,
	XContinuousHistoricalData
} from "tradingbot-common-apisdk-ts";
import SingleTradeBacktester from "./SingleTradeBacktester";
import {
	GROUP_BACKTEST_MAX_ALLOWED_FAILURE_RATE,
	NoPatternBacktester,
	SingleBacktestFunc
} from "./Models";
import { generateSampleGroupDTO } from "./DTOUtils";
import FuncBatchRateLimiter from "../concurrency/FuncBatchRateLimiter";

class NoPatternBtProvider_HDS implements NoPatternBtProvider {
	hdsClient: HistoricalData;
	backtestThreadLimit: number;

	constructor(
		backtestThreadLimit: number,
		hdsClientOptions: ApiClientOptions,
		hdsCacheOptions?: CacheOptions
	) {
		this.hdsClient = new HistoricalData(hdsClientOptions, hdsCacheOptions);
		this.backtestThreadLimit = backtestThreadLimit;
	}

	async doSingleBacktest(
		backtestParams: DSingleBacktestParams,
		seriesNum?: number
	): Promise<DTradeResultResponse> {
		let backtester: NoPatternBacktester;
		const performanceInfo: DTradeResultPerformanceInfo = {};

		try {
			const init_startTimeMs: number = new Date().getTime();

			const reqExchange: Exchange = {
				domainName: backtestParams.segmentParams.exchange_domainName,
				name: backtestParams.segmentParams.exchange_name
			};

			const startingSegment: CandleSegment =
				await this.hdsClient.getDatasetById.send({
					exchange: reqExchange,
					segmentId: Number(backtestParams.segmentParams.segmentId)
				});

			const candleFinder: CandlePositionFinder = new CandlePositionFinder(
				startingSegment
			);
			const entryCandlePos: number = candleFinder.findCandleIndex(
				backtestParams.entryInfo.entryCandle
			);

			const continuousHDS: XContinuousHistoricalData =
				new XContinuousHistoricalData(
					this.hdsClient,
					reqExchange,
					startingSegment,
					entryCandlePos
				);

			backtester = new SingleTradeBacktester(
				continuousHDS,
				backtestParams
			);

			const init_endTimeMs: number = new Date().getTime();
			performanceInfo.initTimeMs = init_endTimeMs - init_startTimeMs;
		} catch (rootE) {
			const initErr: Error = new Error(
				`datasetParams [${backtestParams.segmentParams}], ` +
					(seriesNum ? `${seriesNum} in series | ` : ``) +
					`Error initializing backtester resources: ${rootE.message}`
			);

			console.error(initErr);
			return Promise.reject(initErr);
		}

		try {
			const op_startTimeMs: number = new Date().getTime();
			const backtestResult: DTradeResult = await backtester.doTest();

			const op_endTimeMs: number = new Date().getTime();
			performanceInfo.operationTimeMs = op_endTimeMs - op_startTimeMs;

			return Promise.resolve({
				result: backtestResult,
				performance: performanceInfo
			});
		} catch (rootE) {
			const testErr: Error = new Error(
				(seriesNum ? `${seriesNum} in series | ` : ``) +
					`Error running backtest operation: ${rootE.message ?? rootE}`
			);
			console.log(testErr);
			return Promise.reject(testErr);
		}
	}

	async doGroupBacktest(
		backtestParams: DGroupBacktestParams
	): Promise<DTradeResultGroupResponse> {
		const performanceInfo: DTradeResultPerformanceInfo = {};
		const op_startTimeMs: number = new Date().getTime();

		const singleBacktestFuncs: SingleBacktestFunc[] = [];

		for (let i = 0; i < backtestParams.segmentsParams.length; i++) {
			const segmentParams: DDataSelectParamsByIdQuery =
				backtestParams.segmentsParams[i];
			const entryInfo: DTradeEntryRequest = backtestParams.entriesInfo[i];
			const exitStrategy: DCompoundExitStrategy =
				backtestParams.exitStrategies[i];

			const singleBacktestParams: DSingleBacktestParams = {
				entryInfo: entryInfo,
				segmentParams: segmentParams,
				exitStrategy: exitStrategy,
				tradeFeeInfo: backtestParams.tradeFeeInfo,
				entryForceType: backtestParams.entryForceType,
				entryStagesAllowed: backtestParams.entryStagesAllowed
			};

			const backtestFunc: SingleBacktestFunc = () =>
				new Promise((res, rej) => {
					this.doSingleBacktest(singleBacktestParams, i + 1)
						.then((tradeResp: DTradeResultResponse) => res(tradeResp))
						.catch((err) => rej(err));
				});

			singleBacktestFuncs.push(backtestFunc);
		}

		let singleBacktestResults: DTradeResultResponse[];
		const maxFailedSingleBacktestCt: number = Math.ceil(
			singleBacktestFuncs.length * GROUP_BACKTEST_MAX_ALLOWED_FAILURE_RATE
		);

		try {
			const rateLimiter: FuncBatchRateLimiter = new FuncBatchRateLimiter(
				singleBacktestFuncs,
				this.backtestThreadLimit,
				maxFailedSingleBacktestCt
			);

			singleBacktestResults = await rateLimiter.run(); // await Promise.all(singleBacktestProms);
		} catch (rootE) {
			const awaitE: Error = new Error(
				`Error while awaiting individual backtest results: ${rootE.message}\n${rootE.stack}`
			);
			return Promise.reject(awaitE);
		}

		let results_dto: DGroupedSampleResults;

		try {
			const groupedSampleResults: GroupedSampleResults =
				new MapGroupedSampleResults(
					singleBacktestResults.map(
						(sbr: DTradeResultResponse) => sbr.result
					)
				);

			results_dto = generateSampleGroupDTO(
				groupedSampleResults,
				backtestParams.resultSampleGroups
			);
		} catch (rootE) {
			const groupE: Error = new Error(
				`Error while grouping backtest results: ${rootE.message}\n${rootE.stack}`
			);
			return Promise.reject(groupE);
		}

		const op_endTimeMs: number = new Date().getTime();
		performanceInfo.operationTimeMs = op_endTimeMs - op_startTimeMs;

		const resp_dto: DTradeResultGroupResponse = {
			results: results_dto,
			performance: performanceInfo
		};
		return Promise.resolve(resp_dto);
	}
}

export { NoPatternBtProvider_HDS };
