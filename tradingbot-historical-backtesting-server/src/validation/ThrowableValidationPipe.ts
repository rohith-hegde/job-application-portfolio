import { Injectable, HttpException, HttpStatus } from "@nestjs/common";
import { JsonSchemaValidationPipe } from "tradingbot-common-nest";

@Injectable()
class ThrowableValidationPipe<I> extends JsonSchemaValidationPipe<I> {
	throwHttpError(errorMsg: string, status: HttpStatus): void {
		throw new HttpException(errorMsg, status);
	}
}

export default ThrowableValidationPipe;
