/**
 * 	This project is an example of how I built my own machine learning model
 * 	I downloaded data on the fuel economy of random vehicles vs. their engine size and weight.
 * 	The neural network trains itself from the example vehicle data.
 * 	Finally, several never seen before vehicles are fed to the network. The network guesses the fuel economy of each one
 * 	The results (the predicted MPG for each vehicle) are printed to the console.
 */

const brain = require("./brain.js");
const MAX_TRAINING_RUNS = 10000;
var trainingData = require("./training-data.js").data;

var testData = require("./test-data.js").data;

main();

function main() {
	console.log(
		"\n\nWelcome to the Vehicle Fuel Economy Simulator \n----------------------"
	);
	//-------------Train the network-------------------------
	const net = new brain.NeuralNetwork();
	//console.log(trainingData);

	const maxes = findMaxes(trainingData);
	//console.log(maxes);

	normalizeInputData(trainingData, maxes);
	//console.log(trainingData);

	console.log(
		"Training neural network with " +
			trainingData.length +
			" sample vehicles..."
	);

	const trainOutput = net.train(trainingData, {
		iterations: MAX_TRAINING_RUNS,
	});
	console.log(
		"Training completed. Training summary: " + JSON.stringify(trainOutput)
	);

	//---------------Test how accurate the network is--------------

	normalizeInputData(testData, maxes);
	//console.log(testData);

	var results = [];

	for (var i = 0; i < testData.length; i++) {
		results.push({
			vehicleName: testData[i].inputRaw.vehicleName,
			actualMpg: testData[i].outputRaw.mpg,
			predictedMpg_norm: net.run(testData[i].input).mpg_norm,
		});
	}

	//console.log(results);

	deNormalizeResults(results, maxes);
	//console.log(results);

	console.log("\nTest results: -------------------\n");

	for (var i = 0; i < results.length; i++) {
		const accuracy =
			1 -
			Math.abs(results[i].actualMpg - results[i].predictedMpg) /
				results[i].actualMpg;

		//profit!

		console.log(
			"Vehicle name: " +
				results[i].vehicleName +
				"\n\t" +
				"Predicted MPG: **" +
				results[i].predictedMpg.toFixed(2) +
				"** | Actual MPG: " +
				results[i].actualMpg.toFixed(2) +
				" | ACCURACY: " +
				(accuracy * 100).toFixed(2) +
				"%\n"
		);
	}
}

function normalizeInputData(vehicleArray, maxes) {
	//converts the input data into a format that brain.js can understand
	//converts all numbers to fit in the range 0-1

	for (var i = 0; i < vehicleArray.length; i++) {
		var vehicle = vehicleArray[i];

		vehicle.input = {
			disp_norm: vehicle.inputRaw.engineDispL / maxes.max_disp,
			weight_norm: vehicle.inputRaw.curbWeightLb / maxes.max_weight,
		};

		vehicle.output = {
			mpg_norm: vehicle.outputRaw.mpg / maxes.max_mpg,
		};
	}
}

function deNormalizeResults(resultsArray, maxes) {
	//converts the results from brain.js into a format that humans can understand
	//convers all 0-1 numbers back to their regular values

	for (var i = 0; i < resultsArray.length; i++) {
		var result = resultsArray[i];
		result.predictedMpg = result.predictedMpg_norm * maxes.max_mpg;
	}
}

function findMaxes(vehicleArray) {
	//calculates the maximum values for the input and output data points
	var max_disp = Number.MIN_VALUE;
	var max_weight = Number.MIN_VALUE;
	var max_mpg = Number.MIN_VALUE;

	for (var i = 0; i < vehicleArray.length; i++) {
		var vehicle = vehicleArray[i];

		if (vehicle.inputRaw.engineDispL > max_disp)
			max_disp = vehicle.inputRaw.engineDispL;

		if (vehicle.inputRaw.curbWeightLb > max_weight)
			max_weight = vehicle.inputRaw.curbWeightLb;

		if (vehicle.outputRaw.mpg > max_mpg) max_mpg = vehicle.outputRaw.mpg;
	}

	return {
		max_disp: max_disp,
		max_weight: max_weight,
		max_mpg: max_mpg,
	};
}
