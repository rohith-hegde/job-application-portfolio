from datetime import datetime, timedelta
import json
import pandas as PD
from typing import Any, Dict, List, Union, Tuple
from Models import InputArgs, SegmentInfo
from util.FileWrappers import FolderWrapper
from util.InputParse import parseEventDict
from workflows.CoinGeckoWorkflow import CoinGeckoWorkflow
from workflows.GridSummariesWorkflow import GridSummariesWorkflow
from workflows.MetadataWorkflow import MetadataWorkflow
from workflows.SegmentResultsWorkflow import SegmentResultsWorkflow
from workflows.TimeSeriesWorkflow import TimeSeriesWorkflow
from workflows.UploadOutputWorkflow import UploadOutputWorkflow

EXAMPLE_EVENT: Dict[str, str] = {
    "gridSearchIdPrefix": 771235  # 170296,
}

LOCAL_HANDLER_EVENT: Dict = EXAMPLE_EVENT
LOCAL_HANDLER_CONTEXT: Dict = {}


def lambda_handler(event: Dict[str, Any], context: Dict[str, Any]):
    print(f"Called with input event: {event}")
    args: InputArgs = parseEventDict(event)
    print(f"Called with input arguments: {args}")

    startTime: datetime = datetime.now()

    srWorkflow: SegmentResultsWorkflow = SegmentResultsWorkflow(args)
    srWorkflowResult: Tuple[SegmentInfo, List[PD.DataFrame]] = srWorkflow.run()

    segmentInfo: SegmentInfo = srWorkflowResult[0]
    segmentResults: List[PD.DataFrame] = srWorkflowResult[1]

    cgWorkflow: CoinGeckoWorkflow = CoinGeckoWorkflow(args, segmentInfo)
    geckoFrame: PD.DataFrame = cgWorkflow.run()

    gsWorkflow: GridSummariesWorkflow = GridSummariesWorkflow(
        args=args, segmentInfo=segmentInfo)
    segmentGridSummaries: List[Tuple[int, PD.DataFrame]] = gsWorkflow.run()

    tsWorkflow: TimeSeriesWorkflow = TimeSeriesWorkflow(
        args=args, segmentInfo=segmentInfo, geckoFrame=geckoFrame,
        segmentGridSummaries=segmentGridSummaries)
    exportFolder: FolderWrapper = tsWorkflow.run()

    mdWorkflow: MetadataWorkflow = MetadataWorkflow(
        args=args, segmentInfo=segmentInfo, exportFolder=exportFolder)
    mdWorkflow.run()

    uoWorkflow: UploadOutputWorkflow = UploadOutputWorkflow(exportFolder)
    uoWorkflow.run()

    endTime: datetime = datetime.now()
    elapsedTime: timedelta = endTime - startTime
    print(
        f"lambda_handler(): Finished entire timeseries combine process in {elapsedTime}.")

    print(
        f"Export folder name: {exportFolder.getFolderName()}")

    return {
        'statusCode': 200,
        'body': {
            "exportFolderName": exportFolder.getFolderName()
        }
    }


if __name__ == '__main__':  # for local debugging only
    lambda_handler(event=LOCAL_HANDLER_EVENT, context=LOCAL_HANDLER_CONTEXT)
