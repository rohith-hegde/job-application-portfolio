var config = require("./config.json");
var condprov = require("./conditionprovider.js");
var switcher = require("./switching.js");

tick();
//test();

function test() {
	condprov.getOutdoorReport(function(report) {
		console.log("outdoor report test received: ");
		console.log(report);
	});
}

async function tick() {
	try {
		await processTick();
	} catch (err) {
		console.error("main.tick(): error while processing tick");
		console.error(err);
	}

	console.log(
		`main.tick(): tick processing finished. Next tick in ${config.tickIntervalS} seconds...` +
			"\n\n"
	);
	setTimeout(tick, config.tickIntervalS * 1000);
}

async function processTick() {
	console.log();
	let outReport = {};
	let inReport = {};

	let tickPromise = new Promise(async function(res, rej) {
		try {
			outReport = await condprov.getOutdoorReport();
			let timeDelayS = new Date().getTime() / 1000 - outReport.timestamp;

			console.log(
				`main.processTick(): current time is ${new Date(
					Date.now()
				).toLocaleString()}. ` +
					`Outdoor report DELAY: ${timeDelayS.toFixed(0)} seconds`
			);

			delete outReport.time;
			delete outReport.timestamp;
			console.log(
				"main.processTick(): outdoor report: " +
					JSON.stringify(outReport)
			);

			inReport = await condprov.getIndoorReport(); //getIndoorReport_mock();

			console.log(
				"main.processTick(): indoor report: " + JSON.stringify(inReport)
			);
		} catch (err) {
			console.log(
				"main.processTick(): error while getting condition reports"
			);
			console.error(err);
			rej(err);
			return;
		}

		try {
			await executeAction(inReport, outReport);
		} catch (err) {
			console.log(
				"main.processTick(): error while executing power action"
			);
			console.error(err);
			rej(err);
			return;
		}

		//all tick actions completed
		res();
	});

	return tickPromise;
}

async function executeAction(inReport, outReport) {
	let colderOutside = outReport.temp < inReport.temp;
	let insideTooCold = inReport.temp < config.indoor_minTemp;
	let insideTooHot = inReport.temp > config.indoor_maxTemp;

	let heaterSwitchedOn = false;

	let execPromise = new Promise(async function(res, rej) {
		if (insideTooHot) {
			//an edge case where the program will cool the room no matter what
			if (colderOutside) await switcher.fanSwitch(true);
			else await switcher.fanSwitch(false);
		} else if (insideTooCold) {
			//an edge case where the program will not cool the room no matter what
			if (colderOutside) {
				await switcher.fanSwitch(false);

				if (config.heater_enabled) {
					await switcher.heaterSwitch(true);
					heaterSwitchedOn = true;
				}
			} else await switcher.fanSwitch(true);
		} else {
			//regular case where the decision is based on dew point
			if (outReport.dewpt + config.dewptTH < inReport.dewpt)
				await switcher.fanSwitch(true);
			else await switcher.fanSwitch(false);
		}

		if (!heaterSwitchedOn) await switcher.heaterSwitch(false);

		//console.log("executed all actions");
		res();
	});

	return execPromise;
}
