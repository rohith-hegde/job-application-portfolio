/* eslint-disable @typescript-eslint/ban-types */
import { INestApplication } from "@nestjs/common";
import {
	DocumentBuilder,
	OpenAPIObject,
	SwaggerModule
} from "@nestjs/swagger";

import {
	ApiGatewayIntegrator,
	DEFAULT_API_TOS,
	OpenApiSetup,
	PrintApiRoutes
} from "tradingbot-common-nest";
import {
	ApiGatewayIntegrationBaseConfig,
	ApiGatewayIntegrationConnectionType,
	ApiGatewayIntegrationType
} from "tradingbot-common-nest";

class NoPatternBtOpenApiSetup extends OpenApiSetup {
	createDocument(
		app: INestApplication,
		modules: Function[],
		apiPath: string
	): PrintApiRoutes {
		const docOptions: Omit<OpenAPIObject, "paths"> = new DocumentBuilder()
			.setTitle(`Coinfarm trading bot | Historical backtesting no-pattern`)
			.setDescription(
				`An API for performing historical market backtests without chart patterns` +
					`<br/><br/>` +
					DEFAULT_API_TOS
			)
			.setVersion(`0.2.2`)
			.build();

		const doc: OpenAPIObject = SwaggerModule.createDocument(
			app,
			docOptions,
			{
				include: modules
			}
		);

		SwaggerModule.setup(apiPath, app, doc);

		return (): void => {
			this.printDocumentRoutes(doc);
		};
	}
}

const integratorBaseConfig: ApiGatewayIntegrationBaseConfig = {
	connectionType: ApiGatewayIntegrationConnectionType.VPC_LINK,
	type: ApiGatewayIntegrationType.HTTP_PROXY,
	connectionId: `akttpr`,
	baseUri: `http://api-vpcl-proxy.dev.coinfarmtradingbot.net/no-pattern-bt/`,
	passthroughBehavior: `when_no_match`
};

const NoPatternBtIntegrator: ApiGatewayIntegrator =
	new ApiGatewayIntegrator(integratorBaseConfig);

export { NoPatternBtOpenApiSetup, NoPatternBtIntegrator };
