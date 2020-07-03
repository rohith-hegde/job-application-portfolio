/**
 * 	This project is an example of how I built my own machine learning regression model
 * 	I downloaded random data on the fuel economy of vehicles vs. their engine size and weight.
 * 	There are 2 inputs: engineDispL and curbWeightLb, and 1 output: miles per gallon
 * 	Heavier cars and cars with bigger engines tend to have lower MPG
 * 	There are 15 example cars in this file: minivans, sedans, pickup trucks, SUVs and crossovers
 *	SOURCES: fueleconomy.gov and edmunds.com
 */

module.exports.data = [
	{
		inputRaw: {
			//one input data point for training the network
			vehicleName: "2019 Kia Soul",
			engineDispL: 2.0,
			curbWeightLb: 2942,
		},
		outputRaw: { mpg: 27 }, //one expected output data point for training the network
	},
	{
		inputRaw: {
			vehicleName: "2019 Chrysler Pacifica",
			engineDispL: 3.6,
			curbWeightLb: 4330,
		},
		outputRaw: { mpg: 22 },
	},
	{
		inputRaw: {
			vehicleName: "2019 Honda Odyssey",
			engineDispL: 3.5,
			curbWeightLb: 4593,
		},
		outputRaw: { mpg: 22 },
	},
	{
		inputRaw: {
			vehicleName: "2019 Ford Mustang",
			engineDispL: 5.0,
			curbWeightLb: 3705,
		},
		outputRaw: { mpg: 19 },
	},
	{
		inputRaw: {
			vehicleName: "2019 Chevrolet Corvette",
			engineDispL: 6.2,
			curbWeightLb: 3298,
		},
		outputRaw: { mpg: 19 },
	},
	{
		inputRaw: {
			vehicleName: "2019 Toyota Highlander",
			engineDispL: 3.5,
			curbWeightLb: 4464,
		},
		outputRaw: { mpg: 23 },
	},
	{
		inputRaw: {
			vehicleName: "2019 Cadillac Escalade",
			engineDispL: 6.2,
			curbWeightLb: 5551,
		},
		outputRaw: { mpg: 17 },
	},
	{
		inputRaw: {
			vehicleName: "2019 Ford Fusion",
			engineDispL: 2.0,
			curbWeightLb: 4085,
		},
		outputRaw: { mpg: 23 },
	},
	{
		inputRaw: {
			vehicleName: "2019 Jeep Cherokee",
			engineDispL: 2.4,
			curbWeightLb: 3590,
		},
		outputRaw: { mpg: 25 },
	},
	{
		inputRaw: {
			vehicleName: "2019 Chevrolet Equinox",
			engineDispL: 2.0,
			curbWeightLb: 3274,
		},
		outputRaw: { mpg: 25 },
	},
	{
		inputRaw: {
			vehicleName: "2019 Honda CR-V",
			engineDispL: 2.4,
			curbWeightLb: 3377,
		},
		outputRaw: { mpg: 28 },
	},
	{
		inputRaw: {
			vehicleName: "2019 Chevrolet Cruze",
			engineDispL: 1.4,
			curbWeightLb: 2870,
		},
		outputRaw: { mpg: 32 },
	},
	{
		inputRaw: {
			vehicleName: "2019 Toyota RAV4",
			engineDispL: 2.5,
			curbWeightLb: 3370,
		},
		outputRaw: { mpg: 30 },
	},
	{
		inputRaw: {
			vehicleName: "2019 Ford F-150",
			engineDispL: 3.5,
			curbWeightLb: 4769,
		},
		outputRaw: { mpg: 16 },
	},
	{
		inputRaw: {
			vehicleName: "2019 GMC Yukon XL",
			engineDispL: 5.3,
			curbWeightLb: 5846,
		},
		outputRaw: { mpg: 16 },
	},
];
