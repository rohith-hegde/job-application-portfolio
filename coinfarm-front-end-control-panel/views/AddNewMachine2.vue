<template>
	<div class="animated fadeIn">
		<b-row align-h="center">
			<b-col sm="12" md="12" lg="11">

				<!-- <AddMachineStep1 ref="comp_step1" v-bind="stepProps.step1"/> -->
				<AddMachineStep1 ref="comp_step1" v-bind="stepProps.step1"/>

				<template v-if="wizardData.showSteps.step2">
					<!-- <AddMachineStep2 ref="comp_step2" v-bind="stepProps.step2"/> -->
					<AddMachineStep2 ref="comp_step2" v-bind="stepProps.step2"/>
				</template>

				<template v-if="wizardData.showSteps.step3">
					<!-- <AddMachineStep3 ref="comp_step3" v-bind="stepProps.step3"/> -->
					<AddMachineStep3 ref="comp_step3" v-bind="stepProps.step3"/>
				</template>

				<template v-if="wizardData.showSteps.step4">
					<!-- <AddMachineStep4 ref="comp_step4" v-bind="stepProps.step4"/> -->
					<AddMachineStep4 ref="comp_step4" v-bind="stepProps.step4"/>
				</template>

				<template v-if="wizardData.showSteps.step5">
					<AddMachineStep5 ref="comp_step5" v-bind="stepProps.step5"/>
				</template>
			</b-col>
		</b-row>
	</div>
</template>

<script>
import Vue from "vue";
import AddMachineStep1 from "./resetterrf/wizard/AddMachineStep1";
import AddMachineStep2 from "./resetterrf/wizard/AddMachineStep2";
import AddMachineStep3 from "./resetterrf/wizard/AddMachineStep3";
import AddMachineStep4 from "./resetterrf/wizard/AddMachineStep4";
import AddMachineStep5 from "./resetterrf/wizard/AddMachineStep5";

export default {
	name: "AddNewMachine2",
	components: {
		AddMachineStep1,
		AddMachineStep2,
		AddMachineStep3,
		AddMachineStep4,
		AddMachineStep5
	},
	data() {
		return {
			stepProps: {
				step1: {
					input: {
						hubOptions: [
							//test data. The real hub options will be rendered in here by the Laravel server
							{
								label: "Garage home resetter (ID 175)",
								value: 175
							},
							{
								label: "Basement farm hub (ID 13)",
								value: 13
							}
						]
					},
					outputCallback: this.step1_finish
				},
				step2: {
					input: {
						hubID: -1
					},
					outputCallback: this.step2_finish
				},
				step3: {
					input: {
						tableItems: {}
					},
					outputCallback: this.step3_finish
				},
				step4: {
					input: {
						machinesInitial: []
					},
					outputCallback: this.step4_finish
				},
				step5: {
					input: {
						filledMachines: []
					}
				}
			},
			wizardData: {
				showSteps: {
					step1: true,
					step2: false,
					step3: false,
					step4: false,
					step5: false
				}
			}
		};
	},
	methods: {
		step1_finish(result) {
			console.log("Step 1 completed. Result: ");
			console.log(result);

			if (result.success)
				this.stepProps.step2.input.hubID = result.selectedHubID.value;
			this.wizardData.showSteps.step2 = result.success;
		},
		step2_finish(result) {
			console.log("Step 2 completed. Result: ");
			console.log(result);

			if (result.success)
				this.stepProps.step3.input.tableItems = result.hostData;
			this.wizardData.showSteps.step3 = result.success;
		},
		step3_finish(result) {
			console.log("Step 3 completed. Result: ");
			console.log(result);

			if (result.success)
				this.stepProps.step4.input.machinesInitial = result.chosenHosts;
			this.wizardData.showSteps.step4 = result.success;
		},
		step4_finish(result) {
			console.log("Step 4 completed. Result: ");
			console.log(result);

			this.wizardData.showSteps.step5 = result.success;
		}
	},
	mounted() {
		//this.stepComponents.step1 = this.$refs['comp_step1']
	}
};
</script>

<style scoped>
.fade-enter-active {
	transition: all 0.3s ease;
}
.fade-leave-active {
	transition: all 0.8s cubic-bezier(1, 0.5, 0.8, 1);
}
.fade-enter,
.fade-leave-to {
	transform: translateX(10px);
	opacity: 0;
}
</style>
