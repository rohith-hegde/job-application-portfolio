<template>
  <b-collapse style="padding:0; padding-bottom:1vh;" class="card-body" :id="cardBodyCollapseIDin">
    <div :style="collapseContentStyle">
      <section>
        <header>
          <i id="mhnIcon" :class="iconClassName" />
          <h1 class="machineHeaderName">{{ machineName }}</h1>
          <b-badge id="machineHeaderBadge" :variant="onlineAccentColor">
            <span id="machineHeaderBadgeContent">{{ onlineTimeText }}</span>
          </b-badge>
        </header>
        <main>
          <HubStatusHeaderSecInfo
            v-for="iHSHSI in secondaryInfoTestSorted"
            :key="iHSHSI.position"
            v-bind="iHSHSI"
          />
        </main>
        <div id="edit-buttons" class="buttons">
          a
          <!-- Edit controls here: edit DR's, edit machine, delete -->

          <MachineStatusButtonModalEdit v-bind="buttonModalEdit" />

          <MachineStatusButtonModalDelete v-bind="buttonModalDelete" />
        </div>
        <div id="status-buttons" class="buttons">
          <!-- Power controls here: manual mode, hard reset, power on/off -->

          <MachineStatusButtonReset v-bind="buttonReset" />
          <MachineStatusButtonPowerOn v-bind="buttonPowerOn" />
          <MachineStatusButtonPowerOff v-bind="buttonPowerOff" />
        </div>
      </section>
    </div>
  </b-collapse>
</template>

<script>
import _ from "lodash";
import HubStatusHeaderSecInfo from "./HubStatusHeaderSecInfo";

import MachineStatusButtonReset from "./MachineStatusButtonReset";
import MachineStatusButtonPowerOn from "./MachineStatusButtonPowerOn";
import MachineStatusButtonPowerOff from "./MachineStatusButtonPowerOff";

import MachineStatusButtonModalEdit from "./MachineStatusButtonModalEdit";
import MachineStatusButtonModalDelete from "./MachineStatusButtonModalDelete";

export default {
  name: "MachineStatus",
  components: {
    HubStatusHeaderSecInfo,
    MachineStatusButtonReset,
    MachineStatusButtonPowerOn,
    MachineStatusButtonPowerOff,
    MachineStatusButtonModalEdit,
    MachineStatusButtonModalDelete
  },
  data() {
    return {
      buttonModalEdit: {
        machineName: this.machineName,
        machineDeviceID: this.machineDeviceID
      },
      buttonModalDelete: {
        machineName: this.machineName,
        machineDeviceID: this.machineDeviceID
      },
      buttonReset: {
        machineName: this.machineName,
        machineDeviceID: this.machineDeviceID
      },
      buttonPowerOn: {
        machineName: this.machineName,
        machineDeviceID: this.machineDeviceID
      },
      buttonPowerOff: {
        machineName: this.machineName,
        machineDeviceID: this.machineDeviceID
      },

      secondaryInfoTest: [
        {
          position: 3,
          iconClassName: "cui-cloud",
          displayText: this.localIP,
          additionalStyle: "color: var(--info); padding-left: 10px;"
        },
        {
          position: 2,
          iconClassName: "cui-sun",
          displayText: this.heatText,
          additionalStyle: "color: var(--red); padding-left: 10px;"
        },
        {
          position: 1,
          iconClassName: "cui-code",
          displayText: this.hashrate,
          additionalStyle: "color: var(--indigo); padding-left: 10px;"
        }
      ]
    };
  },
  props: {
    cardBodyCollapseIDin: String,
    iconClassName: String,
    machineName: String,
    machineDeviceID: Number,
    onlineAccentColor: String,
    onlineTimeText: String,
    localIP: String,
    hashrate: String,
    heatText: String
  },
  computed: {
    collapseContentStyle() {
      return (
        `border: 1px solid var(--${this.onlineAccentColor}); ` +
        `padding: 0.3rem; border-radius: 12px; background-color: #F8F8F8;`
      );
    },
    secondaryInfoTestSorted() {
      const ret = _.orderBy(this.secondaryInfoTest, "position");
      //console.log(ret)
      return ret;
    }
  }
};
</script>

<style scoped>
section {
  padding: 0;
}

.buttons {
  display: flex;
  flex-direction: column;
  text-align: center;
}
.buttons > * {
  margin: 0.5vh;
}

@media screen and (min-width: 610px) {
  section {
    display: grid;
    grid-template:
      "header stats" 1fr
      "edit status" 1fr
      / 1fr 1fr;
    align-items: baseline;
  }

  header {
    grid-area: header;
    display: inline-flex;
    align-items: baseline;
  }
  header > * {
    margin: 0.7vw;
  }

  main {
    grid-area: stats;
  }

  div#edit-buttons {
    grid-area: edit;
  }

  div#status-buttons {
    grid-area: status;
  }

  .buttons {
    display: inline-block;
    text-align: left;
  }

  .buttons > * {
    margin: 0.5vh;
    display: inline-block;
  }
}
#machineHeaderBadgeContent {
  padding: 5px;
  vertical-align: text-top;
}

#machineHeaderBadge {
  font-size: 90%;
  border-radius: 8px;
}
#mhnIcon {
  font-size: 140%;
}
.machineHeaderName {
  font-size: 115%;
  vertical-align: text-bottom;
}

#hubHeaderRight {
  font-size: 100%;
}
</style>
