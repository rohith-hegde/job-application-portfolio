<template>
  <div v-if="renderEditComps">
    <b-row>
      <!-- machine name edit component -->
      <b-col sm="12" md="9" lg="8">
        <b-card>
          <h5>
            <i class="cui-pencil" /> Rename the machine
          </h5>
          <div class="me-subcmp-div">
            <MachineEditName
              v-model="editNameValue"
              ref="editNameComp"
              :machineCurrentName="machineName"
              :machineDeviceID="machineDeviceID"
            />
          </div>
        </b-card>
      </b-col>
    </b-row>
    <b-row>
      <!-- deviceroutine edit component -->
      <b-col sm="12" md="12" lg="12">
        <b-card>
          <h5>
            <i class="cui-list" /> Modify resetting routines
          </h5>

          <div class="me-subcmp-div">
            <br />
            <MachineEditRoutines :input="getExistingRoutineData()" ref="editRoutineComp" />
          </div>
        </b-card>
      </b-col>
    </b-row>
  </div>
</template>

<script lang="ts">
import MachineEditName from "./MachineEditName.vue";
import MachineEditRoutines from "./MachineEditRoutines.vue";
import testRoutineTableData from "./TestRoutineTableData";

import { Vue, Component, Prop, Watch, Emit, Ref } from "vue-property-decorator";

@Component({
  components: {
    MachineEditName,
    MachineEditRoutines
  },
})
export default class MachineEdit extends Vue {
  @Prop(String) readonly machineName!: string;
  /**
   * used for identifying the machine when talking to the processor API
   */
  @Prop(Number) readonly machineDeviceID!: number;

  editNameValue = "machinename" //this.machineName, //might be causeing sticky name bug
  renderEditComps = true
  //modalOpenCount: 0

  //Method 1: user-friendly forms to set deviceroutines
  //online only from <time of day> to <time of day>
  //online only for <# hours> from now | shut down/reset <# hours> from now
  //hard reset every regular <interval> hours
  //...
  //Method 2: allow user to edit table directly
  //1 row for each deviceroutine
  //Columns: [Name], Action, Executions Remaining, Next Execution Time, Repeat Time (RDS)

  //SELECTED AS OF 6/7/19: Method 1

  @Ref("editNameComp") 
  readonly editNameComp!: MachineEditName;
  
  @Ref("editRoutineComp") 
  readonly editRoutineComp!: MachineEditRoutines;

  forceEditCompsRefresh() {
    this.renderEditComps = false;

    this.$nextTick(() => {
      // Add the component back in
      this.renderEditComps = true;
      console.log("Edit components refreshed.");
    });
  }
  getMachineChanges() {
    //placeholder code until actual edit components are finished
    const changes = { machineName:"" };

    //retrieve new machine name from editNameComp
    changes.machineName = this.editNameValue; //this.editNameComp.getChangedName();
    console.log(
      "MachineEdit.getMachineChanges(): changed name: " +
        changes.machineName +
        ", wrapper value: " +
        this.editNameValue
    );

    //retrieve resetting routine changes (if any) from editRoutinesComp

    return changes;
  }

  getExistingRoutineData() {
    return testRoutineTableData;
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

/*.form-group,
.me-deviceroutine-div {
	padding-left: 20px;
}*/

.me-subcmp-div {
  padding-left: 20px;
}
</style>
