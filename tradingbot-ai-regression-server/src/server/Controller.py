import json
from typing import Any, List
from fastapi import FastAPI, Request, UploadFile, File
from fastapi.exceptions import HTTPException
import numpy as NP
from io import BytesIO
from datetime import datetime, timedelta
from fastapi.params import Depends
from config.Injectable import AppConfig_Injectable
from config.Logging import logger
from config.Loader import loadAppConfig
from model.LiveModelCollection import LiveModelCollection
from objects.Server import DPredictCandleFrameMatrixReq, DPredictCandleFrameMatrixReqBatch, DPredictCandleFrameReq, DPredictCandleFrameResp, DPredictCandleFrameRespBatch, DPredictionType, DReturnPredictions, DReturnPredictionsBatch
from server.CfmCache import CfmCache
from server.CfmCacheBin import CfmCacheBin
from server.CfmStrInputParse import parseCfmInput
from server.OpenAPI import custom_openapi
from tradingbot_py_common_classes.src.matrixes.MatrixConversion import convertCandleFrameToMatrix


webapp: FastAPI = FastAPI(
    dependencies=[
        Depends(AppConfig_Injectable),
        Depends(LiveModelCollection(loadAppConfig().modelsLoad.toModelsConfig())),
        Depends(CfmCache(loadAppConfig().server.disableCache)),
        Depends(CfmCacheBin(loadAppConfig().server.disableCache)),
    ]
)

webapp.openapi = lambda: custom_openapi(webapp)

logger.info("Controller: Initialized FastAPI controller with dependencies.")
logger.info("")


# Predicts one candlestick chart frame
@webapp.post("/predictOutcomeCFM_bin",
             response_model=DPredictCandleFrameResp)
async def predictCandleFrameMatrixOutcome(request: Request, pickle: UploadFile = File(...)):
    startTime: datetime = datetime.now()
    modelCollection: LiveModelCollection = request.state._state['modelCollection']
    cacheBin: CfmCacheBin = request.state._state['cfmCacheBin']

    allPredictionTypes: List[DPredictionType] = [
        DPredictionType.maxRisePct,
        DPredictionType.maxDeclinePct,
        DPredictionType.endChangePct
    ]
    predictions: DReturnPredictions

    pickleBytes: bytes
    frameMatrix: NP.ndarray

    try:
        pickleBytes = await pickle.read()

        if not isinstance(pickleBytes, bytes):
            raise ValueError(
                "Only a binary form file as a pickled NumPy ndarray (3 dimensions) is supported")

        frameMatrix = NP.load(BytesIO(pickleBytes), allow_pickle=True)
    except Exception as e:
        logger.warning("Error parsing /predictOutcomeCFM_bin request", e)
        raise HTTPException(
            status_code=400, detail=f"Error with request data: {e.args}")

    try:
        predictions = cacheBin.get(pickleBytes, allPredictionTypes)
        cacheMiss: bool = False

        if not predictions:
            cacheMiss = True
            predictions = await modelCollection.inferReturns_multiple(frameMatrix, allPredictionTypes)

            cacheBin.put(binaryPickle=pickleBytes,
                         predictionTypes=allPredictionTypes,
                         result=predictions)

        response: DPredictCandleFrameResp = DPredictCandleFrameResp(
            predictions=predictions)

        endTime: datetime = datetime.now()
        elapsedTime: timedelta = endTime - startTime

        logger.info(
            f"Completed predictOutcomeCFM_bin request (cache miss: {cacheMiss}) in {elapsedTime}.")
        return response
    except Exception as e:
        logger.warning("Error processing /predictOutcomeCFM_bin request", e)
        raise HTTPException(
            status_code=500, detail=f"Error running inference: {e.args}")


# Predicts multiple candlestick chart frames
@webapp.post("/predictOutcomeCFM_batch_bin",
             response_model=DPredictCandleFrameRespBatch)
async def predictCandleFrameMatrixOutcomeBatch(request: Request, pickle: UploadFile = File(...)):
    startTime: datetime = datetime.now()
    modelCollection: LiveModelCollection = request.state._state['modelCollection']

    cacheBin: CfmCacheBin = request.state._state['cfmCacheBin']
    batchPredictions: DReturnPredictionsBatch

    allPredictionTypes: List[DPredictionType] = [
        DPredictionType.maxRisePct,
        DPredictionType.maxDeclinePct,
        DPredictionType.endChangePct
    ]

    pickleBytes: bytes
    frameMatrixes: NP.ndarray

    try:
        pickleBytes = await pickle.read()

        if not isinstance(pickleBytes, bytes):
            raise ValueError(
                "Only a binary form file as a pickled NumPy ndarray (4 dimensions) is supported")

        frameMatrixes = NP.load(BytesIO(pickleBytes), allow_pickle=True)
    except Exception as e:
        logger.warning("Error parsing /predictOutcomeCFM_bin_batch request", e)
        raise HTTPException(
            status_code=400, detail=f"Error with request data: {e.args}")

    try:
        batchPredictions = cacheBin.get(pickleBytes, allPredictionTypes)
        cacheMiss: bool = False

        if not batchPredictions:
            cacheMiss = True
            batchPredictions = await modelCollection.inferReturns_multiple_batch(frameMatrixes, allPredictionTypes)

            cacheBin.put(binaryPickle=pickleBytes,
                         predictionTypes=allPredictionTypes,
                         result=batchPredictions)

        response: DPredictCandleFrameRespBatch = DPredictCandleFrameRespBatch(
            batchPredictions=batchPredictions)

        endTime: datetime = datetime.now()
        elapsedTime: timedelta = endTime - startTime

        logger.info(
            f"Completed predictOutcomeCFM_bin_batch request (cache miss: {cacheMiss}) in {elapsedTime}.")
        return response
    except Exception as e:
        logger.warning(
            "Error processing /predictOutcomeCFM_bin_batch request", e)
        raise HTTPException(
            status_code=500, detail=f"Error running inference: {e.args}")
