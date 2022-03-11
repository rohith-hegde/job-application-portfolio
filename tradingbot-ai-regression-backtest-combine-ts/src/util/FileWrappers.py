
import json
import os
from typing import Any, Dict, List, Union
import pandas as PD

env_workDir: str = os.environ.get('WORKING_DIRECTORY')
WORKING_DIRECTORY_LOCAL_PATH: str = env_workDir if env_workDir else os.getcwd()

BACKTEST_INPUT_LOCAL_PATH: str = f"{WORKING_DIRECTORY_LOCAL_PATH}/input_backtest"
COINGECKO_INPUT_LOCAL_PATH: str = f"{WORKING_DIRECTORY_LOCAL_PATH}/input_coingecko"
OUTPUT_LOCAL_PATH: str = f"{WORKING_DIRECTORY_LOCAL_PATH}/output"


class FileWrapper():
    filePath: str

    def __init__(self, filePath: str):
        self.filePath = filePath
        return

    def getFileName(self) -> str:
        filePathParts: List[str] = self.filePath.split("/")
        return filePathParts[len(filePathParts) - 1]

    def loadFile_csvFrame(self) -> PD.DataFrame:
        return PD.read_csv(self.filePath)

    def saveFile_csvFrame(self, data: PD.DataFrame) -> None:
        os.makedirs(os.path.split(self.filePath)[0], exist_ok=True)
        data.to_csv(self.filePath)
        return


class FolderWrapper():
    folderPath: str

    def __init__(self, folderPath: str):
        if folderPath.endswith('/'):
            folderPath = folderPath[:-1]
        self.folderPath = folderPath
        return

    def getFolderName(self) -> str:
        folderPathParts: List[str] = self.folderPath.split("/")
        return folderPathParts[len(folderPathParts) - 1]

    def getFile(self, fileName: str) -> FileWrapper:
        filePath: str = f"{self.folderPath}{'/'}{fileName}"
        fileObj: FileWrapper = FileWrapper(filePath=filePath)
        return fileObj

    def getSubfolder(self, folderName: str) -> Any:
        folderPath: str = f"{self.folderPath}{'/'}{folderName}"
        folderObj: FolderWrapper = FolderWrapper(folderPath=folderPath)
        return folderObj
