<template>
  <div>
    <AddMachineStep_P v-bind="stepBase" ref="stepBaseComp">
      <b-col slot="stepBody" md="8" sm="10">
        <ul>
          <li>
            Attach the
            <strong>antenna module</strong> as shown in the user manual
          </li>
          <li>Ensure the micro SD card is securely pushed in</li>
          <li>Connect the micro USB power adapter and Ethernet cable</li>
          <li>Make sure the Ethernet port lights are blinking or lit</li>
          <li>Wait 120 seconds for the boot to complete</li>
        </ul>
        <br />

        <h5>Confirm the hub is connected to the RRF system</h5>

        <b-form-group
          @submit.stop.prevent
          label="Public IP address"
          label-for="step1_form_hubGlobalIP"
          description="This is the public IP address of the network your hub is connected to"
        >
          <b-row>
            <b-col md="9" sm="12">
              <b-input-group>
                <b-input-group-prepend>
                  <b-input-group-text>#.#.#.#</b-input-group-text>
                </b-input-group-prepend>
                <b-form-input
                  v-model="hubGlobalIP_input"
                  id="step1_form_hubGlobalIP"
                  class="form-control"
                  type="text"
                  maxlength="17"
                />
              </b-input-group>
            </b-col>
          </b-row>
        </b-form-group>
      </b-col>
      <span slot="buttonLabel">Confirm hub is online</span>
      <span slot="successMsgLabel">Successfully detected hub</span>
      <span slot="errorMsgLabel">
        This hub can't be found. Reason:
        {{ errorMsgDisplay }}
      </span>
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
export default class AddHubStep1 extends Vue {
  stepBase = {
    header: {
      accent: "danger",
      heading: "Before you start",
      badgeText: "Step 1"
    },
    subheading: "Start the Resetter RF hub up:",
    refPrefix: "step1",
    startProcessingCallback: this.processData,
    buttonStyle: "width: 240px;"
  };
  errorMsgDisplay = "no error";
  output: ANHT.OutputStep1 = { type: "init" };
  hubGlobalIP_input = "";
  //@Prop(Object) input!: object;
  @Prop(Function) outputCallback!: () => void;
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

    if (!this.hubGlobalIP_input || !this.validIP(this.hubGlobalIP_input)) {
      this.errorMsgDisplay = "Please enter an IPv4 address";
      this.output = { type: "failed", errorMsg: this.errorMsgDisplay };

      this.stepBaseComp.processingFinished(false);
      this.outputCallback(this.output);
    } else {
      //TODO Fix this type error message by defining the types for 'stepBaseComp'
      this.output = { type: "successful", hubGlobalIP: this.hubGlobalIP_input };

      this.stepBaseComp.processingFinished(false);
      this.outputCallback(this.output);
    }
  }

  validIP(ipStr: String) {
    return true; //placeholder code until real validator is added
  }
}
</script>


<style scoped>
</style>
