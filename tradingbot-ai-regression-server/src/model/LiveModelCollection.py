
import asyncio
import os
from typing import Any, Dict, List, Tuple
from config.Logging import logger

from model.LiveModel import LiveModel
from objects.Config import AppConfig_Models
from objects.Server import DPredictionType, DReturnPredictions, DReturnPredictionsBatch
from util.Async import async_wrap
import numpy as NP
import tensorflow as TF
from fastapi import Request


class LiveModelCollection():
    modelsConfig: AppConfig_Models
    model_maxRisePct: LiveModel
    model_maxDeclinePct: LiveModel
    model_endChangePct: LiveModel

    def __init__(self, modelsConfig: AppConfig_Models):
        self.modelsConfig = modelsConfig
        if (modelsConfig.disableGpu):
            logger.info(
                "LiveModelCollection.__init__(): Disabling CUDA GPU acceleration. CPU performance may be slow.")
            os.environ['CUDA_VISIBLE_DEVICES'] = '-1'
        else:
            gpus = TF.config.experimental.list_physical_devices('GPU')
            for gpu in gpus:
                TF.config.experimental.set_memory_growth(gpu, True)

        self.model_maxRisePct = LiveModel(
            modelsConfig.maxRisePct, modelsConfig.maxBatchSize)
        self.model_maxDeclinePct = LiveModel(
            modelsConfig.maxDeclinePct, modelsConfig.maxBatchSize)
        self.model_endChangePct = LiveModel(
            modelsConfig.endChangePct, modelsConfig.maxBatchSize)
        return

    def __call__(self, request: Request):
        request.state.modelCollection = self

    async def inferReturns_multiple(self, frameMatrix: NP.ndarray, predictionTypes: List[DPredictionType]) -> DReturnPredictions:
        async def inferReturnWithOutcome(model: LiveModel) -> Tuple[str, float]:
            infer_async = async_wrap(model.inferReturn)
            returnPrediction: float = await infer_async(frameMatrix)
            return [model.config.outcome, returnPrediction]

        inferCoroutines: List[Any] = []

        if DPredictionType.maxRisePct in predictionTypes:
            inferCoroutines.append(
                inferReturnWithOutcome(
                    self.model_maxRisePct))

        if DPredictionType.maxDeclinePct in predictionTypes:
            inferCoroutines.append(
                inferReturnWithOutcome(
                    self.model_maxDeclinePct))

        if DPredictionType.endChangePct in predictionTypes:
            inferCoroutines.append(
                inferReturnWithOutcome(
                    self.model_endChangePct))

        inferResults: List[Tuple[str, float]] = await asyncio.gather(*inferCoroutines)
        predictions: DReturnPredictions = DReturnPredictions()

        for inferResult in inferResults:
            if inferResult[0] == DPredictionType.maxRisePct:
                predictions.maxRisePct = inferResult[1]
            elif inferResult[0] == DPredictionType.maxDeclinePct:
                predictions.maxDeclinePct = inferResult[1]
            elif inferResult[0] == DPredictionType.endChangePct:
                predictions.endChangePct = inferResult[1]

        return predictions

    async def inferReturns_multiple_batch(self, frameMatrixes: NP.ndarray, predictionTypes: List[DPredictionType]) -> DReturnPredictionsBatch:
        async def inferReturnWithOutcome_batch(model: LiveModel) -> Tuple[str, List[float]]:
            infer_async = async_wrap(model.inferReturn_batch)
            returnPredictions: List[float] = await infer_async(frameMatrixes)
            return [model.config.outcome, returnPredictions]

        inferCoroutines: List[Any] = []

        if DPredictionType.maxRisePct in predictionTypes:
            inferCoroutines.append(
                inferReturnWithOutcome_batch(
                    self.model_maxRisePct))

        if DPredictionType.maxDeclinePct in predictionTypes:
            inferCoroutines.append(
                inferReturnWithOutcome_batch(
                    self.model_maxDeclinePct))

        if DPredictionType.endChangePct in predictionTypes:
            inferCoroutines.append(
                inferReturnWithOutcome_batch(
                    self.model_endChangePct))

        inferResults: List[Tuple[str, List[float]]] = await asyncio.gather(*inferCoroutines)
        predictions: DReturnPredictionsBatch = DReturnPredictionsBatch()

        for inferResult in inferResults:
            if inferResult[0] == DPredictionType.maxRisePct:
                predictions.maxRisePct = inferResult[1]
            elif inferResult[0] == DPredictionType.maxDeclinePct:
                predictions.maxDeclinePct = inferResult[1]
            elif inferResult[0] == DPredictionType.endChangePct:
                predictions.endChangePct = inferResult[1]

        return predictions
