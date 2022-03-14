import { HttpProtocol } from "tradingbot-common-apisdk-ts";
import { FileConfigLoaderCwd } from "tradingbot-common-config";
import { AppConfig } from "./Models";

class AppFileConfigLoader extends FileConfigLoaderCwd<AppConfig> {
	generateDefault(): AppConfig {
		const defaultCfg: AppConfig = {
			listenHostname: "0.0.0.0",
			listenPort: 3005,
			maxBacktestThreads: 50,
			apisdk_historicalData: {
				protocol: HttpProtocol.HTTPS,
				hostname: `[censored].execute-api.us-east-2.amazonaws.com`,
				basePath: `LATEST`
			},
			apisdk_historicalData_XHDS_cache: {
				maxObjects: 200
			}
		};

		return defaultCfg;
	}
}

export default AppFileConfigLoader;
