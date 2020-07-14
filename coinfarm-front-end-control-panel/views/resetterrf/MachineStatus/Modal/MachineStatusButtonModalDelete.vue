<template>
  <MachineStatusButtonModal v-bind="modalButtonBase" ref="modalButtonBaseComp">
    <div slot="modalContent">
      <p>
        <strong>Are you sure</strong> you want to delete this machine?
      </p>
      <p>
        The machine will keep running, but will be removed from the Resetter RF system.
        You can always reinstate this machine with the 'add new machine' page.
      </p>
    </div>
  </MachineStatusButtonModal>
</template>

<script>
import MachineStatusButtonModal from "./MachineStatusButtonModal";

export default {
  name: "MachineStatusButtonModalDelete",
  components: {
    MachineStatusButtonModal
  },
  props: {
    machineName: String,
    machineDeviceID: Number //used for identifying the machine when talking to the processor API
  },
  data() {
    return {
      modalButtonBase: {
        buttonBase: {
          buttonText: "Delete",
          buttonColorAccent: "outline-danger",
          shapeClass: "btn-pill",
          withIcon: true,
          iconClass: "cui-trash",
          additionalStyle: ""
        },
        closeBackdrop: true,
        okButtonCallback: this.clickOkFunction,
        dialogTitle: `Delete machine \'${this.machineName}\'`,
        okButtonText: "Delete",
        okButtonColor: "outline-danger",
        cancelButtonText: "Cancel",
        cancelButtonColor: "primary"
      }
    };
  },
  computed: {
    modalButtonBaseComp() {
      return this.$refs.modalButtonBaseComp;
    }
    /*dialogTitleC() {
			return `Edit machine \'${this.machineName}\' routines`;
		}*/
  },
  methods: {
    clickOkFunction() {
      console.log(
        `Deleting machine \'${this.machineName}\' with ID \'${this.machineDeviceID}\'...`
      );
    }
  },
  mounted: function() {}
};
</script>


