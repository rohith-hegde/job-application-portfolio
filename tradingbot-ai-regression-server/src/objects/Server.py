
from enum import Enum
import numpy as NP
from typing import Any, List, Optional
from pydantic import BaseModel
from tradingbot_py_common_classes.src.candles.CandleFramePairXY import CandleFrame


class DReturnPredictions(BaseModel):
    maxRisePct: Optional[float]  # should be positive
    maxDeclinePct: Optional[float]  # should be positive
    endChangePct: Optional[float]  # can be positive or negative

    def __str__(self):
        return self.json()


class DReturnPredictionsBatch(BaseModel):
    maxRisePct: Optional[List[float]]  # should be positive
    maxDeclinePct: Optional[List[float]]  # should be positive
    endChangePct: Optional[List[float]]  # can be positive or negative

    def __str__(self):
        return self.json()


class DPredictCandleFrameResp(BaseModel):
    predictions: DReturnPredictions

    def __str__(self):
        return self.json()


class DPredictCandleFrameRespBatch(BaseModel):
    batchPredictions: DReturnPredictionsBatch

    def __str__(self):
        return self.json()


##################################################################


class DPredictionType(str, Enum):
    maxRisePct = "maxRisePct"
    maxDeclinePct = "maxDeclinePct"
    endChangePct = "endChangePct"


class DPredictCandleFrameReq(BaseModel):
    # dimensions: (frameSize, frameYaxisRes)
    frame: Any  # CandleFrame
    predictionTypes: List[DPredictionType]

    def __str__(self):
        return self.json()


class DPredictCandleFrameMatrixReq(BaseModel):
    # dimensions: (frameSize, frameYaxisRes, channels = 3)
    frameMatrix: Any  # JSON array str or NP.ndarray(3)
    predictionTypes: List[DPredictionType]

    def __str__(self):
        return self.json()


class DPredictCandleFrameMatrixReqBatch(BaseModel):
    # dimensions: (frames, frameSize, frameYaxisRes, channels = 3)
    frameMatrixes: Any  # JSON array str or NP.ndarray(4)
    predictionTypes: List[DPredictionType]

    def __str__(self):
        return self.json()
