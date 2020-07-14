<template>
	<div>
		<AddMachineStep_P v-bind="stepBase" ref="stepBaseComp">
			<b-col slot="stepBody" md="11" sm="12">
				<HostTable v-bind="hostTableC" ref="step3_machineTable" hover/>
			</b-col>
			<span slot="buttonLabel">Finish machine selection</span>
			<span slot="successMsgLabel">Successfully picked machines</span>
			<span slot="errorMsgLabel">You made an error in your selections: {{ errorMsgD }}</span>
		</AddMachineStep_P>
	</div>
</template>


<script>
import HostTable from "./HostTable";
import AddMachineStep_P from "./AddMachineStep_P";

export default {
	name: "AddMachineStep3_P",
	components: {
		AddMachineStep_P,
		HostTable
	},
	data() {
		return {
			stepBase: {
				header: {
					accent: "dark",
					heading: "Select machines to add",
					badgeText: "Step 3"
				},
				subheading: "Specify the hosts to be included",
				refPrefix: "step3",
				startProcessingCallback: this.processData,
				buttonStyle: "width: 270px;"
			},

			hostTable: {
				tableFields: [
					{ key: "S" },
					{ key: "mac" },
					{ key: "localIP" },
					{ key: "localName" },
					{ key: "status" }
				],
				currentPage: 1,
				rowsPerPage: 5,
				preSelectedItems: [],
				selectAllFieldName: "mac"
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
			this.processResult();
		},
		processResult() {
			const chosenMachines = this.getChosenMachines();

			if (chosenMachines.length > 0) {
				this.output.success = true;
				this.output.chosenHosts = this.getChosenMachines();
				delete this.output.errorMsg;
			} else {
				this.output.success = false;
				this.output.errorMsg =
					"Please select at least one machine from the table";
			}

			if (this.output.errorMsg) this.errorMsgD = this.output.errorMsg;

			this.stepBaseComp.processingFinished(this.output.success);
			this.outputCallback(this.output);
		},
		getChosenMachines() {
			const hostTableComp = this.hostTableComp;
			const chosenMacs = hostTableComp.getSelectedRows();
			const chosenMachines = [];

			for (let i = 0; i < hostTableComp.getRowCount(); i++) {
				const item = hostTableComp.getTableItems()[i];
				if (chosenMacs.includes(item.mac)) chosenMachines.push(item);
			}

			return chosenMachines;
		}
	},
	computed: {
		stepBaseComp: function() {
			return this.$refs.stepBaseComp;
		},
		hostTableComp: function() {
			return this.$refs.step3_machineTable;
		},
		hostTableC() {
			const hostTableAssembled = this.hostTable;
			hostTableAssembled.tableItems = this.input.tableItems;
			return hostTableAssembled;
		}
	}
};
</script>


<style scoped>
</style>
