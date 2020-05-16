/**
 * 	This file contains the stats of never-seen-before vehicles
 * 	SOURCES: fueleconomy.gov and edmunds.com
 */

module.exports.data = [
	{
		inputRaw: {
			//one input data point for training the network
			vehicleName: "2019 Toyota Camry XSE",
			engineDispL: 3.5,
			curbWeightLb: 3395,
		},
		outputRaw: { mpg: 26 }, //the expected output data point
	},
	{
		inputRaw: {
			vehicleName: "2019 BMW X7 50i",
			engineDispL: 4.4,
			curbWeightLb: 5370,
		},
		outputRaw: { mpg: 17 },
	},
	{
		inputRaw: {
			vehicleName: "2019 Volvo XC60",
			engineDispL: 2.0,
			curbWeightLb: 4109,
		},
		outputRaw: { mpg: 24 },
	},
	{
		inputRaw: {
			vehicleName: "2019 Ram 1500 Classic",
			engineDispL: 3.6,
			curbWeightLb: 4520,
		},
		outputRaw: { mpg: 19 },
	},
	{
		inputRaw: {
			vehicleName: "2019 Volkswagen GTI",
			engineDispL: 2.0,
			curbWeightLb: 3128,
		},
		outputRaw: { mpg: 27 },
	},
	{
		inputRaw: {
			vehicleName: "2020 Cadillac XT4",
			engineDispL: 2.0,
			curbWeightLb: 3691,
		},
		outputRaw: { mpg: 24 },
	},
];

/*{
		inputRaw: {
			vehicleName: "2019 Harley Davidson Superlow",
			engineDispL: 0.88,
			curbWeightLb: 545,
		},
		outputRaw: { mpg: 51 },
	},*/
/*{
		inputRaw: {
			vehicleName: "US M1 Abrams battle tank",
			engineDispL: 22.14,
			curbWeightLb: 124000,
		},
		outputRaw: { mpg: 0.57 },
	},*/
