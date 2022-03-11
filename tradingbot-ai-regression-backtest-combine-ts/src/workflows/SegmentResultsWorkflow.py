
from datetime import datetime, timedelta, timezone
from math import ceil
from typing import Dict, List, Tuple
from Models import InputArgs, SegmentInfo
from util.FileWrappers import FileWrapper
from util.S3DownloadExt import downloadGridInputBucketFiles, getSegmentIdFromFile
import pandas as PD


class SegmentResultsWorkflow():
    args: InputArgs

    def __init__(self, args: InputArgs):
        self.args = args
        return

    def run(self) -> Tuple[SegmentInfo, List[PD.DataFrame]]:
        startTime: datetime = datetime.now()

        print(f"SegmentResultsWorkflow.run(): Downloading segment result input CSV files...")
        downloadedFiles: List[FileWrapper] = self.downloadBucket()

        print(f"SegmentResultsWorkflow.run(): Parsing candle segment metadata...")
        candleIntervalS: int = self.getCandleIntervalS(downloadedFiles[0])
        segmentDurationS: int = self.getSegmentDurationS(downloadedFiles[0])
        pair: str = self.getPair(downloadedFiles[0])

        print(f"SegmentResultsWorkflow.run(): Loading candle segment frames...")
        segmentResults: List[PD.DataFrame] = self.loadSegmentResults(
            downloadedFiles)

        print(f"SegmentResultsWorkflow.run(): Generating ordered candle segment list...")
        segmentRangeTimes: Tuple[datetime, datetime] = self.getSegmentRangeTimes(
            segmentResults)
        segmentIds: List[int] = self.getOrderedSegmentIds(segmentResults)

        times: SegmentInfo = SegmentInfo(
            pair=pair,
            segmentIds=segmentIds,
            candleIntervalS=candleIntervalS, segmentDurationS=segmentDurationS,
            firstSegmentStartTime=segmentRangeTimes[0],
            lastSegmentEndTime=segmentRangeTimes[len(segmentRangeTimes) - 1])

        endTime: datetime = datetime.now()
        elapsedTime: timedelta = endTime - startTime
        print(f"GridSummariesWorkflow.run(): Finished in {elapsedTime}.")

        return [times, segmentResults]

    def downloadBucket(self) -> List[FileWrapper]:
        return downloadGridInputBucketFiles(
            self.args.gridSearchIdPrefix, "combinedSegmentResults")

    def getSegmentIdsFromFiles(self, files: List[FileWrapper]) -> List[int]:
        return list(map(getSegmentIdFromFile, files))

    def getPair(self, anyFile: FileWrapper) -> int:
        frame: PD.DataFrame = anyFile.loadFile_csvFrame()
        frame = frame.set_index("Unnamed: 0")

        pairSymbol_raw: str = frame.loc["segmentInfo.pair.joinedSymbol"][0]
        pairSymbol: str = pairSymbol_raw.replace("/", "-")
        return pairSymbol

    def getCandleIntervalS(self, anyFile: FileWrapper) -> int:
        frame: PD.DataFrame = anyFile.loadFile_csvFrame()
        frame = frame.set_index("Unnamed: 0")
        candleIntervalS: int = int(frame.loc["segmentInfo.intervalS"][0])
        return candleIntervalS

    def getSegmentDurationS(self, anyFile: FileWrapper) -> int:
        frame: PD.DataFrame = anyFile.loadFile_csvFrame()
        frame = frame.set_index("Unnamed: 0")

        startTime: datetime = datetime.fromisoformat(
            frame.loc["segmentInfo.start_time"][0])
        endTime: datetime = datetime.fromisoformat(
            frame.loc["segmentInfo.end_time"][0])

        elapsedTime: timedelta = endTime - startTime
        elapsedTimeS: int = ceil(elapsedTime.total_seconds())
        return elapsedTimeS

    def loadSegmentResults(self, files: List[FileWrapper]) -> List[PD.DataFrame]:
        def loadSingleSegmentResults(file: FileWrapper) -> PD.DataFrame:
            frame: PD.DataFrame = file.loadFile_csvFrame()
            frame = frame.set_index("Unnamed: 0")
            return frame

        return list(map(loadSingleSegmentResults, files))

    def getSegmentRangeTimes(self, segmentResults: List[PD.DataFrame]) -> Tuple[datetime, datetime]:
        earliestStartTime: datetime = datetime(
            year=9999, month=1, day=1, tzinfo=timezone.utc)
        latestEndTime: datetime = datetime(
            year=1, month=1, day=1, tzinfo=timezone.utc)

        for segmentResult in segmentResults:
            startTime: datetime = datetime.fromisoformat(
                segmentResult.loc["segmentInfo.start_time"][0])
            endTime: datetime = datetime.fromisoformat(
                segmentResult.loc["segmentInfo.end_time"][0])

            if startTime < earliestStartTime:
                earliestStartTime = startTime
            if endTime > latestEndTime:
                latestEndTime = endTime

        return [earliestStartTime, latestEndTime]

    def getOrderedSegmentIds(
            self, segmentResults: List[PD.DataFrame]) -> List[int]:
        sortDict: Dict[datetime, int] = {}

        def putSegmentInDict(segmentResult: PD.DataFrame) -> None:
            segmentId: int = segmentResult.loc["segmentId"][0]
            startTime: datetime = datetime.fromisoformat(
                segmentResult.loc["segmentInfo.start_time"][0])
            sortDict[startTime] = segmentId

        for segmentResult in segmentResults:
            putSegmentInDict(segmentResult)

        sortedSegmentIds: List[int] = []

        for sortedTimeKey in sorted(sortDict):
            sortedSegmentId: int = sortDict[sortedTimeKey]
            sortedSegmentIds.append(sortedSegmentId)

        return sortedSegmentIds
