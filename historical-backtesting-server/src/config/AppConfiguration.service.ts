import { Injectable } from "@nestjs/common";
import { AppConfig } from "./Models";
import { FileConfigLoader } from "tradingbot-common-config";
import { CustomConfigService } from "tradingbot-common-nest";
import AppFileConfigLoader from "./AppFileConfigLoader";

@Injectable()
class AppConfigurationService extends CustomConfigService<AppConfig> {
	createLoader(): FileConfigLoader<AppConfig> {
		return new AppFileConfigLoader(process.cwd());
	}
}

export default AppConfigurationService;
