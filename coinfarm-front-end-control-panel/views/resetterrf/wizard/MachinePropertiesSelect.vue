<template>
	<div>
		<b-row>
			<b-col sm="12" md="12" lg="10" xl="9">
				<b-card>
					<b-row>
						<b-col>
							<h5>Local IP: {{ currentMachine.localIP }}</h5>
						</b-col>
					</b-row>
					<b-row class="machinePropsText">
						<b-col><h6>MAC address:</h6></b-col>
						<b-col><h6>NetBIOS name:</h6></b-col>
						<b-col><h6>Status: </h6></b-col>
					</b-row>
					<b-row class="machinePropsText">
						<b-col><code>{{ currentMachine.mac }}</code></b-col>
						<b-col>{{ currentMachine.localName }}</b-col>
						<b-col>
							<b-badge :variant="getStatusBadge(currentMachine.status)">
								{{ currentMachine.status }}
							</b-badge>
						</b-col>
					</b-row>
					<b-row>
						<b-col cols="12">
							<div class="formDivider"></div>
						</b-col>
					</b-row>
					<b-row>
						<b-col cols="1">
							<!-- empty space -->
						</b-col>
						<b-col cols="10">
							<b-form @submit.stop.prevent>
								<b-form-group label="New machine name" :label-cols="4" :horizontal="true">
									<b-input-group>
										<b-form-input v-model="currentFormData.newName" ref="form_newName" type="text" maxlength="30"></b-form-input>
									</b-input-group>
								</b-form-group>

								<b-form-group label="Select device type" :label-cols="4" :horizontal="true">
									<b-form-select v-model="currentFormData.deviceType" ref="form_deviceType" :plain="true"
										:options="deviceTypeOptions"
										:value="deviceTypeOptions[0]">
									</b-form-select>
								</b-form-group>
								<b-form-group label="Choose API port" :label-cols="4" :horizontal="true">
									<b-input-group>
										<b-form-input v-model="currentFormData.apiPort" ref="form_apiPort" type="number" min="1" max="65535" value="3333"></b-form-input>
									</b-input-group>
								</b-form-group>

								<b-row>
									<b-col cols="12">
										<div class="formSubmitContainer">
											<b-button type="submit" ref="form_submitBtn" id="form_submitBtn" class="form-control is-valid" variant="success" @click="saveMachineForm">
												<i class="cui-task"></i> Save info
											</b-button>
											<div id="form_submitBtnPadding"/>
											<b-form-invalid-feedback><strong>{{ currentFormErrorMsg }}</strong></b-form-invalid-feedback>
										</div>
									</b-col>
								</b-row>
							</b-form>
						</b-col>
						<b-col cols="1">
							<!-- empty space -->
						</b-col>
					</b-row>

				</b-card>
			</b-col>
		</b-row>
		<b-row>
			<b-col sm="12" md="10" xl="8">
				<nav>
					<b-pagination :total-rows="machines.length" :per-page="paginationPerPage"
						@input="loadMachineForm" ref="machinePropPagination"
						v-model="currentPageNum" prev-text="Prev" next-text="Next"/>
				</nav>

			</b-col>
		</b-row>
	</div>
</template>

<script>
import Joi from "joi";

export default {
	name: "MachinePropertiesSelect",
	data() {
		return {
			currentPageNum: 1,
			paginationPerPage: 1,
			machines: [],
			deviceTypeOptions: [
				"GPU rig - Claymore/Finminer/other EthMan API",
				"GPU rig - non-EthMan API",
				"ASIC unit - Bitmain",
				"ASIC unit - non-Bitmain",
				"Server/Network appliance/other"
			],
			formDefaults: {},
			currentFormData: {},
			machineFormValidationSchema: {},
			currentFormErrorMsg: ""
		};
	},
	props: {
		machinesInitial: Array
	},
	beforeMount() {
		this.machines = this.machinesInitial;

		this.formDefaults = {
			newName: "",
			deviceType: this.deviceTypeOptions[0],
			apiPort: 3333
		};

		this.currentFormData = JSON.parse(JSON.stringify(this.formDefaults));
		if (this.currentMachine.localName !== "")
			//if the machine has a NetBIOS hostname
			this.currentFormData.newName = this.currentMachine.localName;

		this.machineFormValidationSchema = {
			newName: Joi.string()
				.min(1)
				.max(30)
				.required(),
			deviceType: Joi.any().required(),
			apiPort: Joi.number()
				.min(1)
				.max(65535)
				.integer()
				.required()
		};
	},
	methods: {
		setMachineData(newMachines) {
			this.machines = newMachines.slice(0); //creates copy of array
		},
		getMachineData() {
			return this.machines.slice(0); //creates copy of array
		},
		getStatusBadge(status) {
			return status === "Online"
				? "success"
				: status === "Unresponsive"
					? "secondary"
					: status === "Offline" ? "danger" : "secondary";
		},
		allFormInputSuccessful() {
			for (let i = 0; i < this.machines.length; i++) {
				const machine = this.machines[i];
				//console.log(machine)
				if (!machine.hasOwnProperty("formInput")) return false;
			}

			return true;
		},
		saveMachineForm() {
			const validationError = this.validateMachineForm();
			//console.log(validationResult);

			if (!validationError) {
				this.setFormValid("form_submitBtn", true);
				this.currentMachine.formInput = this.currentFormData;

				if (this.currentPageNum < this.machines.length)
					this.$refs.machinePropPagination.value++; //next page
				//this statement will throw a prop direct mutation warning message
				//but there's no other way to turn the page
			} else {
				this.currentFormErrorMsg = `Invalid form input: ${validationError}`;
				this.setFormValid("form_submitBtn", false);

				delete this.currentMachine.formInput; //invalidates this machine if it was validated in the past
			}
		},
		loadMachineForm() {
			if (this.currentMachine.formInput) {
				//if exists
				this.currentFormData = this.currentMachine.formInput;
			} else {
				this.currentFormData = JSON.parse(
					JSON.stringify(this.formDefaults)
				);
				if (this.currentMachine.localName !== "")
					//if the machine has a NetBIOS hostname
					this.currentFormData.newName = this.currentMachine.localName;
			}
		},
		validateMachineForm() {
			const result = Joi.validate(
				this.currentFormData,
				this.machineFormValidationSchema,
				{ abortEarly: false }
			);
			//console.log(result)
			if (result.error) {
				return result.error.details[0].message;
			}
			return false;
		},
		setFormValid(buttonRef, valid) {
			const buttonClasses = this.$refs[buttonRef].classList;
			if (valid) {
				buttonClasses.remove("is-invalid");
				buttonClasses.add("is-valid");
			} else {
				buttonClasses.remove("is-valid");
				buttonClasses.add("is-invalid");
			}
		}
	},
	computed: {
		currentMachineInd() {
			return this.currentPageNum - 1;
		},
		currentMachine() {
			return this.machines[this.currentMachineInd];
		}
	}
};
</script>

<style scoped>
.machinePropsText {
	font-size: 110%;
}
.formDivider {
	width: 1px;
	height: 20px;
}
.valid-feedback,
.invalid-feedback {
	font-size: 100%;
}

.formSubmitContainer {
	display: flex;
	flex-direction: row;
}

#form_submitBtn {
	width: 27%;
}

#form_submitBtnPadding {
	width: 5%;
}

.b-form-invalid-feedback {
	padding-left: 0.2em;
}
</style>
