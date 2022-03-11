from typing import Any, Dict, List
from pydantic import BaseModel
import os
from objects.Models import ModelConfig, ModelLoadConfig
from util.FileConstants import MODEL_FOLDER_PATH


class AppConfig_AWS(BaseModel):
    region: str

    def __str__(self) -> str:
        return str(self.dict())


class AppConfig_Server(BaseModel):
    port: int
    disableCache: bool

    def __str__(self) -> str:
        return str(self.dict())


class AppConfig_Models(BaseModel):
    disableGpu: bool
    maxBatchSize: int  # 100 = ~ 6.6 GB VRAM usage on RTX 3060 12GB
    maxRisePct: ModelConfig
    maxDeclinePct: ModelConfig
    endChangePct: ModelConfig

    def __str__(self) -> str:
        return str(self.dict())


class AppConfig_ModelsLoad(BaseModel):
    disableGpu: bool
    maxBatchSize: int
    maxRisePctLoad: ModelLoadConfig
    maxDeclinePctLoad: ModelLoadConfig
    endChangePctLoad: ModelLoadConfig

    def toModelsConfig(self) -> AppConfig_Models:
        # modelId-1011_trainStart-2022-01-31T03.11.39_dtype-mixed_float16_outcome-maxRisePct-30c-10q_epochCt-500_frame-200x150-1s-v1.0_valLoss-1.0096
        def getModelFolderNameById(modelId: int) -> str:
            allModelFolderNames: List[str] = os.listdir(MODEL_FOLDER_PATH)
            return list(
                filter(
                    lambda f: f.startswith(f"modelId-{modelId}"),
                    allModelFolderNames))[0]

        def getModelAttributes(folderName: str) -> Dict[str, Any]:
            return {
                "trainStart": folderName.split("trainStart-")[1].split("_")[0],
                "dtype": folderName.split("dtype-")[1].split("outcome")[0][:-1],
                "outcome": folderName.split("outcome-")[1].split("_")[0].split("-")[0],
                "outcomeCandleCt": int(folderName.split("c-")[0].split("-")[-1:][0]),
                "quantileCt": int(folderName.split("q_")[0].split("-")[-1:][0]),
                "epochCt": int(folderName.split("epochCt-")[1].split("_")[0]),
                "frameSize": int(folderName.split("frame-")[1].split("x")[0]),
                "frameYaxisRes": int(folderName.split("frame-")[1].split("x")[1].split("-")[0]),
                "frameStride": int(folderName.split("-v")[0].split("-")[-1:][0].split("s")[0]),
                "cstomVersion": folderName.split("s-")[1].split("_")[0][1:],
                "valLoss": float(folderName.split("valLoss-")[1])
            }

        def buildModelConfig(loadConfig: ModelLoadConfig,
                             modelAttrs: Dict[str, Any]) -> ModelConfig:
            return ModelConfig(
                modelId=loadConfig.modelId,
                enabled=loadConfig.enabled,
                interpolateReturnMap=loadConfig.interpolateReturnMap,
                trainStart=modelAttrs["trainStart"],
                dtype=modelAttrs["dtype"],
                outcome=modelAttrs["outcome"],
                outcomeCandleCt=modelAttrs["outcomeCandleCt"],
                quantileCt=modelAttrs["quantileCt"],
                epochCt=modelAttrs["epochCt"],
                frameSize=modelAttrs["frameSize"],
                frameYaxisRes=modelAttrs["frameYaxisRes"],
                frameStride=modelAttrs["frameStride"],
                cstomVersion=modelAttrs["cstomVersion"],
                valLoss=modelAttrs["valLoss"]
            )

        modelAttrs_maxRisePct: Dict[str, Any] = getModelAttributes(
            getModelFolderNameById(self.maxRisePctLoad.modelId))
        modelConfig_maxRisePct: ModelConfig = buildModelConfig(
            self.maxRisePctLoad, modelAttrs_maxRisePct)

        modelAttrs_maxDeclinePct: Dict[str, Any] = getModelAttributes(
            getModelFolderNameById(self.maxDeclinePctLoad.modelId))
        modelConfig_maxDeclinePct: ModelConfig = buildModelConfig(
            self.maxDeclinePctLoad, modelAttrs_maxDeclinePct)

        modelAttrs_endChangePct: Dict[str, Any] = getModelAttributes(
            getModelFolderNameById(self.endChangePctLoad.modelId))
        modelConfig_endChangePct: ModelConfig = buildModelConfig(
            self.endChangePctLoad, modelAttrs_endChangePct)

        modelsConfig: AppConfig_Models = AppConfig_Models(
            disableGpu=self.disableGpu, maxBatchSize=self.maxBatchSize,
            maxRisePct=modelConfig_maxRisePct,
            maxDeclinePct=modelConfig_maxDeclinePct,
            endChangePct=modelConfig_endChangePct)

        return modelsConfig

    def __str__(self) -> str:
        return str(self.dict())


class AppConfig(BaseModel):
    aws: AppConfig_AWS
    server: AppConfig_Server
    # models: AppConfig_Models
    modelsLoad: AppConfig_ModelsLoad

    def __str__(self) -> str:
        return str(self.dict())
