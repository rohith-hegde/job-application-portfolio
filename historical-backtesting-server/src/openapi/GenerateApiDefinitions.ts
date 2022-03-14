/* eslint-disable @typescript-eslint/no-var-requires */
/* eslint-disable @typescript-eslint/no-inferrable-types */
import { bootstrap } from "../main";
import AppConfigurationService from "../config/AppConfiguration.service";
import { AppConfig } from "../config/Models";
import {
	ApiGatewayPushScriptGenerator,
	OpenApi3DefinitionGenerator
} from "tradingbot-common-nest";

const resourcePolicy_noPatternBt: JSON = require("./apiGatewayPolicy_noPatternBt.json");

const API_PATH_URLS = [`noPatternBt`];
const APIGW_IDS = [`[censored]`];
const APIGW_RESOURCEPOLICIES = [resourcePolicy_noPatternBt];

async function loadAppConfig(): Promise<AppConfig> {
	const appConfigService: AppConfigurationService =
		new AppConfigurationService();
	await appConfigService.load();

	const selectedAppConfig: AppConfig = appConfigService.get();
	return Promise.resolve(selectedAppConfig);
}

async function generateAllApiSchemas(): Promise<void> {
	const selectedAppConfig: AppConfig = await loadAppConfig();
	try {
		await bootstrap();
	} catch (err) {}

	const serverHostname: string = `localhost`;
	const serverPort: number = selectedAppConfig.listenPort;
	const outputRelDirPath: string = `openapi`;

	console.log(`Generating OpenAPI definitions...\n`);

	for (let i: number = 0; i < API_PATH_URLS.length; i++) {
		try {
			const apiUrlPath: string = API_PATH_URLS[i];
			const apiGwId: string = APIGW_IDS[i];
			const resPolicy: JSON = APIGW_RESOURCEPOLICIES[i];

			const generator: OpenApi3DefinitionGenerator =
				new OpenApi3DefinitionGenerator(
					apiUrlPath,
					outputRelDirPath,
					serverHostname,
					serverPort,
					resPolicy
				);
			await generator.generate();

			const scriptGenerator: ApiGatewayPushScriptGenerator =
				new ApiGatewayPushScriptGenerator(
					apiUrlPath,
					apiGwId,
					outputRelDirPath
				);
			await scriptGenerator.generate();
		} catch (err) {
			console.error(err);
		}
	}

	process.exit(0);
}

generateAllApiSchemas();
