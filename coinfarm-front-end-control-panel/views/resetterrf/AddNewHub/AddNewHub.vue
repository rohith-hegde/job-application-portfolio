<template>
  <div class="animated fadeIn">
    <b-row align-h="center">
      <b-col sm="12" md="12" lg="11">
        <AddHubStep1 ref="comp_step1" v-bind="stepProps.step1" />

        <template v-if="wizardData.showSteps.step2">
          <AddHubStep2 ref="comp_step2" v-bind="stepProps.step2" />
        </template>

        <template v-if="wizardData.showSteps.step3">
          <AddHubStep3 ref="comp_step3" v-bind="stepProps.step3" />
        </template>
      </b-col>
    </b-row>
  </div>
</template>

<script lang="ts">
import StepCardHeader from "./StepCardHeader.vue";
import AddHubStep1 from "./AddHubStep1.vue";
import AddHubStep2 from "./AddHubStep2.vue";
import AddHubStep3 from "./AddHubStep3.vue";
import * as ANHT from "./ANHtypes";
import { Component, Prop, Vue } from "vue-property-decorator";

@Component({
  components: {
    StepCardHeader,
    AddHubStep1,
    AddHubStep2,
    AddHubStep3
  }
})
export default class AddNewHub extends Vue {
  stepProps = {
    step1: {
      outputCallback: this.step1_finish
    },
    step2: {
      hubGlobalIP_input: "",
      outputCallback: this.step2_finish
    },
    step3: {
      hubGlobalIP_input: "",
      hubSerialNumber_input: "",
      outputCallback: this.step3_finish
    }
  };

  wizardData = {
    showSteps: {
      step1: true,
      step2: false,
      step3: false
    }
  };

  step1_finish(result: ANHT.OutputStep1_successful | ANHT.OutputStep1_failed) {
    console.log("Step 1 completed. Result: ");
    console.log(result);

    if (result.type === "successful") {
      this.stepProps.step2.hubGlobalIP_input = result.hubGlobalIP;
      this.wizardData.showSteps.step2 = true; //result.success;
    } else this.wizardData.showSteps.step2 = false;
  }

  step2_finish(result: ANHT.OutputStep2_successful | ANHT.OutputStep2_failed) {
    console.log("Step 2 completed. Result: ");
    console.log(result);

    if (result.type === "successful") {
      this.stepProps.step3.hubSerialNumber_input = result.hubSerialNumber;
      this.stepProps.step3.hubGlobalIP_input = result.hubGlobalIP;
    }
    this.wizardData.showSteps.step3 = true; //result.success;
  }

  step3_finish(result: ANHT.OutputStep3) {
    console.log("Step 3 completed. Result: ");
    console.log(result);
  }
}
</script>

<style>
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

small,
code {
  font-size: 110%;
}

.valid-feedback,
.invalid-feedback {
  font-size: 100%;
}
</style>
