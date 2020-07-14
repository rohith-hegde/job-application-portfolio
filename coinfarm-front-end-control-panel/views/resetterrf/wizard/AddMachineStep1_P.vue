<template>
	<div>
		<AddMachineStep_P v-bind="stepBase" ref="stepBaseComp">
			<b-col slot="stepBody" md="8" sm="10">
				<v-select v-model="output.selectedHubID" :options="input.hubOptions" placeholder="Select hub name" />
			</b-col>
			<span slot="buttonLabel">Use this hub</span>
			<span slot="successMsgLabel">Successfully communicated with hub</span>
			<span slot="errorMsgLabel">This hub can't be used. Reason: {{ errorMsgD }}</span>
		</AddMachineStep_P>
	</div>
</template>


<script>
import vSelect from "vue-select";
import AddMachineStep_P from "./AddMachineStep_P";

export default {
	name: "AddMachineStep1_P",
	components: {
		AddMachineStep_P,
		vSelect
	},
	data() {
		return {
			stepBase: {
				header: {
					accent: "info",
					heading: "Choose the right hub",
					badgeText: "Step 1"
				},
				subheading: "Select the hub for the new machine(s)",
				refPrefix: "step1",
				startProcessingCallback: this.processData,
				buttonStyle: "width: 160px;"
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

			//placeholder code to simulate the real delay involved
			const loadDelayMs = Math.random() * 1500;
			setTimeout(this.processResult, loadDelayMs);
		},
		processResult() {
			//placeholder result until real code is implemented

			if (Math.random() < 1) {
				this.output.success = true;
				delete this.output.errorMsg;
			} else {
				this.output.success = false;
				this.output.errorMsg = "Random error";
			}

			if (
				!this.output.selectedHubID ||
				!this.output.selectedHubID.value
			) {
				this.output.success = false;
				this.output.errorMsg =
					"Please select one of your hubs from the drop-down list";
			}

			if (this.output.errorMsg) this.errorMsgD = this.output.errorMsg;

			this.stepBaseComp.processingFinished(this.output.success);
			this.outputCallback(this.output);
		}
	},
	computed: {
		stepBaseComp: function() {
			return this.$refs.stepBaseComp;
		}
	}
};
</script>


<style scoped>
</style>
