
import json
from typing import Dict
from objects.Config import AppConfig, AppConfig_AWS, AppConfig_ModelsLoad, AppConfig_Server, ModelConfig, AppConfig_Models
from objects.Models import ModelLoadConfig
from util.FileConstants import CONFIG_FILE_PATH
from config.Logging import logger


def loadAppConfig() -> AppConfig:
    appConfig: AppConfig
    logger.info("Loading configuration file...")

    with open(CONFIG_FILE_PATH) as json_file:
        appConfig_dict: Dict = json.load(json_file)
        appConfig = parseAppConfig(appConfig_dict)

    logger.info("Loaded configuration file '%s'", json_file.name)
    return appConfig


##################### this should've been replaced with a library for managing configuration files ######################


def parseAppConfig(configDict: Dict) -> AppConfig:
    aws_dict: Dict = configDict["aws"]
    config_aws: AppConfig_AWS = AppConfig_AWS(region=aws_dict["region"])

    server_dict: Dict = configDict["server"]
    config_server: AppConfig_Server = AppConfig_Server(
        port=server_dict["port"],
        disableCache=server_dict["disableCache"])

    models_dict: Dict = configDict["modelsLoad"]
    config_modelsLoad: AppConfig_ModelsLoad = parseModelsLoadConfig(models_dict)

    config: AppConfig = AppConfig(
        aws=config_aws, server=config_server, modelsLoad=config_modelsLoad)
    return config


def parseModelsLoadConfig(modelsLoad_dict: Dict) -> AppConfig_ModelsLoad:
    modelsLoad_disableGpu: Dict = modelsLoad_dict["disableGpu"]
    modelsLoad_maxBatchSize: Dict = modelsLoad_dict["maxBatchSize"]

    modelLoad_dict_maxRisePct: Dict = modelsLoad_dict["maxRisePct"]
    modelLoad_dict_maxDeclinePct: Dict = modelsLoad_dict["maxDeclinePct"]
    modelLoad_dict_endChangePct: Dict = modelsLoad_dict["endChangePct"]

    modelLoad_maxRisePct: ModelLoadConfig = parseModelLoadConfig(
        modelLoad_dict_maxRisePct)
    modelLoad_maxDeclinePct: ModelLoadConfig = parseModelLoadConfig(
        modelLoad_dict_maxDeclinePct)
    modelLoad_endChangePct: ModelLoadConfig = parseModelLoadConfig(
        modelLoad_dict_endChangePct)

    config_modelsLoad: AppConfig_ModelsLoad = AppConfig_ModelsLoad(
        maxRisePctLoad=modelLoad_maxRisePct,
        maxDeclinePctLoad=modelLoad_maxDeclinePct,
        endChangePctLoad=modelLoad_endChangePct,
        disableGpu=modelsLoad_disableGpu, maxBatchSize=modelsLoad_maxBatchSize)

    return config_modelsLoad


def parseModelLoadConfig(modelDict: Dict) -> ModelLoadConfig:
    config_modelLoad: ModelLoadConfig = ModelLoadConfig(
        modelId=modelDict["modelId"],
        enabled=modelDict["enabled"],
        interpolateReturnMap=modelDict["interpolateReturnMap"],
    )

    return config_modelLoad
