import { SingleBacktestFunc } from "../noPatternBtHds/Models";
import { DTradeResultResponse } from "tradingbot-common-classes";

class FuncBatchRateLimiter {
	jobFuncs: SingleBacktestFunc[];
	batchSize: number;
	maxTolerableRejections: number;
	rejectionCt: number;

	constructor(
		jobFuncs: SingleBacktestFunc[],
		batchSize: number,
		maxTolerableRejections = 0
	) {
		this.jobFuncs = jobFuncs;
		this.batchSize = batchSize;
		this.maxTolerableRejections = maxTolerableRejections;
		this.rejectionCt = 0;
	}

	async run(): Promise<DTradeResultResponse[]> {
		const batchCt: number = Math.ceil(
			this.jobFuncs.length / this.batchSize
		); // ceil(4741 / 50) = ceil(94.82) = 95 batches

		const allResults: DTradeResultResponse[] = [];

		for (let b = 0; b < batchCt; b++) {
			const batchResults: DTradeResultResponse[] =
				await this.runBatch_tolerateFailure(b);
			allResults.push(...batchResults);
		}

		return allResults;
	}

	async runBatch_tolerateFailure(
		batchNum?: number
	): Promise<DTradeResultResponse[]> {
		const batchProms: Promise<DTradeResultResponse>[] = [];

		for (let p = 0; p < this.batchSize && this.jobFuncs.length >= 1; p++) {
			const jobFunc: SingleBacktestFunc = this.jobFuncs.shift();
			const jobProm: Promise<DTradeResultResponse> = jobFunc(); //the Promise will be started now
			batchProms.push(jobProm);
		}

		const settledResults: PromiseSettledResult<DTradeResultResponse>[] =
			await Promise.allSettled(batchProms);

		const successfulResults: DTradeResultResponse[] = [];

		settledResults.forEach(
			(
				settledResult: PromiseSettledResult<DTradeResultResponse>,
				i: number
			) => {
				if (settledResult.status === "rejected") {
					console.error(
						`WARNING | FuncBatchRateLimiter.runBatch_tolerateFailure(): settled result ${i} in batch ${batchNum} rejected: \n${settledResult.reason}`
					);
					this.rejectionCt++;
				} else successfulResults.push(settledResult.value);
			}
		);

		if (this.rejectionCt > this.maxTolerableRejections) {
			const tooManyFailureErr: Error = Error(
				`Too many job Promises were rejected (${this.rejectionCt} > ${this.maxTolerableRejections} tolerable)`
			);
			return Promise.reject(tooManyFailureErr);
		} else return Promise.resolve(successfulResults);
	}
}

export default FuncBatchRateLimiter;
