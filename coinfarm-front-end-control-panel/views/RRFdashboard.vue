<template>
  <div id="container" class="animated fadeIn">
    <b-row v-for="iHubStatus in exampleHubStatuses" v-bind:key="iHubStatus.accent">
      <b-col lg="12">
        <HubStatus v-bind="iHubStatus"/>
      </b-col>
    </b-row>
  </div>
</template>


<script lang="ts">
import CardLine1ChartExample from "./dashboard/CardLine1ChartExample.vue";
import HubStatus from "./resetterrf/HubStatus.vue";
import authService from "../auth/authService";

export default {
  name: "dashboard",
  components: {
    HubStatus
  },

  data() {
    return {
      exampleHubStatuses: [
        {
          hubName: "Garage home resetter",
          hubID: 1,
          accent: "success",
          insideText: "status detailed info here!",
          onlineStatus: "4/4 online"
        },
        {
          hubName: "Basement farm hub",
          hubID: 2,
          accent: "danger",
          insideText: "status detailed info here!",
          onlineStatus: "1/3 online"
        }
      ]
    };
  }
};
</script>

<style scoped>
/* IE fix */
#card-chart-01,
#card-chart-02 {
  width: 100% !important;
}
/*
#container {
   display: flex;
  flex-direction: column;
  align-items: baseline;
  justify-content: space-around;
} */

</style>
