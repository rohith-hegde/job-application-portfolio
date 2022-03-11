
from Models import InputArgs, SegmentInfo
from util.FileWrappers import OUTPUT_LOCAL_PATH, FileWrapper, FolderWrapper
from datetime import datetime, timedelta
from typing import Any, Dict, List, Tuple
import pandas as PD


class MetadataWorkflow():
    args: InputArgs
    segmentInfo: SegmentInfo
    exportFolder: FolderWrapper

    def __init__(
            self, args: InputArgs, segmentInfo: SegmentInfo,
            exportFolder: FolderWrapper):
        self.args = args
        self.segmentInfo = segmentInfo
        self.exportFolder = exportFolder
        return

    def run(self) -> None:
        print(f"MetadataWorkflow.run(): Exporting metadata output file...")
        startTime: datetime = datetime.now()

        self.exportMetadataFrame()

        endTime: datetime = datetime.now()
        elapsedTime: timedelta = endTime - startTime

        print(f"MetadataWorkflow.run(): Workflow finished in {elapsedTime}.")
        return

    def exportMetadataFrame(self) -> None:
        outputFile: FileWrapper = self.exportFolder.getFile(
            f"metadata_gridPrefix-{self.args.gridSearchIdPrefix}.csv")

        metaDict: Dict[str, Any] = {
            "pair": self.segmentInfo.pair,
            "candleIntervalS": self.segmentInfo.candleIntervalS,
            "segmentDurationS": self.segmentInfo.segmentDurationS,
            "firstSegmentStartTime": self.segmentInfo.firstSegmentStartTime,
            "lastSegmentEndTime": self.segmentInfo.lastSegmentEndTime,
        }

        metaFrame: PD.DataFrame = PD.DataFrame.from_dict(
            metaDict, orient="index", columns=["value"])

        outputFile.saveFile_csvFrame(metaFrame)
        return
