<template>
  <div class="button-container">
    <b-button
      @click="clickTest()"
      :variant="buttonColorAccent"
      size="sm"
      :class="shapeClassC"
      :disabled="disabled"
    >
      <i :class="iconClass" :style="iconStyleC" />
      <span class="buttonText">{{ (withIcon ? "&nbsp;" : "") + buttonText }}</span>

      <slot name="buttonExtras"></slot>
      <!-- used for extra features like modals and editing -->
    </b-button>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import Component from "vue-class-component";

const Props = Vue.extend({
  props: {
    buttonText: String,
    buttonColorAccent: String,
    shapeClass: {
      type: String,
      default: "btn-pill"
    },
    withIcon: Boolean,
    iconClass: String,
    additionalStyle: String,
    disabled: Boolean,
    onClickCallback: Function
  }
});

@Component
export default class MachineStatusButton extends Props {
  get iconStyleC() {
    return this.withIcon ? this.additionalStyle : "display: none;";
  }

  clickTest() {
    //console.log("kliked");
    this.onClickCallback();
  }
}
</script>

<style scoped>
.button-container {
  display: flex;
  flex-direction: column;
}

#edit-button {
  text-align: center;
}

@media screen and (min-width: 610px) {
  .button-container {
    display: inline-block;
  }
}

.buttonText {
  vertical-align: text-bottom;
}

.btn-sm {
  font-size: 0.82rem;
}
</style>
