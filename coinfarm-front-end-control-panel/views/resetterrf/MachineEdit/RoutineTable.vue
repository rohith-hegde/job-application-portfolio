<template>
  <div>
    <b-table
      v-if="renderComponent"
      :hover="hover"
      :striped="striped"
      :bordered="bordered"
      :small="small"
      :fixed="fixed"
      responsive="sm"
      :items="tableItemsS"
      :fields="tableFields"
      :current-page="currentPageC"
      :per-page="rowsPerPage"
    >
      <template slot="timeCreated" slot-scope="row">
        {{ reformatDate(row.item.timeCreated) }}
        <!-- (row.item.timeCreated + "").split("GMT")[0].trim() -->
      </template>
      <template slot="enabled" slot-scope="row">
        <!-- <input type="checkbox" :value="row.item.enabled" />-->
        <!-- v-model="selected" checked -->
        <b-form-checkbox v-model="row.item.enabled" />
      </template>
      <template slot="button" slot-scope="row">
        <!-- <b-badge :variant="getBadge(row.item.status)">{{ row.item.status }}</b-badge> -->
        <b-button
          v-on:click="deleteRow(row.item.id)"
          variant="outline-danger"
          size="sm"
          class="btn-pill"
        >
          Delete
          <!-- {{ rowDeleted(row.item.id) ? "Deleted" : "Delete" }} -->
        </b-button>
        <!-- [delete button here] (typeof row.item == "undefined") :disabled="rowDeleted(row.item.id)" -->
      </template>
    </b-table>
    <b-row>
      <b-col sm="6" md="6">
        <nav>
          <b-pagination
            :total-rows="getRowCount()"
            :per-page="rtProps.rowsPerPage"
            v-model="currentPageC"
            prev-text="Prev"
            next-text="Next"
            hide-goto-end-buttons
          />
        </nav>
      </b-col>
      <b-col sm="2" md="2">
        <div class="float-right">
          <b-button
            v-on:click="enableAll()"
            ref="enableAllButton"
            type="submit"
            variant="dark"
          >Enable all</b-button>
        </div>
      </b-col>
      <b-col sm="2" md="2">
        <div class="float-right">
          <b-button
            v-on:click="disableAll()"
            ref="disableAllButton"
            type="submit"
            variant="secondary"
          >Disable all</b-button>
        </div>
      </b-col>
      <b-col sm="2" md="2">
        <div class="float-right">
          <b-button
            v-on:click="deleteAll()"
            ref="deleteAllButton"
            type="submit"
            variant="outline-danger"
          >Delete all</b-button>
        </div>
      </b-col>
    </b-row>
  </div>
</template>

<script lang="ts">
import Vue, { ComponentOptions } from "vue";
import * as RT from "./RoutineTypes";
import moment from "moment";
import {Component, Prop} from 'vue-property-decorator';

/**
 * Randomize array element order in-place.
 * Using Durstenfeld shuffle algorithm.
 */
const shuffleArray = <T>(array: T[]) => {
  for (let i = array.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    const temp = array[i];
    array[i] = array[j];
    array[j] = temp;
  }
  return array;
};

@Component
export default class RoutineTable extends Vue {
  
  @Prop({default:[
    { key: "id" },
    { key: "name" },
    { key: "kind" },
    { key: "timeCreated" },
    { key: "enabled" },
    { key: "button" }
  ]})
  readonly tableFields!:{key:string}[]

  // TODO: What is the type for this?
  @Prop({default:[]}) 
  readonly tableItems!:{id:string, enabled:boolean}[]
  
  @Prop({default:1})
  readonly currentPage!:number
  
  @Prop({default:5})
  readonly rowsPerPage!:number
  
  @Prop({default:"id"})
  readonly deleteAllFieldName!:string 
  
  @Prop({default:"Table"})
  readonly caption!:string  

  @Prop({default:false})
  readonly hover!:boolean     

  @Prop({default:false})
  readonly striped!:boolean 

  @Prop({default:false})
  readonly bordered!:boolean  

  @Prop({default:false})
  readonly small!:boolean   
    
  @Prop({default:false})
  readonly fixed!:boolean     

  currentPageC = 1
  renderComponent = true

  mounted() {
    this.currentPageC = this.currentPage;
  }
  
  tableItemsState = this.tableItems

  /*getBadge(status) {
			return status === "Online"
				? "success"
				: status === "Unresponsive"
					? "secondary"
					: status === "Offline" ? "danger" : "secondary";
		},*/
    forceTableRefresh() {
      this.renderComponent = false;

      this.$nextTick(() => {
        // Add the component back in
        this.renderComponent = true;
      });
    }
    
    reformatDate(timeCreated: Date): string {
      let weekday = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"][
        timeCreated.getDay()
      ];
      return weekday + " " + moment(timeCreated).format("YYYY-MM-DD HH:mm:ss");
    }

    getRowCount() {
      return this.tableItemsState.length;
    }
    /*selectAll(fieldName) {
			this.clearAll();
			for (let i = 0; i < this.tableItems.length; i++)
				this.selected.push(this.tableItems[i][fieldName]);
		}
		clearAll() {
			this.selected = [];
		}*/
    deleteRow(routineID:string) {
      for (var i = 0; i < this.tableItemsState.length; i++)
        if (this.tableItemsState[i].id === routineID) 
          this.tableItemsState.splice(i, 1);
      this.forceTableRefresh();
    }
    /*rowDeleted(routineID) {
			let deleted = true;
			this.tableItemsState.forEach(existRoutine => {
				if (deleted) {
					if (existRoutine.id == routineID) deleted = false;
				}
			});

			return deleted;
		}*/
    enableAll() {
      this.tableItemsState.forEach(existRoutine => {
        existRoutine.enabled = true;
      });
    }
    disableAll() {
      this.tableItemsState.forEach(existRoutine => {
        existRoutine.enabled = false;
      });
    }

    deleteAll() {
      this.tableItemsState.splice(0, this.tableItemsState.length);
      //console.log("All table items deleted");
      this.forceTableRefresh();
    }

    getTableItems() {
      return this.tableItemsState.slice(0);
    }
};
</script>

<style scoped>
.badge {
  font-size: 90%;
}
.flex-container {
  display: flex;
  flex-direction: row;
  margin-right: 20px;
}
</style>
