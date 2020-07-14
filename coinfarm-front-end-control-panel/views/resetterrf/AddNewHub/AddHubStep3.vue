<template>
  <div>
    <AddMachineStep_P v-bind="stepBase" ref="stepBaseComp">
      <b-col slot="stepBody" lg="9" md="10" sm="12">
        <b-form-group
          @submit.stop.prevent
          label="Desired nickname for hub"
          label-for="step3_form_hubName"
        >
          <b-row>
            <b-col md="10" sm="12">
              <b-input-group>
                <b-input-group-prepend>
                  <b-input-group-text>ABC</b-input-group-text>
                </b-input-group-prepend>
                <b-form-input
                  v-model="hubName_input"
                  id="step3_form_hubName"
                  class="form-control"
                  type="text"
                  maxlength="45"
                />
              </b-input-group>
            </b-col>
          </b-row>
        </b-form-group>
      </b-col>
      <span slot="buttonLabel">Finish adding hub</span>
      <span slot="successMsgLabel">Successfully registered hub. You can now add machines to it</span>
      <span slot="errorMsgLabel">Error registering hub: {{ errorMsgDisplay }}</span>
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
export default class AddHubStep3 extends Vue {
  stepBase = {
    header: {
      accent: "success",
      heading: "Register hub",
      badgeText: "Step 3"
    },
    subheading: "Input the following information:",
    refPrefix: "step3",
    startProcessingCallback: this.processData,
    buttonStyle: "width: 200px;"
  };

  errorMsgDisplay = "no error";
  output: ANHT.OutputStep3 = { type: "init" };
  hubName_input = "";

  @Prop(String) hubGlobalIP_input!: string;
  @Prop(String) hubSerialNumber_input!: string;
  @Ref() stepBaseComp!: AddMachineStep_P;
  @Prop(Function) outputCallback!: () => void;

  processData() {
    this.stepBaseComp.processingStarted();

    //placeholder code to simulate the real delay involved
    const loadDelayMs = Math.random() * 1500;
    setTimeout(this.processResult, loadDelayMs);
  }

  processResult() {
    //placeholder result until real code is implemented

    if (!this.hubName_input || !this.validHubName(this.hubName_input)) {
      this.errorMsgDisplay = "Please enter the name of the new hub";
      this.output = { type: "failed", errorMsg: this.errorMsgDisplay };

      this.addNewHubAPIcall();

      //TODO Fix this type error message by defining the types for 'stepBaseComp'
      this.stepBaseComp.processingFinished(false);
      this.outputCallback(this.output);
    } else {
      this.output = { type: "successful" };
      this.stepBaseComp.processingFinished(true);
      this.outputCallback(this.output);
    }
  }

  validHubName(nameStr: String) {
    return true; //placeholder code until real validator is added
  }

  addNewHubAPIcall() {
    //placeholder code
  }
}
</script>


<style scoped>
</style>
