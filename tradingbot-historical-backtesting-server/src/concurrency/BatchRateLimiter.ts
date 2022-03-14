class BatchRateLimiter<ResultT> {
	jobProms: Promise<ResultT>[];
	batchSize: number;

	constructor(jobProms: Promise<ResultT>[], batchSize: number) {
		this.jobProms = jobProms;
		this.batchSize = batchSize;
	}

	async run(): Promise<ResultT[]> {
		const batchCt: number = Math.ceil(
			this.jobProms.length / this.batchSize
		); // ceil(4741 / 50) = ceil(94.82) = 95 batches

		const allResults: ResultT[] = [];

		for (let b = 0; b < batchCt; b++) {
			const batchResults: ResultT[] = await this.runBatch();
			allResults.push(...batchResults);
		}

		return allResults;
	}

	async runBatch(): Promise<ResultT[]> {
		const batchProms: Promise<ResultT>[] = [];

		for (let p = 0; p < this.batchSize && this.jobProms.length >= 1; p++) {
			const batchProm: Promise<ResultT> = this.jobProms.shift();
			batchProms.push(batchProm);
		}

		return await Promise.all(batchProms);
	}
}

export default BatchRateLimiter;
