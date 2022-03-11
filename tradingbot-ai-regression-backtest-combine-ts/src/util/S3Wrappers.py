from bisect import bisect_left
from datetime import datetime, timedelta
import os
from os.path import exists
from pathlib import Path
from typing import Any, List
import boto3

AWS_REGION: str = "us-east-2"
S3_BACKTEST_INPUT_BUCKET_NAME: str = "[censored]-backtest-combine-output-dev"
S3_COINGECKO_INPUT_BUCKET_NAME: str = "[censored]-coingecko-history-dev"
S3_OUTPUT_BUCKET_NAME: str = "[censored]-backtest-combine-ts-output-dev"


class S3Wrapper_Down():
    fileTypeFilters: List[str]
    fileFilters: List[str]
    localFolderPath: str
    awsRegion: str
    bucketName: str
    bucketClient: Any
    s3Client: Any

    def __init__(
            self, awsRegion: str, bucketName: str, localFolderPath: str,
            fileTypeFilters: List[str], fileFilters: List[str] = None):
        self.fileTypeFilters = fileTypeFilters
        self.awsRegion = awsRegion
        self.fileFilters = fileFilters
        self.localFolderPath = localFolderPath
        self.bucketName = bucketName

        s3Client: Any = boto3.resource("s3", region_name=awsRegion)
        self.s3Client = s3Client
        self.bucketClient = s3Client.Bucket(bucketName)
        return

    def s3ObjectInFileTypeFilters(self, obj: Any) -> bool:
        presentInAny: bool = False

        for fileTypeFilter in self.fileTypeFilters:
            if fileTypeFilter in str(obj.key):
                presentInAny = True

        return presentInAny

    def listObjects_filtered(self) -> List[Any]:
        filteredObjs: List[Any] = []

        # inefficient, but more complete
        allObjs: List[Any] = list(self.bucketClient.objects.all())

        for fileFilter in self.fileFilters:
            objsMatchingFilters: List[Any] = list(
                filter(
                    lambda o: fileFilter
                    in str(o.key) and self.s3ObjectInFileTypeFilters(o),
                    allObjs))

            filteredObjs.extend(objsMatchingFilters)

        return filteredObjs

    def downloadObjects_filtered(self) -> List[str]:
        filteredObjs: List[Any] = self.listObjects_filtered()
        filePathsDownloaded: List[str] = []
        startTime: datetime = datetime.now()

        for filteredObj in filteredObjs:
            downloadObj: Any = self.s3Client.Object(
                self.bucketName, filteredObj.key)

            objFullFilePath_local: str = f"{self.localFolderPath}/{filteredObj.key}"
            filePathsDownloaded.append(objFullFilePath_local)

            os.makedirs(os.path.split(objFullFilePath_local)[0], exist_ok=True)
            if not exists(objFullFilePath_local):
                downloadObj.download_file(objFullFilePath_local)

        endTime: datetime = datetime.now()
        elapsedTime: timedelta = endTime - startTime

        print(
            f"S3Wrapper_Down.downloadObjects_filtered(): Successfully downloaded {len(filteredObjs)} objects with filename filters '{self.fileFilters}' from S3 bucket {self.bucketName} in {elapsedTime}.")
        return filePathsDownloaded


class S3Wrapper_Up():
    localFolderPath: str
    awsRegion: str
    bucketName: str
    s3Client: Any

    def __init__(
            self, awsRegion: str, bucketName: str, localFolderPath: str):
        self.awsRegion = awsRegion
        self.localFolderPath = localFolderPath
        self.bucketName = bucketName

        s3Client: Any = boto3.client("s3", region_name=awsRegion)
        self.s3Client = s3Client
        return

    def uploadFile(self, fileName: str) -> None:
        fullFilePath_local: str = f"{self.localFolderPath}/{fileName}"
        startTime: datetime = datetime.now()

        self.s3Client.upload_file(
            fullFilePath_local, self.bucketName, fileName)

        endTime: datetime = datetime.now()
        elapsedTime: timedelta = endTime - startTime

        print(
            f"S3Wrapper_Up.uploadFile(): Successfully uploaded file '{fileName}' to S3 bucket {self.bucketName} in {elapsedTime}.")
        return

    def uploadSubFolder(self, subFolderName: str) -> None:
        subFolderPath: str = f"{self.localFolderPath}/{subFolderName}"
        startTime: datetime = datetime.now()

        for fileName in os.listdir(subFolderPath):
            fullFilePath_local: str = f"{subFolderPath}/{fileName}"
            s3ObjPath: str = f"{subFolderName}/{fileName}"

            self.s3Client.upload_file(
                fullFilePath_local, self.bucketName, s3ObjPath)

        endTime: datetime = datetime.now()
        elapsedTime: timedelta = endTime - startTime

        print(
            f"S3Wrapper_Up.uploadFile(): Successfully uploaded subfolder '{subFolderName}' to S3 bucket {self.bucketName} in {elapsedTime}.")
        return


# Credit goes to: https://stackoverflow.com/a/56892500
class S3Wrapper_Sync:
    """
    Class that holds the operations needed for synchronize local dirs to a given bucket.
    """

    def __init__(
            self, awsRegion: str, bucketName: str, localFolderPath: str):
        self.awsRegion = awsRegion
        self.localFolderPath = localFolderPath
        self.bucketName = bucketName

        s3Client: Any = boto3.client("s3", region_name=awsRegion)
        self._s3 = s3Client
        return

    def sync(self):
        """
        Sync source to dest, this means that all elements existing in
        source that not exists in dest will be copied to dest.

        No element will be deleted.

        :param source: Source folder.
        :param dest: Destination folder.

        :return: None
        """
        source = self.localFolderPath
        dest = self.bucketName

        paths = self.list_source_objects(source_folder=source)
        objects = self.list_bucket_objects(dest)

        # Getting the keys and ordering to perform binary search
        # each time we want to check if any paths is already there.
        object_keys = [obj['Key'] for obj in objects]
        object_keys.sort()
        object_keys_length = len(object_keys)

        for path in paths:
            # Binary search.
            index = bisect_left(object_keys, path)
            if index == object_keys_length:
                # If path not found in object_keys, it has to be sync-ed.
                self._s3.upload_file(
                    str(Path(source).joinpath(path)),
                    Bucket=dest, Key=path)

    def list_bucket_objects(self, bucket: str):
        """
        List all objects for the given bucket.

        :param bucket: Bucket name.
        :return: A [dict] containing the elements in the bucket.

        Example of a single object.

        {
            'Key': 'example/example.txt',
            'LastModified': datetime.datetime(2019, 7, 4, 13, 50, 34, 893000, tzinfo=tzutc()),
            'ETag': '"b11564415be7f58435013b414a59ae5c"',
            'Size': 115280,
            'StorageClass': 'STANDARD',
            'Owner': {
                'DisplayName': 'webfile',
                'ID': '75aa57f09aa0c8caeab4f8c24e99d10f8e7faeebf76c078efc7c6caea54ba06a'
            }
        }

        """
        try:
            contents = self._s3.list_objects(Bucket=bucket)['Contents']
        except KeyError:
            # No Contents Key, empty bucket.
            return []
        else:
            return contents

    @staticmethod
    def list_source_objects(source_folder: str):
        """
        :param source_folder:  Root folder for resources you want to list.
        :return: A [str] containing relative names of the files.

        Example:

            /tmp
                - example
                    - file_1.txt
                    - some_folder
                        - file_2.txt

            >>> sync.list_source_objects("/tmp/example")
            ['file_1.txt', 'some_folder/file_2.txt']

        """

        path = Path(source_folder)

        paths = []

        for file_path in path.rglob("*"):
            if file_path.is_dir():
                continue
            str_file_path = str(file_path)
            str_file_path = str_file_path.replace(f'{str(path)}/', "")
            paths.append(str_file_path)

        return paths
