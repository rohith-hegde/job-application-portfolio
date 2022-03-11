import json
from typing import Dict

from config.Loader import parseAppConfig
from objects.Config import AppConfig
from util.FileConstants import CONFIG_FILE_PATH


class AppConfig_Injectable():
    appConfig: AppConfig

    def __init__(self):
        with open(CONFIG_FILE_PATH) as json_file:
            appConfig_dict: Dict = json.load(json_file)
            self.appConfig = parseAppConfig(appConfig_dict)
