<template>
	<MachineStatusButton v-bind="buttonBase" ref="buttonBaseComp" />
</template>

<script>
import MachineStatusButton from "./MachineStatusButton";

export default {
	name: "MachineStatusButtonPowerOn",
	components: {
		MachineStatusButton
	},
	props: {
		machineName: String,
		machineDeviceID: Number //used for identifying the machine when talking to the processor API
	},
	data() {
		return {
			buttonBase: {
				buttonText: "Power on",
				buttonColorAccent: "light",
				shapeClass: "btn-pill",
				withIcon: true,
				iconClass: "cui-lightbulb",
				additionalStyle: "",
				disabled: true,
				onClickCallback: this.powerOnMachine
			}
		};
	},
	computed: {
		buttonBaseComp() {
			return this.$refs.buttonBaseComp;
		}
	},
	methods: {
		powerOnMachine() {
			//placeholder code until real function with API call is implemented
			console.log(
				`Powering on machine \'${this.machineName}\' with ID \'${
					this.machineDeviceID
				}\'...`
			);
		}
	}
};
</script>


