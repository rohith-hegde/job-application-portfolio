<template>
	<div>
		<AddMachineStep_P v-bind="stepBase" ref="stepBaseComp">
			<b-col slot="stepBody" md="8" sm="10">
			</b-col>
			<span slot="buttonLabel">Finish adding machines</span>
			<span slot="successMsgLabel">Successfully created new machines</span>
			<span slot="errorMsgLabel">Error creating machines. Reason: {{ errorMsgD }}</span>
		</AddMachineStep_P>
	</div>
</template>


<script>
import AddMachineStep_P from "./AddMachineStep_P";

export default {
	name: "AddMachineStep5_P",
	components: {
		AddMachineStep_P
	},
	data() {
		return {
			stepBase: {
				header: {
					accent: "success",
					heading: "Finish adding machines",
					badgeText: "Step 5"
				},
				subheading: "Complete the process",
				refPrefix: "step5",
				startProcessingCallback: this.processData,
				buttonStyle: "width: 300px;"
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
			//real code: send API call to processor with input.filledMachines array
			//receive result of call

			if (Math.random() < 0.5) {
				this.output.success = true;
				delete this.output.errorMsg;
			} else {
				this.output.success = false;
				this.output.errorMsg = "Random error";
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
