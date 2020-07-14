<template>
	<MachineStatusButton v-bind="buttonBase" ref="buttonBaseComp" />
</template>

<script lang="ts">
import MachineStatusButton from "./MachineStatusButton.vue";
import Component from 'vue-class-component';
import Vue from 'vue';

const Props = Vue.extend({
	props: {
		machineName: String,
		machineDeviceID: Number //used for identifying the machine when talking to the processor API
	}
})

@Component({
	components: {
		MachineStatusButton
	},
})
export default class MachineStatusButtonPowerOff extends Props {
	
	$refs!:{
		buttonBaseComp:MachineStatusButton
	}

	data() {
		return {
			buttonBase: {
				buttonText: "Power off",
				buttonColorAccent: "light",
				shapeClass: "btn-pill",
				withIcon: true,
				iconClass: "cui-moon",
				additionalStyle: "",
				disabled: false,
				onClickCallback: this.powerOffMachine
			}
		};
	}

	
	
	get	buttonBaseComp() {
		return this.$refs.buttonBaseComp;
	}
	
	
	powerOffMachine() {
		//placeholder code until real function with API call is implemented
		console.log(
			`Powering off machine \'${this.machineName}\' with ID \'${
				this.machineDeviceID
			}\'...`
		);
	}
	
};
</script>


