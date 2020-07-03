var https = require("https");
var config = require("./config.json");
var dewpoint_lib = require("dewpoint");
var envSensor = require("node-dht-sensor");

const sleep = milliseconds => {
	return new Promise(resolve => setTimeout(resolve, milliseconds));
};

//test the DHT22 sensor
//dhtTest();

async function dhtTest() {
	/*getIndoorReport_test(6);
	await sleep(1000);
	getIndoorReport_test(6);
	await sleep(1000);
	getIndoorReport_test(7);
	await sleep(1000);
	getIndoorReport_test(6);
	await sleep(1000);
	getIndoorReport_test(6);
	await sleep(1000);
	getIndoorReport_test(8);
	await sleep(1000);*/

	getIndoorReport_test();

	/*for (var i = 4; i < 5; i++) {
		getIndoorReport_test(i);
	}*/
}

function getWeatherbitReport() {
	var url =
		`https://api.weatherbit.io/v2.0/current?postal_code=${config.weatherbit.postal_code}` +
		`&country=${config.weatherbit.country}&units=${config.weatherbit.units}&key=${config.weatherbit.api_key}`;

	let apiPromise = new Promise(function(resolve, reject) {
		https
			.get(url, resp => {
				let data = "";

				resp.on("data", chunk => {
					data += chunk;
				});

				resp.on("end", () => {
					//console.log("data received");
					resolve(JSON.parse(data).data[0]);
				});
			})
			.on("error", err => {
				reject(err);
			});
	});

	return apiPromise;
}

async function getOutdoorReport() {
	let reportPromise = new Promise(async function(resolve, reject) {
		let jsonObj = {};

		try {
			jsonObj = await getWeatherbitReport();
		} catch (err) {
			console.error(
				"conditionprovider.getOutdoorReport(): error while getting report"
			);
			console.error(err);
			reject(err);
		}

		//console.log("API received");
		let report = {};

		report.realfeel = jsonObj.app_temp;
		report.rh = jsonObj.rh;
		report.temp = jsonObj.temp;
		report.dewpt = jsonObj.dewpt;
		report.time = jsonObj.ob_time;
		report.timestamp = jsonObj.ts;

		if (config.weatherbit.convert_to_F) {
			report.temp = report.temp * 1.8 + 32;
			report.realfeel = report.realfeel * 1.8 + 32;
			report.dewpt = report.dewpt * 1.8 + 32;
		}

		report.temp = +report.temp.toFixed(2);
		report.realfeel = +report.realfeel.toFixed(2);
		report.dewpt = +report.dewpt.toFixed(2);

		resolve(report);
	});

	return reportPromise;
}

async function getIndoorReport_mock() {
	let reportPromise = new Promise(async function(resolve, reject) {
		let report = {};
		report.realfeel = -999;

		try {
			const test_temp = [80.4, 76.5, 83, 85, 81.1];
			const test_rh = [60, 88, 48, 72, 45];
			const sensor_rh = test_rh[3];
			const sensor_temp = test_temp[3];
			let sensor_dewpt = new dewpoint_lib(0).Calc(sensor_temp, sensor_rh)
				.dp;

			report.rh = sensor_rh;
			report.temp = sensor_temp;
			report.dewpt = sensor_dewpt;

			report.temp = +report.temp.toFixed(2);
			report.rh = +report.rh.toFixed(2);
			report.dewpt = +report.dewpt.toFixed(2);

			resolve(report);
		} catch (err) {
			console.log(
				"conditionprovider.getIndoorReport_mock(): error while getting report"
			);
			console.error(err);
			reject(err);
		}
	});

	return reportPromise;
}

async function getIndoorReport_test() {
	console.log("reading dHT22 sensor values... ");
	envSensor.read(22, config.dht22_gpio_pin, function(err, temp, rh) {
		console.log(`temp: ${temp}, rh: ${rh}`);
		if (err) throw err;
	});
}

async function getIndoorReport() {
	let reportPromise = new Promise(async function(resolve, reject) {
		envSensor.read(22, config.dht22_gpio_pin, function(err, temp, rh) {
			try {
				if (err) reject(err);
				let report = {};
				report.realfeel = -999;

				report.rh = rh;
				report.temp = temp * 1.8 + 32;
				report.temp += config.indoor_tempAdjust;

				report.dewpt = new dewpoint_lib(0).Calc(
					report.temp,
					report.rh
				).dp;
				report.temp = +report.temp.toFixed(2);
				report.rh = +report.rh.toFixed(2);
				report.dewpt = +report.dewpt.toFixed(2);

				resolve(report);
			} catch (err) {
				console.log(
					"conditionprovider.getIndoorReport(): error while getting report"
				);
				console.error(err);
				reject(err);
			}
		});
	});

	return reportPromise;
}

module.exports.getWeatherbitReport = getWeatherbitReport;
module.exports.getIndoorReport_mock = getIndoorReport_mock;
module.exports.getIndoorReport = getIndoorReport;
module.exports.getOutdoorReport = getOutdoorReport;
