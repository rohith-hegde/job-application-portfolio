import {
	DGroupBacktestParams,
	DSingleBacktestParams,
	Schema_DGroupBacktestParams,
	Schema_DSingleBacktestParams
} from "tradingbot-common-classes";
import ThrowableValidationPipe from "../validation/ThrowableValidationPipe";

const ValidationPipe_SingleBacktestParams =
	new ThrowableValidationPipe<DSingleBacktestParams>(
		Schema_DSingleBacktestParams as unknown as JSON
	);

const ValidationPipe_GroupBacktestParams =
	new ThrowableValidationPipe<DGroupBacktestParams>(
		Schema_DGroupBacktestParams as unknown as JSON
	);

export {
	ValidationPipe_SingleBacktestParams,
	ValidationPipe_GroupBacktestParams
};
