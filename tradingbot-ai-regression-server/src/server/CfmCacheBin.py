
import hashlib
from typing import Dict, List, Union
from fastapi import Request
from objects.Server import DPredictionType, DReturnPredictions, DReturnPredictionsBatch


class CfmCacheBin():
    cacheDict: Dict[str, Union[DReturnPredictions, DReturnPredictionsBatch]]
    disabled: bool

    def __init__(self, disabled: bool = False):
        self.cacheDict = {}
        self.disabled = disabled
        return

    def __call__(self, request: Request):
        request.state.cfmCacheBin = self

    def getRequestKey(self, binaryPickle: bytes, predictionTypes:
                      List[DPredictionType]) -> str:
        frameMatrixPickle: str = hashlib.sha1(binaryPickle).hexdigest()

        key: str = f"{frameMatrixPickle}_{str(predictionTypes)}"
        return key

    def get(self, binaryPickle: bytes, predictionTypes: List[DPredictionType]) -> Union[Union[DReturnPredictions, DReturnPredictionsBatch], None]:
        if self.disabled:
            return None

        reqKey: str = self.getRequestKey(binaryPickle, predictionTypes)
        value: Union[Union[DReturnPredictions, DReturnPredictionsBatch],
                     None] = self.cacheDict.get(reqKey)
        return value

    def put(
            self, binaryPickle: bytes, predictionTypes: List[DPredictionType],
            result: Union[DReturnPredictions, DReturnPredictionsBatch]) -> None:
        if self.disabled:
            return

        reqKey: str = self.getRequestKey(binaryPickle, predictionTypes)
        self.cacheDict[reqKey] = result
        return
