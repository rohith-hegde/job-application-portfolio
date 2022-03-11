
from typing import Dict
from math import ceil, floor
import numpy as NP

RANGE: float = 10.0
IND_ROUND_PLACES: int = 2


def round_to_multiple(number, multiple, direction='nearest'):
    if direction == 'nearest':
        return multiple * round(number / multiple)
    elif direction == 'up':
        return multiple * ceil(number / multiple)
    elif direction == 'down':
        return multiple * floor(number / multiple)
    else:
        return multiple * round(number / multiple)


class ClassReturnMap():
    mapDict: Dict[float, float]
    mapDict_keyCt: int
    mapDict_increment: float
    interpolateClass: bool

    def __init__(
            self, mapDict: Dict[float, float],
            interpolateClass: bool = True):
        self.mapDict = mapDict
        self.interpolateClass = interpolateClass
        self.mapDict_keyCt = len(mapDict)
        self.mapDict_increment = RANGE / self.mapDict_keyCt
        return

    def getReturnForClass(self, classDec: float) -> float:
        if classDec < -3 or classDec > RANGE + 3:
            raise ValueError(f"Decimal class '{classDec}' is out of range.")

        classDec_adj: float = classDec  # 4.76
        if classDec_adj < 0:
            classDec_adj = self.mapDict_increment
        elif classDec_adj > 10:
            classDec_adj = 10 - self.mapDict_increment

        if self.interpolateClass:
            nearestMult_down: float = round_to_multiple(
                classDec_adj, self.mapDict_increment, "down")
            nearestMult_up: float = round_to_multiple(
                classDec_adj, self.mapDict_increment, "up")

            highestKeyDec: float = float(list(self.mapDict.keys())[-1:][0])

            if nearestMult_down > highestKeyDec:
                nearestMult_down = highestKeyDec

            if nearestMult_up > highestKeyDec:
                nearestMult_up = highestKeyDec

            # +1 guarantees no zero weight error
            nearestMult_down_diff: float = abs(
                classDec_adj - nearestMult_down) + 1
            nearestMult_up_diff: float = abs(
                nearestMult_up - classDec) + 1

            nearestMult_down_key: str = f"{round(nearestMult_down, IND_ROUND_PLACES)}"
            nearestMult_up_key: str = f"{round(nearestMult_up, IND_ROUND_PLACES)}"

            try:
                return_down: float = self.mapDict[nearestMult_down_key]
                return_up: float = self.mapDict[nearestMult_up_key]

                return_wAvg: float = NP.average(
                    [return_down, return_up],
                    weights=[nearestMult_up_diff, nearestMult_down_diff])
                return return_wAvg
            except Exception as e:
                raise ValueError(
                    f"Error looking up return for either class down index '{nearestMult_down_key}' or class up index '{nearestMult_up_key}': ",
                    e)
        else:
            nearestMult: float = round_to_multiple(
                classDec_adj, self.mapDict_increment)

            return_nearest: float = self.mapDict[round(
                nearestMult, IND_ROUND_PLACES)]
            return return_nearest
