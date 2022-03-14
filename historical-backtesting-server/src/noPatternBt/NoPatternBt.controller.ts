import { Body, Post, UsePipes } from "@nestjs/common";
import { Controller } from "@nestjs/common";
import { ApiBody, ApiExtension, ApiResponse } from "@nestjs/swagger";
import {
	DTradeResultResponse,
	DSingleBacktestParams,
	DGroupBacktestParams,
	DTradeResultGroupResponse
} from "tradingbot-common-classes";

import { HTTP2_METHODS } from "tradingbot-common-nest";
import { AWS_API_GATEWAY_EXTENSION_NAME } from "tradingbot-common-nest";
import { NoPatternBtIntegrator } from "./NoPatternBt.openapi";
import NoPatternBtService from "./NoPatternBt.service";
import {
	ValidationPipe_GroupBacktestParams,
	ValidationPipe_SingleBacktestParams
} from "./NoPatternBt.validation";

@Controller(`noPatternBt`)
class NoPatternBtController {
	constructor(private readonly NoPatternBtService: NoPatternBtService) {}

	@Post(`singleBacktest`)
	@UsePipes(ValidationPipe_SingleBacktestParams)
	@ApiBody({
		type: DSingleBacktestParams
	})
	@ApiResponse({
		type: DTradeResultResponse
	})
	@ApiExtension(
		AWS_API_GATEWAY_EXTENSION_NAME,
		NoPatternBtIntegrator.generateOpenApi3Extension({
			httpMethod: HTTP2_METHODS.POST,
			path: `singleBacktest`,
			defaultResponseStatusCode: 200
		})
	)
	async singleBacktest(
		@Body() backtestParams: DSingleBacktestParams
	): Promise<DTradeResultResponse> {
		const resp: DTradeResultResponse =
			await this.NoPatternBtService.doSingleBacktest(backtestParams);
		return Promise.resolve(resp);
	}

	@Post(`groupBacktest`)
	@UsePipes(ValidationPipe_GroupBacktestParams)
	@ApiBody({
		type: DGroupBacktestParams
	})
	@ApiResponse({
		type: DTradeResultGroupResponse
	})
	@ApiExtension(
		AWS_API_GATEWAY_EXTENSION_NAME,
		NoPatternBtIntegrator.generateOpenApi3Extension({
			httpMethod: HTTP2_METHODS.POST,
			path: `groupBacktest`,
			defaultResponseStatusCode: 200
		})
	)
	async groupBacktest(
		@Body() backtestParams: DGroupBacktestParams
	): Promise<DTradeResultGroupResponse> {
		const resp: DTradeResultGroupResponse =
			await this.NoPatternBtService.doGroupBacktest(backtestParams);
		return Promise.resolve(resp);
	}
}

export default NoPatternBtController;
