var config = require("./config.json");
var cmdline = require("node-cmd");

const sleep = (milliseconds) => {
	return new Promise((resolve) => setTimeout(resolve, milliseconds));
};

async function fanSwitch(on) {
	let fsPromise = new Promise(async function (res, rej) {
		if (on) {
			console.log("switching.fanSwitch(): switching fan ON...");
			await broadcastRadioCode(
				config.radioCodes_on.fan.int,
				config.radioCodes_on.fan.pulseLength
			);
		} else {
			console.log("switching.fanSwitch(): switching fan OFF...");
			await broadcastRadioCode(
				config.radioCodes_off.fan.int,
				config.radioCodes_off.fan.pulseLength
			);
		}

		res();
	});

	return fsPromise;
}

async function heaterSwitch(on) {
	let hsPromise = new Promise(async function (res, rej) {
		if (config.heater_enabled) {
			if (on) {
				console.log("switching.heaterSwitch(): switching heater ON...");
				await broadcastRadioCode(
					config.radioCodes_on.heater.int,
					config.radioCodes_on.heater.pulseLength
				);
			} else {
				console.log(
					"switching.heaterSwitch(): switching heater OFF..."
				);
				await broadcastRadioCode(
					config.radioCodes_off.heater.int,
					config.radioCodes_off.heater.pulseLength
				);
			}
		} else console.log("switching.heaterSwitch(): heater disabled, so doing nothing...");

		res();
	});

	return hsPromise;
}

function broadcastRadioCode(int, pulseLength) {
	let cmdPromise = new Promise(function (resolve, reject) {
		try {
			let cmdOnce = `${config.codesend_executable_path} ${int} -p ${config.tx_gpio_pin} -l ${pulseLength}\n`;

			let cmdRepeated = "";

			for (var i = 0; i <= config.radioCode_repeatTimes; i++) {
				cmdRepeated += cmdOnce;
				cmdRepeated += `sleep ${config.radioCode_delayMs / 1000}\n`;
			}

			/*cmdline.get(cmdRepeated, function(err, data, stderr) {
				//if (!err) console.log(data);
				//else console.log("error with command line");
			});*/

			//cmdline.run(cmdRepeated);
			cmdline.get(cmdRepeated, function (err, data, stderr) {
				if (err) reject(err);
				else resolve();
			});
		} catch (err) {
			console.log(
				"switching.broadcastRadioCode(): error executing RFoutlet codesend command"
			);
			console.error(err);
			reject(err);
		}
	});

	return cmdPromise;
}

module.exports.heaterSwitch = heaterSwitch;
module.exports.fanSwitch = fanSwitch;
