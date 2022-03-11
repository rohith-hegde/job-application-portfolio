from datetime import datetime
from typing import Dict, List, Union
from pydantic import BaseModel


class InputArgs(BaseModel):
    gridSearchIdPrefix: int


class SegmentInfo(BaseModel):
    pair: str
    segmentIds: List[int]  # ordered from oldest to newest
    candleIntervalS: int
    segmentDurationS: int
    firstSegmentStartTime: datetime
    lastSegmentEndTime: datetime
