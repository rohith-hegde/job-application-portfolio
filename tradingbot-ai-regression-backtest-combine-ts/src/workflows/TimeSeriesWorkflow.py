import numpy as NP
from datetime import datetime, timedelta
from random import randrange
import time
from types import FunctionType
from typing import Any, Dict, List, Tuple
from Models import InputArgs, SegmentInfo
from util.FileWrappers import OUTPUT_LOCAL_PATH, FileWrapper, FolderWrapper
import pandas as PD

RESULT_TYPES: List[str] = ["exp24hROI", "meanTradeProfit",
                           "meanCandlesToExit", "meanTimeToExitMin"]
GROUP_TYPES: List[str] = [
    "groupedByAll", "results.groupedByEntryType.bullish",
    "results.groupedByEntryType.bearish",
    "results.groupedByPriceTargetReached.true",
    "results.groupedByPriceTargetReached.false"]


def getBestExitStrategyMeasureOrder(resultType: str) -> bool:
    if resultType == "exp24hROI" or resultType == "meanTradeProfit":
        return False
    elif resultType == "meanCandlesToExit" or resultType == "meanTimeToExitMin":
        return True
    else:
        raise IndexError(f"Unknown result type '{resultType}'.")


def getGroupingFileName(groupType: str) -> str:
    renameDict: Dict[str, str] = {
        GROUP_TYPES[0]: "groupedByAll",
        GROUP_TYPES[1]: "entryType-bullish",
        GROUP_TYPES[2]: "entryType-bearish",
        GROUP_TYPES[3]: "targetReached-true",
        GROUP_TYPES[4]: "targetReached-false",
    }
    return renameDict[groupType]


def getFrameFileName(gridSearchPrefix: int, resultType: str, groupType: str):
    resultFileName: str = f"{resultType}"
    groupingFileName: str = getGroupingFileName(groupType)
    return f"timeseries_gridPrefix-{gridSearchPrefix}_{resultFileName}_{groupingFileName}.csv"


class TimeSeriesWorkflow():
    args: InputArgs
    segmentInfo: SegmentInfo
    geckoFrame: PD.DataFrame
    segmentGridSummaries: List[Tuple[int, PD.DataFrame]]

    def __init__(
            self, args: InputArgs, segmentInfo: SegmentInfo,
            geckoFrame: PD.DataFrame,
            segmentGridSummaries: List[Tuple[int, PD.DataFrame]]):
        self.args = args
        self.segmentInfo = segmentInfo
        self.geckoFrame = geckoFrame
        self.segmentGridSummaries = segmentGridSummaries
        return

    def run(self) -> FolderWrapper:
        print(f"TimeSeriesWorkflow.run(): Exporting timeseries output files...")
        startTime: datetime = datetime.now()

        exportFolder: FolderWrapper = self.exportTimeSeriesFrames()

        endTime: datetime = datetime.now()
        elapsedTime: timedelta = endTime - startTime

        print(f"TimeSeriesWorkflow.run(): Workflow finished in {elapsedTime}.")
        return exportFolder

    def exportTimeSeriesFrames(self) -> FolderWrapper:
        outputFolder: FolderWrapper = FolderWrapper(OUTPUT_LOCAL_PATH)
        randomTsId: int = randrange(1000 ** 2, 1000 ** 3)
        currTimeStr: str = datetime.utcnow().strftime("%Y-%m-%d_%H.%M.%S")

        outputFolderGrid: FolderWrapper = outputFolder.getSubfolder(
            f"gridSearchPrefix-{self.args.gridSearchIdPrefix}_tsId-{randomTsId}_{self.segmentInfo.pair}_{currTimeStr}")

        for resultType in RESULT_TYPES:
            for groupType in GROUP_TYPES:
                print(
                    f"TimeSeriesWorkflow.exportTimeSeriesFrames(): Building timeseries frame for {[resultType, groupType]} ...")
                frame: PD.DataFrame = self.buildTimeSeriesFrame(
                    resultType, groupType)

                frameFileName: str = getFrameFileName(
                    gridSearchPrefix=self.args.gridSearchIdPrefix,
                    resultType=resultType, groupType=groupType)
                frameFile: FileWrapper = outputFolderGrid.getFile(frameFileName)

                print(
                    f"TimeSeriesWorkflow.exportTimeSeriesFrames(): Exporting timeseries CSV output file '{frameFile.getFileName()}' ...\n")
                frameFile.saveFile_csvFrame(frame)

        return outputFolderGrid

    def buildTimeSeriesFrame(
            self, resultType: str, groupType: str) -> PD.DataFrame:
        colNames_base: List[str] = [
            "segmentId", "price", "priceChange12h", "priceChange24h",
            "priceChangeSegmentPeriod"]
        colNames_grid: List[str] = self.getGridSummaryColumns()
        colNames_all: List[str] = [*colNames_base, *colNames_grid]

        timeSeriesFrame: PD.DataFrame = PD.DataFrame(
            data=[], columns=colNames_all)

        for rowInd in range(0, len(self.geckoFrame)):
            rowTime: datetime = self.geckoFrame.index[rowInd]

            newRow_segmentId: int = int(self.geckoFrame["segmentId"][rowInd])
            newRow_price: float = self.geckoFrame["price"][rowInd]
            newRow_priceChange12h: float = self.geckoFrame["priceChange12h"][
                rowInd]
            newRow_priceChange24h: float = self.geckoFrame["priceChange24h"][
                rowInd]
            newRow_priceChangeSegmentPeriod: float = self.geckoFrame[
                "priceChangeSegmentPeriod"][rowInd]

            newRow_gridSummary: Any

            try:
                newRow_gridSummary = self.getGridSummaryRowForSegmentId(
                    newRow_segmentId, resultType, groupType)
            except Exception as rootE:
                print(
                    f"TimeSeriesWorkflow.buildTimeSeriesFrame(): WARNING: getGridSummaryRowForSegmentId() failed for segment ID '{newRow_segmentId}' / row {rowInd}. \n\tRoot cause: {rootE}. \n\tInserting dummy values into timeseries row...")
                newRow_gridSummary = [NP.nan for c in range(len(colNames_grid))]

            newRow: Any = [newRow_segmentId, newRow_price,
                           newRow_priceChange12h, newRow_priceChange24h,
                           newRow_priceChangeSegmentPeriod, *newRow_gridSummary]

            timeSeriesFrame.loc[rowTime] = newRow

        timeSeriesFrame["segmentId"] = timeSeriesFrame[
            "segmentId"].astype(int)

        bestStrategyFrame: PD.DataFrame = self.buildBestExitStrategyFrame(
            timeSeriesFrame=timeSeriesFrame, resultType=resultType)

        timeSeriesFrame.insert(
            len(colNames_base),
            "bestStrategy", list(bestStrategyFrame["bestStrategy"]))

        return timeSeriesFrame

    def buildBestExitStrategyFrame(
            self, timeSeriesFrame: PD.DataFrame, resultType: str) -> PD.DataFrame:
        tsFrameColNames: List[str] = timeSeriesFrame.columns.to_list()
        exitStrategyColNames: List[str] = list(
            filter(lambda c: "target" in c and "stop" in c, tsFrameColNames))

        exitStrategyMeasureDict: Dict[float, str] = {}

        for colName in exitStrategyColNames:
            measureValue: float = timeSeriesFrame[colName].mean()
            exitStrategyMeasureDict[measureValue] = colName

        reverseSort: bool = not getBestExitStrategyMeasureOrder(resultType)
        sortedColNames: List[str] = []

        for sortedColNameKey in sorted(
                exitStrategyMeasureDict, reverse=reverseSort):
            sortedColNames.append(exitStrategyMeasureDict[sortedColNameKey])

        bestColName: str = sortedColNames[0]
        bestColList: List[float] = timeSeriesFrame[bestColName].to_list()

        bestColumnFrame: PD.DataFrame = PD.DataFrame(
            {"bestStrategy": bestColList})
        return bestColumnFrame

    def getGridSummaryColumns(self) -> List[str]:
        anyFrame: PD.DataFrame = self.segmentGridSummaries[0][1]
        return anyFrame.columns.tolist()

    def getGridSummaryRowForSegmentId(
            self, segmentId: int, resultType: str, groupType: str) -> Any:
        def filterGridSummaries(
                segmentGridSummary: Tuple[int, PD.DataFrame]) -> bool:
            return segmentGridSummary[0] == segmentId

        gridSummaryFrame: PD.DataFrame = list(
            filter(filterGridSummaries, self.segmentGridSummaries))[0][1]
        resultKey: str = f"summaryMetric.{resultType}.{groupType}"

        resultRow: List[float] = list(gridSummaryFrame.loc[resultKey])
        return resultRow
