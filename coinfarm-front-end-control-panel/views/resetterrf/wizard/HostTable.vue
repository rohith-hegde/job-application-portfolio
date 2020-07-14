<template>
	<div>
		<b-table :hover="hover" :striped="striped" :bordered="bordered" :small="small" :fixed="fixed" responsive="sm" :items="tableItems" :fields="tableFields" :current-page="currentPageC" :per-page="rowsPerPage">
			<template slot="S" slot-scope="row">
				<input type="checkbox" v-model="selected" :value="row.item.mac" checked/>
			</template>
			<template slot="status" slot-scope="row">
				<b-badge :variant="getBadge(row.item.status)">{{ row.item.status }}</b-badge>
			</template>
		</b-table>
		<b-row>
			<b-col sm="8" md="8">
				<nav>
					<b-pagination :total-rows="getRowCount(tableItems)" :per-page="rowsPerPage" v-model="currentPageC" prev-text="Prev" next-text="Next" hide-goto-end-buttons/>
				</nav>

			</b-col>
			<b-col sm="2" md="2">
				<div class="float-right">
					<b-button v-on:click="selectAll(selectAllFieldName)" ref="selectAllButton"
							type="submit" variant="dark">Select all</b-button>
				</div>
			</b-col>
			<b-col sm="2" md="2">
				<div class="float-right">
					<b-button v-on:click="clearAll()" ref="clearAllButton"
							type="submit" variant="secondary">Clear all</b-button>
				</div>
			</b-col>
		</b-row>
	</div>
</template>

<script>
//import cTable from '../tables/Table.vue'

/**
 * Randomize array element order in-place.
 * Using Durstenfeld shuffle algorithm.
 */
const shuffleArray = array => {
	for (let i = array.length - 1; i > 0; i--) {
		const j = Math.floor(Math.random() * (i + 1));
		const temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
	return array;
};

export default {
	name: "HostTable",
	//extends: cTable,
	props: {
		tableItems: Array,
		tableFields: Array,
		currentPage: Number,
		rowsPerPage: Number,
		preSelectedItems: Array,
		selectAllFieldName: String,
		caption: {
			type: String,
			default: "Table"
		},
		hover: {
			type: Boolean,
			default: false
		},
		striped: {
			type: Boolean,
			default: false
		},
		bordered: {
			type: Boolean,
			default: false
		},
		small: {
			type: Boolean,
			default: false
		},
		fixed: {
			type: Boolean,
			default: false
		}
	},
	data() {
		return {
			currentPageC: 1,
			selected: []
		};
	},
	mounted() {
		this.currentPageC = this.currentPage;

		//console.log('pre-selected: ' + this.preSelectedItems)
		this.selected = this.preSelectedItems;
		//console.log('selected: ' + this.selected)
	},
	methods: {
		getBadge(status) {
			return status === "Online"
				? "success"
				: status === "Unresponsive"
					? "secondary"
					: status === "Offline" ? "danger" : "secondary";
		},
		getRowCount() {
			return this.tableItems.length;
		},
		selectAll(fieldName) {
			this.clearAll();
			for (let i = 0; i < this.tableItems.length; i++)
				this.selected.push(this.tableItems[i][fieldName]);
		},
		clearAll() {
			this.selected = [];
		},
		getSelectedRows() {
			return this.selected.slice(0);
		},
		getTableItems() {
			return this.tableItems.slice(0);
		}
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
