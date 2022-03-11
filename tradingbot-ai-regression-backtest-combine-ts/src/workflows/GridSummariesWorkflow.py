from datetime import datetime, timedelta
from typing import Dict, List, Tuple
from Models import InputArgs, SegmentInfo
from util.FileWrappers import FileWrapper
from util.S3DownloadExt import downloadGridInputBucketFiles, getSegmentIdFromFile
import pandas as PD


class GridSummariesWorkflow():
    args: InputArgs
    segmentInfo: SegmentInfo

    def __init__(self, args: InputArgs, segmentInfo: SegmentInfo):
        self.args = args
        self.segmentInfo = segmentInfo
        return

    def run(self) -> List[Tuple[int, PD.DataFrame]]:
        startTime: datetime = datetime.now()

        print(f"GridSummariesWorkflow.run(): Downloading grid summary input CSV files...")
        files: List[FileWrapper] = self.downloadBucket()

        print(f"GridSummariesWorkflow.run(): Loading ordered grid summary files...")
        orderedGridSummaries: List[Tuple[int, PD.DataFrame]
                                   ] = self.loadGridSummariesOrdered(files)

        print(f"GridSummariesWorkflow.run(): Verifying grid summary data integrity...")
        self.verifyIntegrity(orderedGridSummaries)

        endTime: datetime = datetime.now()
        elapsedTime: timedelta = endTime - startTime
        print(f"GridSummariesWorkflow.run(): Finished in {elapsedTime}.")

        return orderedGridSummaries

    def downloadBucket(self) -> List[FileWrapper]:
        return downloadGridInputBucketFiles(
            self.args.gridSearchIdPrefix, "gridSummary")

    def loadGridSummariesOrdered(self, files: List[FileWrapper]) -> List[Tuple[int, PD.DataFrame]]:
        segmentIdToFileDict: Dict[int, PD.DataFrame] = {}

        for file in files:
            fileFrame: PD.DataFrame = file.loadFile_csvFrame()
            fileFrame = fileFrame.set_index("Unnamed: 0")

            fileSegmentId: int = getSegmentIdFromFile(file)
            segmentIdToFileDict[fileSegmentId] = fileFrame

        gridSummariesOrdered: List[Tuple[int, PD.DataFrame]] = []

        for segmentId in self.segmentInfo.segmentIds:
            if segmentId in segmentIdToFileDict:
                fileFrame: PD.DataFrame = segmentIdToFileDict[segmentId]
                gridSummariesOrdered.append([segmentId, fileFrame])

        return gridSummariesOrdered

    def verifyIntegrity(
            self, orderedGridSummaries: List[Tuple[int, PD.DataFrame]]) -> None:
        if len(orderedGridSummaries) != len(self.segmentInfo.segmentIds):
            print(
                f"GridSummariesWorkflow.verifyIntegrity(): WARNING: Mismatch between grid summary count ({len(orderedGridSummaries)}) and number of segments ({len(self.segmentInfo.segmentIds)}). Probable cause: failed backtest-combine operation(s).")
            '''raise ValueError(
                f"Mismatch between grid summary count ({len(orderedGridSummaries)}) and number of segments ({len(self.segmentInfo.segmentIds)}).")'''

        def segmentIdInList(
                orderedGridSummaries: List[Tuple[int, PD.DataFrame]],
                segmentId: int) -> bool:
            found: bool = False

            for tupleValue in orderedGridSummaries:
                gridSegmentId: int = tupleValue[0]
                if gridSegmentId == segmentId:
                    found = True

            return found

        for segmentId in self.segmentInfo.segmentIds:
            if not segmentIdInList(orderedGridSummaries, segmentId):
                print(
                    f"GridSummariesWorkflow.verifyIntegrity(): WARNING: Expected segmentId '{segmentId}' is not in ordered grid summary list. Probable cause: failed backtest-combine operation.")
                '''raise IndexError(
                    f"Expected segment ID '{segmentId}' is not in grid summaries.")'''

        return
