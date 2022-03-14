import { Injectable, Logger } from "@nestjs/common";
import AppConfigurationService from "../config/AppConfiguration.service";
import { NoPatternBtProvider } from "./NoPatternBt.models";
import {
	DGroupBacktestParams,
	DSingleBacktestParams,
	DTradeResultGroupResponse,
	DTradeResultResponse
} from "tradingbot-common-classes";
import { NoPatternBtProvider_HDS } from "../noPatternBtHds/NoPatternBtHds.provider";

@Injectable()
class NoPatternBtService {
	private readonly logger: Logger = new Logger(NoPatternBtService.name);
	provider: NoPatternBtProvider;

	constructor(private configService: AppConfigurationService) {
		this.configService.load().then(() => {
			this.provider = new NoPatternBtProvider_HDS(
				this.configService.get().maxBacktestThreads,
				this.configService.get().apisdk_historicalData,
				this.configService.get().apisdk_historicalData_XHDS_cache
			);
		});
	}

	async doSingleBacktest(
		backtestParams: DSingleBacktestParams
	): Promise<DTradeResultResponse> {
		try {
			const result: DTradeResultResponse =
				await this.provider.doSingleBacktest(backtestParams);
			return Promise.resolve(result);
		} catch (rootErr) {
			this.logger.error(
				`Error attempting to perform single backtest: ${
					(rootErr as Error).message
				} \n ${(rootErr as Error).stack}}`
			);
			return Promise.reject(rootErr);
		}
	}

	async doGroupBacktest(
		backtestParams: DGroupBacktestParams
	): Promise<DTradeResultGroupResponse> {
		try {
			const result: DTradeResultGroupResponse =
				await this.provider.doGroupBacktest(backtestParams);
			return Promise.resolve(result);
		} catch (rootErr) {
			this.logger.error(
				`Error attempting to perform group backtest: ${
					(rootErr as Error).message
				} \n ${(rootErr as Error).stack}}`
			);
			return Promise.reject(rootErr);
		}
	}
}

export default NoPatternBtService;
