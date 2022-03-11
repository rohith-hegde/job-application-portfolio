import os
import sys
from config.Logging import logger

configFileName: str = "DEFAULT.json"

if len(sys.argv) >= 2:
    configFileName = sys.argv[1]

logger.info(
    f"FileConstants | Using configuration file name '{configFileName}'...")

CONFIG_FILE_PATH: str = f"{os.getcwd()}/config/{configFileName}"

MASTER_INPUT_FOLDER_PATH: str = f"{os.getcwd()}/input"
MODEL_FOLDER_PATH: str = f"{MASTER_INPUT_FOLDER_PATH}/models/completed"

FILE_NAME_CLASSTORETURNMAP: str = "classToReturnMap.json"
