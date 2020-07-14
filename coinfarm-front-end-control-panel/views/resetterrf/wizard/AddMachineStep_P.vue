<template>
  <b-card :border-variant="header.accent">
    <div slot="header">
      <StepCardHeader v-bind="header" />
    </div>
    <div>
      <h5>{{ subheading }}</h5>
      <b-row>
        <slot name="stepBody"></slot>
      </b-row>
      <br />
      <b-row>
        <b-col md="12" sm="12">
          <div class="flex-container-step">
            <b-button
              v-on:click="startProcessingCallback()"
              :ref="this.getRefPrefix() + '_finishButton'"
              type="submit"
              variant="primary"
              :class="buttonValidClass"
              :disabled="buttonDisabled"
            >
              <slot name="buttonLabel"></slot>
            </b-button>

            <SubheadingSpinner
              :containerRef="this.getRefPrefix() + '_loadingSpinner_container'"
              :ref="getRefPrefix() + '_loadingSpinner'"
            />

            <b-form-valid-feedback class="form-feedback-step">
              <strong>
                <slot name="successMsgLabel"></slot>
              </strong>
            </b-form-valid-feedback>
            <b-form-invalid-feedback class="form-feedback-step">
              <strong>
                <slot name="errorMsgLabel"></slot>
              </strong>
            </b-form-invalid-feedback>
          </div>
        </b-col>
      </b-row>
    </div>
  </b-card>
</template>

<script lang="ts">
import StepCardHeader from "./StepCardHeader.vue";
import SubheadingSpinner from "./SubheadingSpinner.vue";
import { Vue, Component, Prop, Watch, Emit, Ref } from "vue-property-decorator";

@Component({
  components: {
    SubheadingSpinner,
    StepCardHeader
  }
})
export default class AddMachineStep_P extends Vue {
  inAction = false;
  processingDone = false;
  buttonValidClass = "form-control";
  buttonDisabled = false;
  buttonStyleL = "width: 20%;"; //default value

  @Prop(Object) header!: object;
  @Prop(String) subheading!: string;
  @Prop(String) buttonStyle!: string;
  @Prop(String) refPrefix!: string;
  @Prop(Function) startProcessingCallback!: () => void;

  processingStarted() {
    if (this.inAction) return; //so the user can't spam click the button

    this.inAction = true;
    this.processingDone = false;

    //TODO Fix this type error by defining types for 'loadingSpinnerComp'
    if (this.loadingSpinnerComp) this.loadingSpinnerComp.setSpinnerShow(true);
  }

  processingFinished(success: boolean) {
    //TODO Fix this type error by defining types for 'loadingSpinnerComp'
    if (this.loadingSpinnerComp) this.loadingSpinnerComp.setSpinnerShow(false);

    this.inAction = false;
    this.processingDone = true;

    this.buttonDisabled = success;
    this.setButtonValid(success);
  }

  setButtonValid(valid: boolean) {
    this.buttonValidClass = valid
      ? "form-control is-valid"
      : "form-control is-invalid";
  }

  getRefPrefix() {
    return this.refPrefix;
  }

  get loadingSpinnerComp() {
    return this.$refs[this.getRefPrefix() + "_loadingSpinner"];
  }

  get buttonComp() {
    return this.$refs[this.getRefPrefix() + "_finishButton"];
  }

  mounted() {
    this.buttonComp.style = this.buttonStyle;
    //ignore this type error since it's literally the CSS style of the button
  }
}
</script>

<style>
h5 {
  padding-bottom: 10px;
}
.flex-container-step {
  display: flex;
  flex-direction: row;
}
.form-feedback-step {
  padding-left: 20px;
  vertical-align: sub;
  font-size: 100%;
}
</style>
