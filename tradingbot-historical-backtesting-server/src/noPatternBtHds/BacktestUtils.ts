import {
	DCompoundExitStrategy,
	DProfitExitStrategy,
	DProfitExitStrategyTargetLimit,
	Name_ProfitExitStrategy_TargetLimit,
	ExchangeTradeFeeTier,
	TradeEntryType,
	TradeFeeType
} from "tradingbot-common-classes";

class BacktestUtils {
	static getEntryPriceWithFee(
		entryPrice_noFee: number,
		entryType: TradeEntryType,
		entryQty: number,
		feeTier: ExchangeTradeFeeTier
	): number {
		const tradeFee: number = feeTier.calculateFeeForTrade(
			BacktestUtils.getDefaultEntryFeeType(),
			entryPrice_noFee // * entryQty
		);

		const priceWithFee: number =
			entryType === TradeEntryType.BULLISH
				? entryPrice_noFee + tradeFee
				: entryPrice_noFee - tradeFee;

		return priceWithFee;
	}

	static getDefaultMaxPullbackEntryCandles(): number {
		return 10;
	}

	static getDefaultEntryVolume(): number {
		return 1;
	}

	static getDefaultEntryFeeType(): TradeFeeType {
		return TradeFeeType.TAKER;
	}

	static getProjectedExitPrice_noFee(
		compoundExitStrategy: DCompoundExitStrategy
	): number | undefined {
		const targetLimitStrategies: DProfitExitStrategy[] =
			compoundExitStrategy.profitSubStrategies.filter(
				(pss: DProfitExitStrategy) =>
					pss.name === Name_ProfitExitStrategy_TargetLimit
			);

		if (targetLimitStrategies.length < 1)
			/*throw new Error(
				`No ProfitExitStrategy_TargetLimit with projected exit price found.`
			);*/
			return undefined;

		const targetLimitStrategy: DProfitExitStrategyTargetLimit =
			targetLimitStrategies[0] as DProfitExitStrategyTargetLimit;
		return targetLimitStrategy.fixedPriceTarget;
	}
}

export default BacktestUtils;
