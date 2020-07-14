<template>
  <MachineStatusButtonModal v-bind="modalButtonBase" ref="modalButtonBaseComp">
    <div slot="modalContent">
      <MachineEdit
        ref="machineEditComp"
        :machineName="machineName"
        :machineDeviceID="machineDeviceID"
      />
    </div>
  </MachineStatusButtonModal>
</template>

<script>
import MachineStatusButtonModal from "./MachineStatusButtonModal";
import MachineEdit from "./MachineEdit";

export default {
  name: "MachineStatusButtonModalEdit",
  components: {
    MachineStatusButtonModal,
    MachineEdit
  },
  props: {
    machineName: String,
    machineDeviceID: Number //used for identifying the machine when talking to the processor API
  },
  data() {
    return {
      modalButtonBase: {
        buttonBase: {
          buttonText: "Edit & configure",
          buttonColorAccent: "outline-dark",
          shapeClass: "btn-pill",
          withIcon: true,
          iconClass: "cui-brush",
          additionalStyle: ""
          //onClickCallback: this.refreshEditComp
          //onClickCallback: this.editComp.forceEditCompsRefresh
        },
        closeBackdrop: false,
        size: "lg",
        okButtonCallback: this.clickOkFunction,
        dialogTitle: `Edit machine \'${this.machineName}\'`,
        okButtonText: "Save & apply",
        okButtonColor: "dark",
        cancelButtonText: "Cancel & lose changes",
        cancelButtonColor: "outline-danger"
      }
    };
  },
  computed: {
    modalButtonBaseComp() {
      return this.$refs.modalButtonBaseComp;
    },
    editComp() {
      return this.$refs.machineEditComp;
    }
    /*dialogTitleC() {
			return `Edit machine \'${this.machineName}\' routines`;
		}*/
  },
  methods: {
    refreshEditComp() {
      console.log("refreshEditComp(): called");
      //this.editComp.forceEditCompsRefresh();
    },
    clickOkFunction() {
      console.log(
        `Applying edit changes to machine \'${this.machineName}\' with ID \'${this.machineDeviceID}\'... `
      );

      var machineChanges = this.editComp.getMachineChanges();
      console.log(machineChanges);

      //send these changes in an API call to the processor
      //after clicking the modal Apply button:
      //print error/success message of the API call as a CoreUI Toastr popup bubble
    }
  }
};
</script>


