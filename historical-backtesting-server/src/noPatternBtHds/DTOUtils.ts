import {
	GroupedSampleResults,
	SampleGroupByType,
	DGroupedSampleResults,
	SampleResults,
	SimpleSampleResults,
	DSampleResults,
	BacktestStatistics,
	DBacktestStatistics,
	DConfidenceIntervalLevel,
	DConfidenceIntervalZScore,
	DConfidenceIntervalPercent,
	DSampleExits,
	SampleExits
} from "tradingbot-common-classes";

function backtestStatisticsToDTO(
	backtestStatistics: BacktestStatistics
): DBacktestStatistics {
	const confLevel: DConfidenceIntervalLevel = {
		zScore: DConfidenceIntervalZScore.NINETY_FIVE,
		percentage: DConfidenceIntervalPercent.NINETY_FIVE
	};

	const backtestStatistics_dto: DBacktestStatistics = {
		sortedValues: backtestStatistics.sortedValues,
		summary: backtestStatistics.getSummary(),
		positiveCount: backtestStatistics.getPositiveCount(),
		negativeCount: backtestStatistics.getNegativeCount(),
		positiveProportion: backtestStatistics.getPositiveProportion(),
		negativeProportion: backtestStatistics.getNegativeProportion(),
		positiveProportionCI_95:
			backtestStatistics.getPositiveProportionCI(confLevel),
		negativeProportionCI_95:
			backtestStatistics.getNegativeProportionCI(confLevel),
		meanCI_95: backtestStatistics.getMeanCI(confLevel)
	};

	return backtestStatistics_dto;
}

function areSampleExitResultsInvalid(exits: SampleExits): boolean {
	const exit_priceTargetsReached: BacktestStatistics =
		exits.priceTargetsReached;
	const exit_candlesToExit: BacktestStatistics = exits.candlesToExit;
	const exit_timeToExitSec: BacktestStatistics = exits.timeToExitSec;

	if (
		exit_priceTargetsReached.sortedValues.length < 1 ||
		exit_candlesToExit.sortedValues.length < 1 ||
		exit_timeToExitSec.sortedValues.length < 1
	)
		return true;
	else return false;
}

function sampleResultsToDTO(
	sampleResults: SampleResults,
	excludeExitInfo: boolean
): DSampleResults {
	// throw new Error(
	// 	`Error with exit results: resultSampleGroup for key '${keyName}' contains only DSkippedTradeResults.`
	// );

	const sampleResults_dto: DSampleResults = {
		sampleSize: sampleResults.sampleSize,
		individualResults: sampleResults.individualResults,
		profitInfo: {
			profitMargin: backtestStatisticsToDTO(
				sampleResults.profitInfo.profitMargin
			),
			profitMargin_noFee: backtestStatisticsToDTO(
				sampleResults.profitInfo.profitMargin_noFee
			)
		},
		exitInfo: excludeExitInfo
			? undefined
			: {
					priceTargetsReached: backtestStatisticsToDTO(
						sampleResults.exitInfo.priceTargetsReached
					),
					candlesToExit: backtestStatisticsToDTO(
						sampleResults.exitInfo.candlesToExit
					),
					timeToExitSec: backtestStatisticsToDTO(
						sampleResults.exitInfo.timeToExitSec
					)
			  }
	};

	return sampleResults_dto;
}

function sampleResultsMapToDTO(
	sampleResultsMap: Map<string, SampleResults>
): { [key: string]: DSampleResults } {
	const sampleResultsMap_dto: { [key: string]: DSampleResults } = {};

	Array.from(sampleResultsMap.keys()).forEach((key: string) => {
		const sampleResults_val: SampleResults = sampleResultsMap.get(key);
		const excludeExitInfo: boolean = areSampleExitResultsInvalid(
			sampleResults_val.exitInfo
		);

		sampleResultsMap_dto[key] = sampleResultsToDTO(
			sampleResults_val,
			excludeExitInfo
		);
		// console.log(
		// 	`DTOUtils.sampleResultsMapToDTO(): excluding invalid sample results for key 'key'`
		// );
	});

	return sampleResultsMap_dto;
}

function generateSampleGroupDTO(
	groupedSampleResults: GroupedSampleResults,
	groupByTypes: SampleGroupByType[]
): DGroupedSampleResults {
	const sampleResults_all: SampleResults = new SimpleSampleResults(
		groupedSampleResults.individualResults
	);

	const sampleResults_all_excludeExitInfo: boolean =
		areSampleExitResultsInvalid(sampleResults_all.exitInfo);

	const results_dto: DGroupedSampleResults = {
		groupedByAll: sampleResultsToDTO(
			sampleResults_all,
			sampleResults_all_excludeExitInfo
		)
	};

	groupByTypes.forEach((groupType: SampleGroupByType) => {
		switch (groupType) {
			case SampleGroupByType.CANDLE_INTERVAL:
				results_dto.groupedByCandleInterval = sampleResultsMapToDTO(
					groupedSampleResults.groupByCandleInterval()
				);
				break;

			case SampleGroupByType.ENTRY_TYPE:
				results_dto.groupedByEntryType = sampleResultsMapToDTO(
					groupedSampleResults.groupByEntryType()
				);
				break;

			case SampleGroupByType.ENTRY_STAGE:
				results_dto.groupedByEntryStage = sampleResultsMapToDTO(
					groupedSampleResults.groupByEntryStage()
				);
				break;

			case SampleGroupByType.EXCHANGE:
				results_dto.groupedByExchange = sampleResultsMapToDTO(
					groupedSampleResults.groupByExchange()
				);
				break;

			case SampleGroupByType.EXIT_STRATEGY_EXECUTED:
				results_dto.groupedByExitStrategyExecuted = sampleResultsMapToDTO(
					groupedSampleResults.groupByExitStrategyExecuted()
				);
				break;

			case SampleGroupByType.FORCED_ENTRY_TYPE:
				results_dto.groupedByForcedEntry = sampleResultsMapToDTO(
					groupedSampleResults.groupByForcedEntry()
				);
				break;

			case SampleGroupByType.MARKET:
				results_dto.groupedByMarket = sampleResultsMapToDTO(
					groupedSampleResults.groupByMarket()
				);
				break;

			case SampleGroupByType.PRICE_TARGET_REACHED:
				results_dto.groupedByPriceTargetReached = sampleResultsMapToDTO(
					groupedSampleResults.groupByPriceTargetReached()
				);
				break;

			case SampleGroupByType.SAMPLE_SPLIT:
				results_dto.groupedBySampleSplit = sampleResultsMapToDTO(
					groupedSampleResults.groupBySampleSplit()
				);
				break;
		}
	});

	return results_dto;
}

export {
	backtestStatisticsToDTO,
	generateSampleGroupDTO,
	sampleResultsToDTO,
	sampleResultsMapToDTO
};
