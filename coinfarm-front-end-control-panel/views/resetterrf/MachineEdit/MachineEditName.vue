<template>
  <b-form-group label="New name for this machine" label-for="me_rename_form">
    <b-input-group>
      <b-input-group-prepend>
        <b-input-group-text>ABC</b-input-group-text>
      </b-input-group-prepend>
      <b-form-input
        ref="me_rename_machineName"
        :value="value"
        @input="onInput($event)"
        :class="inputValidClass"
        type="text"
      />
      <b-input-group-append>
        <b-button @click="processData()" variant="warning">Check name</b-button>
      </b-input-group-append>

      <b-form-valid-feedback>
        <strong>This new name is valid. Press the 'save &amp; apply' button</strong>
      </b-form-valid-feedback>
      <b-form-invalid-feedback>
        <strong>This machine name cannot be used. Reason: {{ errorMsgD }}</strong>
      </b-form-invalid-feedback>
    </b-input-group>
  </b-form-group>
</template>

<script lang="ts">
import _ from "lodash";

interface Err {
  type: "error";
  errorMsg: string;
}

interface Success {
  type: "success";
}

interface NotFilled {
  type: "not_filled";
}

type Output = Err | Success | NotFilled;

import { Vue, Component, Prop, Watch, Emit, Ref } from "vue-property-decorator";

@Component
export default class MachineEditName extends Vue {
  @Prop(String)
  readonly machineCurrentName!: string;

  @Prop(String)
  readonly value!: string;

  @Prop(Number)
  readonly machineDeviceID!: number; //used for identifying the machine when talking to the processor API

  inAction = false;
  processingDone = false;
  inputValidClass = "form-control";
  errorMsgD = "no error";
  output: Output = {
    type: "not_filled"
  };

  onInput(value: string) {
    this.$emit("input", value);
  }

  processData() {
    //placeholder code to simulate the real delay involved
    const loadDelayMs = Math.random() * 400;
    setTimeout(this.processResult, loadDelayMs);
  }

  processResult() {
    //placeholder result until real API call to processor is implemented
    const newNameCmp = this.value.trim();
    const currNameCmp = this.machineCurrentName;

    if (!newNameCmp || newNameCmp == "") {
      this.output = {
        type: "error",
        errorMsg: `Please type a name`
      };
    } else if (newNameCmp === currNameCmp) {
      this.output = {
        type: "error",
        errorMsg: `Identical to current name`
      };
    } else if (Math.random() < 1 - 0.7) {
      this.output = {
        type: "error",
        errorMsg: `Random error`
      };
    } else {
      this.output = {
        type: "success"
      };
    }
    " X";
    if (this.output.type === "error") this.errorMsgD = this.output.errorMsg;

    this.setInputValid(this.output.type === "success");
  }

  setInputValid(valid: boolean) {
    this.inputValidClass = valid
      ? "form-control is-valid"
      : "form-control is-invalid";
  }
}
</script>

<style scoped>
small,
code {
  font-size: 110%;
}

.valid-feedback,
.invalid-feedback {
  font-size: 100%;
}

.form-group,
.me-deviceroutine-div {
  padding-left: 20px;
}
</style>
