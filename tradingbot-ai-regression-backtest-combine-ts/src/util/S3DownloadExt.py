
from typing import List

from util.FileWrappers import BACKTEST_INPUT_LOCAL_PATH, FileWrapper, FolderWrapper
from util.S3Wrappers import AWS_REGION, S3_BACKTEST_INPUT_BUCKET_NAME, S3Wrapper_Down


def downloadGridInputBucketFiles(
        gridSearchPrefix: int, fileTypeFilter: str) -> List[FileWrapper]:
    downloadFolderFilter: str = f"gridSearchId-{gridSearchPrefix}"
    downloadFileTypeFilters: List[str] = [fileTypeFilter]

    s3Down: S3Wrapper_Down = S3Wrapper_Down(
        awsRegion=AWS_REGION, bucketName=S3_BACKTEST_INPUT_BUCKET_NAME,
        localFolderPath=BACKTEST_INPUT_LOCAL_PATH,
        fileTypeFilters=downloadFileTypeFilters,
        fileFilters=[downloadFolderFilter])

    def parseFileName(filePath: str) -> str:
        fileParts: List[str] = filePath.split("/")
        return fileParts[len(fileParts) - 1]

    downloadedFilePaths: List[str] = s3Down.downloadObjects_filtered()
    downloadedFileNames: List[str] = list(
        map(parseFileName, downloadedFilePaths))

    folder: FolderWrapper = FolderWrapper(BACKTEST_INPUT_LOCAL_PATH)
    downloadedFiles: List[FileWrapper] = list(
        map(lambda fn: folder.getFile(fn), downloadedFileNames))

    return downloadedFiles


def getSegmentIdFromFile(file: FileWrapper) -> int:
    nameParts_segmentId: List[str] = file.filePath.split("segmentId-")
    nameParts_gridSearchId: List[str] = nameParts_segmentId[1].split(
        "_gridSearchId")
    segmentId: int = int(nameParts_gridSearchId[0])
    return segmentId
