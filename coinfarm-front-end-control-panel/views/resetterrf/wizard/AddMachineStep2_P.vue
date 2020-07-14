<template>
	<div>
		<AddMachineStep_P v-bind="stepBase" ref="stepBaseComp">
			<b-col slot="stepBody" md="8" sm="10">
				
			</b-col>
			<span slot="buttonLabel">Retrieve hosts</span>
			<span slot="successMsgLabel">{{ hostCountD }} LAN hosts successfully received</span>
			<span slot="errorMsgLabel">Retrieving hosts failed. Reason: {{ errorMsgD }}</span>
		</AddMachineStep_P>
	</div>
</template>


<script>
import AddMachineStep_P from "./AddMachineStep_P";
import testHostTableData from "./TestHostTableData";

export default {
	name: "AddMachineStep2_P",
	components: {
		AddMachineStep_P
	},
	data() {
		return {
			stepBase: {
				header: {
					accent: "danger",
					heading: "Scan network for hosts",
					badgeText: "Step 2"
				},
				subheading: "Get new hosts on the LAN",
				refPrefix: "step2",
				startProcessingCallback: this.processData,
				buttonStyle: "width: 160px;"
			},
			errorMsgD: "no error",
			hostCountD: -1,
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

				this.output.hostData = testHostTableData;
				this.output.hostCount = testHostTableData.length;
			} else {
				this.output.success = false;
				//this.output.errorMsg = "Random error";

				this.output.errorMsg = "ARP table read denied: error 5";
				delete this.output.hostData;
				delete this.output.hostCount;
			}

			if (this.input.hubID < 1) {
				this.output.success = false;
				this.output.errorMsg = `Invalid HubID ${this.input.hubID}`;
			}

			if (this.output.errorMsg) this.errorMsgD = this.output.errorMsg;
			if (this.output.hostCount) this.hostCountD = this.output.hostCount;

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
