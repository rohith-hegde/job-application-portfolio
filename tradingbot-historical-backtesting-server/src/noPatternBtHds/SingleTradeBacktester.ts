/* eslint-disable @typescript-eslint/no-inferrable-types */
import { XContinuousHistoricalData } from "tradingbot-common-apisdk-ts";
import {
	DCompleteTradeResult,
	ExchangeTradeFeeTier,
	TradeEntryForceType,
	DTradeEntryRequest,
	DSingleBacktestParams,
	DTradeEntry,
	TradeEntryType,
	TradeFeeType,
	Candle,
	ExitStrategyFactory,
	DCompoundExitStrategy,
	TradeEntryStage,
	DTradeExit,
	DTradeFeeCharged,
	DTradeProfit,
	DSkippedTradeResult,
	DTradeResult,
	DExchangeTradeFeeTier,
	TradeFeeTierFactory,
	CompoundExitStrategy
} from "tradingbot-common-classes";
import BacktestUtils from "./BacktestUtils";
import { NoPatternBacktester } from "./Models";

class SingleTradeBacktester implements NoPatternBacktester {
	continuousHDS: XContinuousHistoricalData;
	feeTier: ExchangeTradeFeeTier;
	feeTierDTO: DExchangeTradeFeeTier;

	forceEntry: TradeEntryForceType;
	entryInfo: DTradeEntryRequest;
	entryStagesAllowed?: TradeEntryStage[];

	exitStrategy: CompoundExitStrategy;
	exitStrategyDTO: DCompoundExitStrategy;
	// backtestParams: DSingleBacktestParams;

	constructor(
		continuousHDS: XContinuousHistoricalData,
		backtestParams: DSingleBacktestParams
	) {
		this.continuousHDS = continuousHDS;
		this.feeTierDTO = backtestParams.tradeFeeInfo;
		this.forceEntry =
			backtestParams.entryForceType ?? TradeEntryForceType.NONE;
		this.entryInfo = backtestParams.entryInfo;
		this.entryStagesAllowed = backtestParams.entryStagesAllowed;
		// this.backtestParams = backtestParams;

		// const exitStrategyFactory: ExitStrategyFactory = new ExitStrategyFactory(this.entryInfo, feeTier);
		// this.exitStrategy = ExitStrategyFactory.
		this.exitStrategyDTO = backtestParams.exitStrategy;
	}

	async doTest(): Promise<DTradeResult> {
		const tradeFeeFactory: TradeFeeTierFactory = new TradeFeeTierFactory();
		this.feeTier = tradeFeeFactory.create_multiFeeTier(this.feeTierDTO);

		const entryInfo: DTradeEntry | null = await this.enterTrade();

		//entering this trade is predicted to be unprofitable, so skip it
		if (entryInfo === null) return new DSkippedTradeResult();

		const exitStrategyFactory: ExitStrategyFactory =
			new ExitStrategyFactory(entryInfo, this.feeTier);
		this.exitStrategy = exitStrategyFactory.create_compoundExitStrategy(
			this.exitStrategyDTO
		);

		let strategyExitInfoOverride: DTradeExit | undefined = undefined;

		while (!strategyExitInfoOverride) {
			let nextCandle: Candle;

			try {
				nextCandle = await this.continuousHDS.getNextCandle();
			} catch (apiErr) {
				const newErr: Error = new Error(
					`Getting next candle after '${
						this.continuousHDS.currCandleInd
					}' failed: ${apiErr.message ?? apiErr}`
				);
				return Promise.reject(newErr);
			}

			this.exitStrategy.feedCandle(nextCandle);
			strategyExitInfoOverride = this.exitStrategy.checkForExit();
		}

		const finalResult: DCompleteTradeResult = this.getExitNowResult(
			strategyExitInfoOverride
		);
		return Promise.resolve(finalResult);
	}

	getExitNowResult(exitInfoOverride?: DTradeExit): DCompleteTradeResult {
		if (!this.exitStrategy)
			throw new Error(
				`Cannot get current backtest result because exitStrategy is undefined.`
			);

		const exitInfo: DTradeExit =
			exitInfoOverride ?? this.exitStrategy.getCurrentExitInfo();
		const entryInfo: DTradeEntry = this.exitStrategy.entryInfo;

		//////////////////////////////////////////////////////////////

		const entryFee: number = Math.abs(
			entryInfo.entryCost - entryInfo.entryCost_noFee
		);
		const exitFee: number = Math.abs(
			exitInfo.exitRevenue - exitInfo.exitRevenue_noFee
		);

		const feeInfo: DTradeFeeCharged = {
			feeTier: this.feeTier,
			entry_feeType: BacktestUtils.getDefaultEntryFeeType(),
			exit_feeType: this.exitStrategy.getDefaultExitFeeType(),
			entry_feeCharged: entryFee,
			exit_feeCharged: exitFee,
			totalFeeCharged: entryFee + exitFee
		};

		//////////////////////////////////////////////////////////////

		const getProfit = (
			entryType: TradeEntryType,
			entryCost: number,
			exitRevenue: number
		): number => {
			//bullish: exitRevenue: 300, entryCost: 250 => profit: 50
			//bearish: exitRevenue: 250, entryCost: 300 => profit: 50
			if (entryType === TradeEntryType.BULLISH)
				return exitRevenue - entryCost;
			else return entryCost - exitRevenue;
		};

		const profit: number = getProfit(
			entryInfo.entryType,
			entryInfo.entryCost,
			exitInfo.exitRevenue
		);

		const profit_noFee: number = getProfit(
			entryInfo.entryType,
			entryInfo.entryCost_noFee,
			exitInfo.exitRevenue_noFee
		);

		const profitInfo: DTradeProfit = {
			profit: profit,
			profit_noFee: profit_noFee,
			profitMargin: profit / entryInfo.entryCost,
			profitMargin_noFee: profit_noFee / entryInfo.entryCost_noFee
		};

		//////////////////////////////////////////////////////////////

		const result: DCompleteTradeResult = {
			entryInfo: entryInfo,
			exitInfo: exitInfo,
			feeInfo: feeInfo,
			profitInfo: profitInfo
		};

		return result;
	}

	async enterTrade(): Promise<DTradeEntry | null> {
		let entryAllowed_instant: boolean = true;
		let entryAllowed_pullback: boolean = true;

		if (this.entryStagesAllowed) {
			entryAllowed_instant = this.entryStagesAllowed.includes(
				TradeEntryStage.INSTANT
			);
			entryAllowed_pullback = this.entryStagesAllowed.includes(
				TradeEntryStage.PULLBACK
			);
		}

		const entryQty: number =
			this.entryInfo.entryQty ?? BacktestUtils.getDefaultEntryVolume();
		const entryType: TradeEntryType = this.entryInfo.entryType;

		let entryCandle: Candle;
		let entryPrice_noFee: number;
		let entryPrice: number;
		let entryStage: TradeEntryStage;
		let forcedType: TradeEntryForceType;

		//check if it's even worth entering the trade based on the predicted fee
		const entryWorthwhile = (entryPrice_noFee: number): boolean => {
			const entryPrice_withFee: number =
				BacktestUtils.getEntryPriceWithFee(
					entryPrice_noFee,
					entryType,
					entryQty,
					this.feeTier
				);

			const projectedExitPrice_noFee: number | undefined =
				BacktestUtils.getProjectedExitPrice_noFee(this.exitStrategyDTO);

			if (!projectedExitPrice_noFee) {
				console.log(
					`SingleTradeBacktester.enterTrade().entryWorthwhile(): projected exit price = undefined. Assuming trade is worthwhile.`
				);
				return true;
			}

			const projectedExitFee: number = this.feeTier.calculateFeeForTrade(
				TradeFeeType.TAKER,
				projectedExitPrice_noFee
			);

			return entryType === TradeEntryType.BULLISH
				? entryPrice_withFee + projectedExitFee < projectedExitPrice_noFee
				: entryPrice_withFee - projectedExitFee > projectedExitPrice_noFee;
		};

		const instantEntryCandle: Candle = this.entryInfo.entryCandle;

		const instantEntryPrice_noFee: number =
			this.entryInfo.entryPrice_noFee ??
			this.entryInfo.entryCandle.prices.open;

		const instantEntryWorthwhile: boolean = entryWorthwhile(
			instantEntryPrice_noFee
		);

		if (instantEntryWorthwhile && entryAllowed_instant) {
			entryCandle = instantEntryCandle;
			entryPrice_noFee = instantEntryPrice_noFee;
			entryStage = TradeEntryStage.INSTANT;
			forcedType = TradeEntryForceType.NONE;
		} else if (
			entryAllowed_pullback &&
			!(this.forceEntry === TradeEntryForceType.INSTANT)
		) {
			//check if there's a pullback that's worth entering
			let pullbackEntryWorthwhile: boolean = false;
			const maxPullbackEntryCandles: number =
				BacktestUtils.getDefaultMaxPullbackEntryCandles();

			let nextCandle: Candle;
			let pullbackEntryPrice_noFee: number;
			let i = 1;

			do {
				nextCandle = await this.continuousHDS.getNextCandle();
				pullbackEntryPrice_noFee = nextCandle.prices.open;

				pullbackEntryWorthwhile = entryWorthwhile(
					pullbackEntryPrice_noFee
				);
				i++;
			} while (i <= maxPullbackEntryCandles && !pullbackEntryWorthwhile);

			if (pullbackEntryWorthwhile) {
				entryCandle = await this.continuousHDS.getNextCandle(); //nextCandle;
				entryPrice_noFee = pullbackEntryPrice_noFee;

				entryStage = TradeEntryStage.PULLBACK;
				forcedType = TradeEntryForceType.NONE;
			} else return Promise.resolve(null); //no type of entry is worthwhile :(
		} else if (this.forceEntry === TradeEntryForceType.INSTANT) {
			entryCandle = instantEntryCandle;
			entryPrice_noFee = instantEntryPrice_noFee;
			entryStage = TradeEntryStage.INSTANT;
			forcedType = TradeEntryForceType.INSTANT;
		} /*else if (this.forceEntry === TradeEntryForceType.PULLBACK) {

		}*/ else return Promise.resolve(null); //breakout entry is not worthwhile :(

		// eslint-disable-next-line prefer-const
		entryPrice = BacktestUtils.getEntryPriceWithFee(
			entryPrice_noFee,
			entryType,
			entryQty,
			this.feeTier
		);

		const entryInfo: DTradeEntry = {
			entryStage: entryStage,
			entryCandle: entryCandle,
			entryPrice_noFee: entryPrice_noFee,
			entryPrice: entryPrice,
			entryCost_noFee: entryPrice_noFee * entryQty,
			entryCost: entryPrice * entryQty,
			entryQty: entryQty,
			entryType: entryType,
			forcedType: forcedType
		};

		return Promise.resolve(entryInfo);
	}
}

export default SingleTradeBacktester;
