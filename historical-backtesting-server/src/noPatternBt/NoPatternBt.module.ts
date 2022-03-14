import { Module } from "@nestjs/common";
import AppConfigurationService from "../config/AppConfiguration.service";
import NoPatternBtController from "./NoPatternBt.controller";
import NoPatternBtService from "./NoPatternBt.service";

@Module({
	controllers: [NoPatternBtController],
	providers: [AppConfigurationService, NoPatternBtService]
})
export class NoPatternBtModule {}
