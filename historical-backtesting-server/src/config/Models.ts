import {
	ApiClientOptions,
	CacheOptions
} from "tradingbot-common-apisdk-ts";

interface AppConfig {
	listenHostname: string;
	listenPort: number;
	maxBacktestThreads: number;
	apisdk_historicalData: ApiClientOptions;
	apisdk_historicalData_XHDS_cache: CacheOptions;
}

export { AppConfig };
