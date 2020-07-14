<template>
  <!-- <b-card v-bind:class="cardAccent" v-bind:header='headerHtml'> -->
  <b-card v-bind:class="cardAccent">
    <HubStatusHeader v-bind="exampleStatusHeader" />
    <main>
      <MachineStatus v-for="iMS in machineStatusesSorted" :key="iMS.localIP" v-bind="iMS" />
    </main>
  </b-card>
</template>

<script>
import Vue from "vue";
import _ from "lodash";
import HubStatusHeader from "./HubStatusHeader";
import MachineStatus from "./MachineStatus";

export default {
  name: "HubStatus",
  components: {
    HubStatusHeader,
    MachineStatus
  },
  props: {
    hubName: String,
    hubID: Number,
    accent: String,
    insideText: String,
    onlineStatus: String
  },
  data() {
    return {
      exampleStatusHeader: {
        hubName: this.hubName,
        badgeAccent: this.accent,
        onlineStatus: this.onlineStatus,
        cardBodyExpanded: false,
        cardBodyCollapseID: "error_id_not_set_only_an_example"
      },
      exampleMachineStatuses: [
        {
          iconClassName: "cui-monitor",
          machineName: "RiggerGTX 1070 003",
          machineDeviceID: 54,
          onlineAccentColor: "success",
          onlineTimeText: "Online (3d 8h)",
          localIP: "10.0.0.39",
          hashrate: "276.41 Mh/s",
          heatText: "GPU 74 °C"
        },
        {
          iconClassName: "cui-speedometer",
          machineName: "Blockdestroyer 580 8 gig",
          machineDeviceID: 88,
          onlineAccentColor: "warning",
          onlineTimeText: "Restarting (33s)",
          localIP: "10.0.0.25",
          hashrate: "-- Mh/s",
          heatText: "GPU -- °C"
        },
        {
          iconClassName: "cui-laptop",
          machineName: "Zellecash zhash Ti",
          machineDeviceID: 112,
          onlineAccentColor: "danger",
          onlineTimeText: "Offline (1h 15m)",
          localIP: "10.0.0.188",
          hashrate: "2451 H/s",
          heatText: "--"
        }
      ]
    };
  },
  computed: {
    headerHtml() {
      const hubNameL = this.hubName;

      console.log(
        `${hubNameL} ___ ${this.accent} ___ ${this.onlineStatus} ___ `
      );

      const htmlString =
        `<i class='cui-inbox'/> ${this.hubName} <b-badge variant='${this.accent}'>` +
        `${this.onlineStatus} </b-badge>`;

      return htmlString;
    },
    cardAccent() {
      return `card-accent-${this.accent}`;
    },
    cardBodyCollapseID() {
      return `hubStatusBodyCollapse${this.hubID}`;
    },
    machineStatusesSorted() {
      return _.orderBy(this.exampleMachineStatuses, "machineName");
    }
  },
  beforeMount() {
    this.exampleStatusHeader.cardBodyCollapseID = this.cardBodyCollapseID;

    for (let i = 0; i < 3; i++) {
      /*this.exampleMachineStatuses.push({
        cardBodyCollapseIDin: this.cardBodyCollapseID
	  });*/
      this.exampleMachineStatuses[
        i
      ].cardBodyCollapseIDin = this.cardBodyCollapseID;
    }
  },
  beforeCreate() {}
};
</script>

<style scoped>
main {
  padding-top: 20px;
}
</style>
