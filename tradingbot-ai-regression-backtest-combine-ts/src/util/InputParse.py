
from typing import Any, Dict, List, Union
from Models import InputArgs


def parseEventDict(event: Dict[str, Any]) -> InputArgs:
    if not event.get("gridSearchIdPrefix") or event.get("gridSearchIdPrefix") == "":
        raise ValueError("Argument 'gridSearchIdPrefix' is required")
    gridSearchIdPrefix: int = int(event["gridSearchIdPrefix"])

    args: InputArgs = InputArgs(gridSearchIdPrefix=gridSearchIdPrefix)
    return args
