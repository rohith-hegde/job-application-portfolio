<template>
	<div>
		<AddMachineStep_P v-bind="stepBase" ref="stepBaseComp">
			<b-col slot="stepBody">
				<MachinePropertiesSelect ref="step4_mps" :machinesInitial="input.machinesInitial" />
			</b-col>
			<span slot="buttonLabel">Finish machine properties</span>
			<span slot="successMsgLabel">Properties validated</span>
			<span slot="errorMsgLabel">Invalid properties. Reason: {{ errorMsgD }}</span>
		</AddMachineStep_P>
	</div>
</template>


<script>
import AddMachineStep_P from "./AddMachineStep_P";
import MachinePropertiesSelect from "./MachinePropertiesSelect";

export default {
	name: "AddMachineStep4_P",
	components: {
		AddMachineStep_P,
		MachinePropertiesSelect
	},
	data() {
		return {
			stepBase: {
				header: {
					accent: "warning",
					heading: "Properties of the machines",
					badgeText: "Step 4"
				},
				subheading: "Configure the properties of each machine",
				refPrefix: "step4",
				startProcessingCallback: this.processData,
				buttonStyle: "width: 320px;"
			},
			errorMsgD: "no error",
			output: {}
		};
	},
	props: {
		input: Object,
		outputCallback: Function
	},
	methods: {
		processData() {
			this.stepBaseComp.processingStarted();
			this.processResult(); //no connection to server needed since data is already here
		},
		processResult() {
			const success = this.mpsComp.allFormInputSuccessful();

			if (success) {
				this.output.success = true;
				this.output.filledMachines = this.mpsComp.getMachineData();
				delete this.output.errorMsg;
			} else {
				this.output.success = false;
				this.output.errorMsg =
					"Form invalidly filled for one or more machines";
				delete this.output.filledMachines;
			}

			if (this.output.errorMsg) this.errorMsgD = this.output.errorMsg;

			this.stepBaseComp.processingFinished(this.output.success);
			this.outputCallback(this.output);
		}
	},
	computed: {
		stepBaseComp: function() {
			return this.$refs.stepBaseComp;
		},
		mpsComp: function() {
			return this.$refs.step4_mps;
		}
	}
};
</script>


<style scoped>
</style>
