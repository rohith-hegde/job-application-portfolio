import { INestApplication } from "@nestjs/common";
import { NestFactory } from "@nestjs/core";
import { PrintApiRoutes } from "tradingbot-common-nest";
import { AppModule } from "./app/App.module";
import AppConfigurationService from "./config/AppConfiguration.service";
import { AppConfig } from "./config/Models";
import { NoPatternBtModule } from "./noPatternBt/NoPatternBt.module";
import { NoPatternBtOpenApiSetup } from "./noPatternBt/NoPatternBt.openapi";
import * as bodyParser from "body-parser";

function bootstrapSwagger(app: INestApplication): PrintApiRoutes {
	const apiSetup_noPatternBt: NoPatternBtOpenApiSetup =
		new NoPatternBtOpenApiSetup();

	const printRoutes_noPatternBt: PrintApiRoutes =
		apiSetup_noPatternBt.createDocument(
			app,
			[NoPatternBtModule],
			`api/noPatternBt`
		);

	return (): void => {
		printRoutes_noPatternBt();
	};
}

async function bootstrap(): Promise<void> {
	const appConfigService: AppConfigurationService =
		new AppConfigurationService();
	await appConfigService.load();
	const selectedAppConfig: AppConfig = appConfigService.get();

	const app: INestApplication = await NestFactory.create(AppModule);

	app.use(bodyParser.json({ limit: "25mb" }));
	app.use(bodyParser.urlencoded({ limit: "25mb", extended: true }));

	app.enableCors();
	app.useLogger(["debug", "verbose", "log", "warn", "error"]);

	const printAllRoutes: PrintApiRoutes = bootstrapSwagger(app);
	await app.listen(
		selectedAppConfig.listenPort,
		selectedAppConfig.listenHostname
	);

	console.log();
	console.log(
		`Server listening on http://${selectedAppConfig.listenHostname}:${selectedAppConfig.listenPort} ...`
	);
	console.log();
	printAllRoutes();
	console.log();
}

bootstrap();

export { bootstrap };
