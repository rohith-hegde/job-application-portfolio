<template>
  <div>
    <AddMachineStep_P v-bind="stepBase" ref="stepBaseComp">
      <b-col slot="stepBody" lg="9" md="10" sm="12">
        <b-form-group
          @submit.stop.prevent
          label="Hub serial number"
          label-for="step2_form_hubSerialNumber"
          description="Hint: the SN# looks like this: JJKQDU-8372344051, and is located on the sticker of your hub"
        >
          <b-row>
            <b-col md="10" sm="12">
              <b-input-group>
                <b-input-group-prepend>
                  <b-input-group-text>ABC-###</b-input-group-text>
                </b-input-group-prepend>
                <b-form-input
                  v-model="hubSerialNumber_input"
                  id="step2_form_hubSerialNumber"
                  class="form-control"
                  type="text"
                  maxlength="17"
                />
              </b-input-group>
            </b-col>
          </b-row>
        </b-form-group>
      </b-col>
      <span slot="buttonLabel">Search for hub</span>
      <span slot="successMsgLabel">Successfully found hub</span>
      <span slot="errorMsgLabel">This hub can't be found. Reason: {{ errorMsgDisplay }}</span>
    </AddMachineStep_P>
  </div>
</template>


<script lang="ts">
import AddMachineStep_P from "./AddMachineStep_P.vue";
import { Vue, Component, Prop, Watch, Emit, Ref } from "vue-property-decorator";
import * as ANHT from "./ANHtypes";

@Component({
  components: {
    AddMachineStep_P
  }
})
export default class AddHubStep2 extends Vue {
  stepBase = {
    header: {
      accent: "primary",
      heading: "Activate hub",
      badgeText: "Step 2"
    },
    subheading: "Input the following information:",
    refPrefix: "step2",
    startProcessingCallback: this.processData,
    buttonStyle: "width: 180px;"
  };

  errorMsgDisplay = "no error";
  output: ANHT.OutputStep2 = { type: "init" };
  hubSerialNumber_input = "";

  @Prop(Function) outputCallback!: () => void;
  @Prop(String) hubGlobalIP_input!: string;
  @Ref() stepBaseComp!: AddMachineStep_P;

  processData() {
    //TODO Fix this type error message by defining the types for 'stepBaseComp'
    this.stepBaseComp.processingStarted();

    //placeholder code to simulate the real delay involved
    const loadDelayMs = Math.random() * 1500;
    setTimeout(this.processResult, loadDelayMs);
  }

  processResult() {
    //placeholder result until real code is implemented

    if (
      !this.hubSerialNumber_input ||
      !this.validSN(this.hubSerialNumber_input)
    ) {
      this.errorMsgDisplay = "Please enter the serial number";
      this.output = { type: "failed", errorMsg: this.errorMsgDisplay };

      //TODO Fix this type error message by defining the types for 'stepBaseComp'
      this.stepBaseComp.processingFinished(false);
      this.outputCallback(this.output);
    } else {
      this.output = {
        type: "successful",
        hubGlobalIP: this.hubGlobalIP_input,
        hubSerialNumber: this.hubSerialNumber_input
      };

      //TODO Fix this type error message by defining the types for 'stepBaseComp'
      this.stepBaseComp.processingFinished(true);
      this.outputCallback(this.output);
    }
  }

  validSN(snStr: String) {
    return true; //placeholder code until real validator is added
  }
}
</script>


<style scoped>
</style>
