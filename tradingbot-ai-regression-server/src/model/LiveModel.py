from array import ArrayType, array
from datetime import datetime, timedelta
import json
import numpy as NP
from typing import Dict, List, Union
from config.Logging import logger
from tensorflow.keras import mixed_precision, models

from model.ClassReturnMap import ClassReturnMap
from objects.Config import ModelConfig
from util.FileConstants import FILE_NAME_CLASSTORETURNMAP, MODEL_FOLDER_PATH


class LiveModel():
    config: ModelConfig
    returnMap: ClassReturnMap
    model: models.Sequential
    maxBatchSize: int

    def __init__(self, config: ModelConfig, maxBatchSize: int):
        self.config = config
        self.maxBatchSize = maxBatchSize

        modelFullPath: str = f"{MODEL_FOLDER_PATH}/{config.getFolderName()}"

        logger.info(
            f"LiveModel.__init__(): loading {config.outcome} model from '{modelFullPath}'...")
        self.model = models.load_model(modelFullPath)
        logger.info(
            f"LiveModel.__init__(): Model for {config.outcome} successfully loaded.")

        returnMapFullPath: str = f"{MODEL_FOLDER_PATH}/{config.getFolderName()}/{FILE_NAME_CLASSTORETURNMAP}"
        logger.info(
            f"LiveModel.__init__(): loading {config.outcome} model return map from '{returnMapFullPath}'...")

        with open(returnMapFullPath) as json_file:
            mapDictRaw: Dict = json.load(json_file)
            self.returnMap = ClassReturnMap(
                mapDict=mapDictRaw["mapDict"],
                interpolateClass=self.config.interpolateReturnMap)

        logger.info(
            f"LiveModel.__init__(): Model return map for {config.outcome} successfully loaded.")
        logger.info("")
        logger.info("")
        return

    def inferClass(self, frameMatrix: NP.ndarray) -> float:
        startTime: datetime = datetime.now()

        classPredicted_arr: ArrayType = self.model.predict(
            NP.expand_dims(frameMatrix, axis=0))
        classPredicted: float = float(classPredicted_arr[0][0])

        endTime: datetime = datetime.now()
        elapsedTime: timedelta = endTime - startTime

        logger.debug(
            f"LiveModel.inferClass(): <{self.config.outcome}> inference took {elapsedTime}.")
        return classPredicted

    def inferClass_batch(self, frameMatrixes: NP.ndarray) -> List[float]:
        startTime: datetime = datetime.now()
        result: Union[float, List[float]]

        inputBatchSize: int = min(frameMatrixes.shape[0], self.maxBatchSize)
        # limit batch size to stay below GPU memory capacity

        classesPredicted_arr: ArrayType = self.model.predict(
            frameMatrixes, batch_size=inputBatchSize)

        classesPredicted: List[float] = list(
            map(lambda a: a[0], classesPredicted_arr))
        result = classesPredicted

        endTime: datetime = datetime.now()
        elapsedTime: timedelta = endTime - startTime

        logger.debug(
            f"LiveModel.inferClass(): <{self.config.outcome}> inference (batchSize: {inputBatchSize}) took {elapsedTime}.")
        return result

    def inferReturn(self, frameMatrix: NP.ndarray) -> float:
        classPredicted: float = self.inferClass(frameMatrix)
        returnPredicted: float = self.returnMap.getReturnForClass(
            classPredicted)

        if classPredicted < 0 or classPredicted > 10:
            logger.warning(
                f"LiveModel[{self.config.outcome}].inferReturn(): Predicted return class '{classPredicted}' has been rounded down. Return %: {returnPredicted}")
        return returnPredicted

    def inferReturn_batch(self, frameMatrixes: NP.ndarray) -> float:
        classesPredicted: List[float] = self.inferClass_batch(frameMatrixes)
        returnsPredicted: List[float] = list(
            map(self.returnMap.getReturnForClass, classesPredicted))
        return returnsPredicted
