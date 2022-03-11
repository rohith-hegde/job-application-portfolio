from typing import List
from tradingbot_py_common_classes.src.util.Shell import runSilentShellScript


def downloadS3bucket_models() -> None:
    scriptFileName: str = "downloadS3Bucket_models_silent.sh"
    executeS3shellScript(scriptFileName)


def downloadS3bucket_modelFolder(modelId: int) -> None:
    scriptFileName: str = "downloadS3Bucket_models_silent.sh"
    modelFolderNameSuffixFilter: str = f"modelId-{modelId}_"
    executeS3shellScript(scriptFileName, modelFolderNameSuffixFilter)


def executeS3shellScript(
        scriptFileName: str, fileNameSuffixFilter: str = None) -> None:
    if not fileNameSuffixFilter:
        runSilentShellScript(scriptFileName)
    else:
        awsCli_fileNameFilterArgs: List[str] = [f"{fileNameSuffixFilter}*"]
        runSilentShellScript(scriptFileName, awsCli_fileNameFilterArgs)
