import {
	DSingleBacktestParams,
	DTradeResultResponse,
	DGroupBacktestParams,
	DTradeResultGroupResponse
} from "tradingbot-common-classes";

interface NoPatternBtProvider {
	doSingleBacktest(
		backtestParams: DSingleBacktestParams,
		seriesNum?: number
	): Promise<DTradeResultResponse>;

	doGroupBacktest(
		backtestParams: DGroupBacktestParams
	): Promise<DTradeResultGroupResponse>;
}

export { NoPatternBtProvider };
