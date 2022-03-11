from pydantic import BaseModel


class ModelLoadConfig(BaseModel):
    modelId: int
    enabled: bool
    interpolateReturnMap: bool

    def __str__(self) -> str:
        return str(self.dict())


class ModelConfig(BaseModel):
    modelId: int
    enabled: bool
    interpolateReturnMap: bool
    trainStart: str
    dtype: str
    outcome: str
    outcomeCandleCt: int
    quantileCt: int
    epochCt: int
    frameSize: int
    frameYaxisRes: int
    frameStride: int
    cstomVersion: str
    valLoss: float

    def __str__(self) -> str:
        return str(self.dict())

    def getFolderName(self) -> str:
        roundedValLoss: float = round(self.valLoss, 4)
        folderName: str = f"modelId-{self.modelId}_trainStart-{self.trainStart}_dtype-{self.dtype}_"
        folderName += f"outcome-{self.outcome}-{self.outcomeCandleCt}c-"
        folderName += f"{self.quantileCt}q_epochCt-{self.epochCt}_frame-"
        folderName += f"{self.frameSize}x{self.frameYaxisRes}-"
        folderName += f"{self.frameStride}s-v{self.cstomVersion}_valLoss-{roundedValLoss}"
        return folderName
