from datetime import datetime, timedelta
from util.FileWrappers import OUTPUT_LOCAL_PATH, FolderWrapper
from util.S3Wrappers import AWS_REGION, S3_OUTPUT_BUCKET_NAME, S3Wrapper_Sync, S3Wrapper_Up


class UploadOutputWorkflow():
    exportFolder: FolderWrapper

    def __init__(self, exportFolder: FolderWrapper):
        self.exportFolder = exportFolder
        return

    def run(self) -> None:
        print(
            f"UploadOutputWorkflow.run(): Uploading timeseries output folder '{self.exportFolder.folderPath}' to S3 bucket...")
        startTime: datetime = datetime.now()

        self.uploadToBucket()

        endTime: datetime = datetime.now()
        elapsedTime: timedelta = endTime - startTime

        print(
            f"UploadOutputWorkflow.run(): Workflow finished in {elapsedTime}.")
        return

    def uploadToBucket(self) -> None:
        wrapper: S3Wrapper_Up = S3Wrapper_Up(
            awsRegion=AWS_REGION, bucketName=S3_OUTPUT_BUCKET_NAME,
            localFolderPath=OUTPUT_LOCAL_PATH)

        wrapper.uploadSubFolder(self.exportFolder.getFolderName())
        return
