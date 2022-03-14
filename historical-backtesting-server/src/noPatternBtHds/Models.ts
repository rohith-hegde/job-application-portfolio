import {
	DCompleteTradeResult,
	DTradeEntryRequest,
	DTradeExit,
	DTradeResult,
	DTradeResultResponse,
	ExchangeTradeFeeTier,
	TradeEntryForceType,
	TradeEntryStage,
	TradeExitStrategy
} from "tradingbot-common-classes";
import { XContinuousHistoricalData } from "tradingbot-common-apisdk-ts";

interface NoPatternBacktester {
	// preliminaryResult: DCompleteTradeResult | undefined;
	continuousHDS: XContinuousHistoricalData;
	feeTier: ExchangeTradeFeeTier;
	forceEntry: TradeEntryForceType;
	entryStagesAllowed?: TradeEntryStage[];
	entryInfo: DTradeEntryRequest;
	exitStrategy: TradeExitStrategy;

	doTest(): Promise<DTradeResult>;
	getExitNowResult(exitInfoOverride?: DTradeExit): DCompleteTradeResult;
}

type SingleBacktestFunc = () => Promise<DTradeResultResponse>;

const GROUP_BACKTEST_MAX_ALLOWED_FAILURE_RATE = 0.05;

export {
	NoPatternBacktester,
	SingleBacktestFunc,
	GROUP_BACKTEST_MAX_ALLOWED_FAILURE_RATE
};
