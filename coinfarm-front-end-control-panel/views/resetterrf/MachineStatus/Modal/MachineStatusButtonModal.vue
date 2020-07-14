<template>
  <MachineStatusButton v-bind="buttonBase" ref="buttonBaseComp">
    <b-modal
      slot="buttonExtras"
      :class="modalAccentC"
      :ok-variant="okButtonColor"
      :title="dialogTitle"
      :cancel-title="cancelButtonText"
      :cancel-variant="cancelButtonColor"
      :ok-title="okButtonText"
      v-model="modalOpened"
      @ok="clickOkFunction()"
      :no-close-on-backdrop="!closeBackdrop"
      :size="size"
    >
      <slot name="modalContent"></slot>
    </b-modal>
  </MachineStatusButton>
</template>

<script lang="ts">
import MachineStatusButton from "../MachineStatusButton.vue";
import Vue from "vue";
import Component from "vue-class-component";

const Props = Vue.extend({
  props: {
    buttonBase: Object as () => {
      onClickCallback: () => void;
      buttonColorAccent: string;
    },
    closeBackdrop: Boolean,
    size: String,
    okButtonCallback: Function,
    dialogTitle: String,
    okButtonText: String,
    okButtonColor: String,
    cancelButtonText: String,
    cancelButtonColor: String,
    onModalOpened: {
      type: Function,
      default: function() {}
    },
    onModalClosed: {
      type: Function,
      default: function() {}
    }
  }
});

@Component({
  components: {
    MachineStatusButton
  }
})
export default class MachineStatusButtonModal extends Props {
  modalOpened = false;

  $refs!: {
    buttonBaseComp: MachineStatusButton;
  };

  get modalAccentC() {
    return `modal-${this.buttonBase.buttonColorAccent.replace("outline-", "")}`;
  }
  get buttonBaseComp() {
    return this.$refs.buttonBaseComp;
  }
  clickOkFunction() {
    this.okButtonCallback();
    this.closeModal();
  }
  openModal() {
    this.modalOpened = true;
    this.onModalOpened();
    //console.log("Modal opened");
  }
  closeModal() {
    this.modalOpened = false;
    this.onModalClosed();
    //console.log("Modal closed");
  }
  created() {
    //injects the 'open modal' function into the props of the base button component
    this.buttonBase.onClickCallback = this.openModal;
  }
}
</script>


