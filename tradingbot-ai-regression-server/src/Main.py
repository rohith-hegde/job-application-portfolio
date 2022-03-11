import os
from typing import List
import uvicorn
from config.Loader import loadAppConfig, parseAppConfig
from objects.Config import AppConfig
from util.InputOutput import downloadS3bucket_modelFolder, downloadS3bucket_models

if __name__ == "__main__":
    print(f"Starting AI server in working directory '{os.getcwd()}'...")
    appConfig: AppConfig = loadAppConfig()

    print(f"Downloading AI models from S3 bucket...")

    modelIdsToLoad: List[int] = [
        appConfig.modelsLoad.maxRisePctLoad.modelId,
        appConfig.modelsLoad.maxDeclinePctLoad.modelId,
        appConfig.modelsLoad.endChangePctLoad.modelId,
    ]

    '''for modelId in modelIdsToLoad:
        print(f"\tDownloading AI model with ID '{modelId}'...")
        downloadS3bucket_modelFolder(modelId)'''
    downloadS3bucket_models()

    print()
    print("Starting AI server API...")

    uvicorn.run("server.Controller:webapp",
                host="0.0.0.0", port=appConfig.server.port)

    print("AI server API terminated.")
