<template>
	<MachineStatusButton v-bind="buttonBase" ref="buttonBaseComp" />
</template>

<script>
import MachineStatusButton from "./MachineStatusButton";

export default {
	name: "MachineStatusButtonReset",
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
				buttonText: "Hard reset",
				buttonColorAccent: "info",
				shapeClass: "btn-pill",
				withIcon: true,
				iconClass: "cui-action-redo",
				additionalStyle: "",
				disabled: false,
				onClickCallback: this.resetMachine
			}
		};
	},
	computed: {
		buttonBaseComp() {
			return this.$refs.buttonBaseComp;
		}
	},
	methods: {
		resetMachine() {
			//placeholder code until real function with API call is implemented
			console.log(
				`Hard resetting machine \'${this.machineName}\' with ID \'${
					this.machineDeviceID
				}\'...`
			);
		}
	}
};
</script>


