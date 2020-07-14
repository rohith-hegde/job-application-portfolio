
<template>
  <div>
    <RoutineTable v-bind="routineTableC" ref="routineTable" />
    <p>[create add new routine component here]</p>
  </div>
</template>


<script lang="ts">
import * as RT from "./RoutineTypes";
import RoutineTable from "./RoutineTable.vue";

import {Component, Vue, Prop} from "vue-property-decorator";

@Component({
  components: {
    RoutineTable
  },
})
export default class MachineEditRoutines extends Vue {

  @Prop()
  input = []

  @Prop()
  outputCallback = () => {}

  routineTable = {
    /*tableFields: [
      { key: "id" },
      { key: "name" },
      { key: "timeCreated" },
      { key: "enabled" },
      { key: "delete" }
    ],
    currentPage: 1,
    rowsPerPage: 10,
    preSelectedItems: [],
    deleteAllFieldName: "id"*/
    rtProps: {
      tableItems:[]
    }
  }

  get routineTableComp() {
      return this.$refs.routineTable;
  }

  get routineTableC() {
    let rtPropsWithInput = this.routineTable; //this.routineTableComp;
    rtPropsWithInput.rtProps.tableItems = this.input;

    //console.log("routine table props input set to: ");
    //console.log(this.input);
    //console.log(rtPropsWithInput);
    return rtPropsWithInput;
  }

  mounted() { }
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

.form-group,
.me-deviceroutine-div {
  padding-left: 20px;
}
</style>