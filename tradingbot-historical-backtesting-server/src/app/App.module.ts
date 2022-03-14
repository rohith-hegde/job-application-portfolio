import { Module } from "@nestjs/common";
import { NoPatternBtModule } from "../noPatternBt/NoPatternBt.module";
import { AppWithLoggerModule } from "tradingbot-common-nest";

@Module({
	imports: [NoPatternBtModule]
})
export class AppModule extends AppWithLoggerModule {}
