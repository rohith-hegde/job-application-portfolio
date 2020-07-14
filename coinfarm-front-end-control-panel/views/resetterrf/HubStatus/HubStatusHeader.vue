<template>
  <section slot="header">
    <header>
      <i id="hhnIcon" class="cui-inbox" />
      <h4>{{ hubName }}</h4>

      <b-badge id="hubHeaderBadge" :variant="badgeAccent">
        <span id="hubHeaderBadgeContent">
          <i class="cui-monitor" />
          {{ onlineStatus }}
        </span>
      </b-badge>
    </header>

    <div id="hubHeaderRight">
      <div id="secondaryInfoTable">
        <HubStatusHeaderSecInfo v-bind="secondaryInfoTest[0]" />
        <HubStatusHeaderSecInfo v-bind="secondaryInfoTest[1]" />
        <HubStatusHeaderSecInfo v-bind="secondaryInfoTest[2]" />
        <HubStatusHeaderSecInfo v-bind="secondaryInfoTest[3]" />
      </div>

      <b-button
        v-b-toggle="cardBodyCollapseID"
        variant="outline-primary"
        :pressed.sync="cardBodyExpanded"
        style="font-size: 110%;"
      >
        <i id="cardBodyExpandButtonIcon" :class="cardBodyExpandButtonIcon" />
        {{ cardBodyExpanded ? 'Hide machines' : 'Show machines' }}
      </b-button>
      <!-- v-b-toggle.{{cardBodyCollapseID}} v-bind:aria-controls="cardBodyCollapseID" -->
    </div>
  </section>
</template>

<script>
import HubStatusHeaderSecInfo from "./HubStatusHeaderSecInfo";

export default {
  name: "HubStatusHeader",
  components: {
    HubStatusHeaderSecInfo
  },
  data() {
    return {
      secondaryInfoTest: [
        {
          iconClassName: "cui-inbox",
          displayText: "Hub online",
          additionalStyle: "color: var(--success); font-weight: bold;"
        },
        {
          iconClassName: "cui-puzzle",
          displayText: "v0.1.0",
          additionalStyle: "color: var(--purple);"
        },
        {
          iconClassName: "cui-cloud",
          displayText: "LAN: 10.0.0.217",
          additionalStyle: "color: var(--info);"
        },
        {
          iconClassName: "cui-sun",
          displayText: "air 29° C",
          additionalStyle: "color: var(--red)"
        }
      ],
      cardBodyExpanded: false
    };
  },
  computed: {
    cardBodyExpandButtonIcon() {
      return this.cardBodyExpanded ? "cui-chevron-top" : "cui-chevron-bottom";
    }
  },
  mounted() {
    this.cardBodyExpanded = this.cardBodyExpandedInitial;
  },

  props: {
    hubName: String,
    badgeAccent: String,
    onlineStatus: String,
    cardBodyExpandedInitial: Boolean,
    cardBodyCollapseID: String
  }
};
</script>

<style scoped>
section {
  display: flex;
  flex-direction: column;
}
#hubHeaderRight {
  font-size: 100%;
  margin-left: 1vw;
  display: inline-flex;
  align-content: space-between;
  flex-direction: row;
}
#secondaryInfoTable {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr 1fr;
  grid-gap: 5px;
  justify-self: flex-end;
}
@media screen and (min-width: 553px) {
  section,
  header {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: space-between;
    align-items: baseline;
  }

  header > * {
    margin: 5px;
  }

  h4 {
    display: inline;
  }
  #hubHeaderBadgeContent {
    vertical-align: text-top;
  }
  #hubHeaderBadge {
    font-size: 110%;
  }
  #hhnIcon {
    font-size: 150%;
  }
  /* .hubHeaderName {
    font-size: 150%;
} */

  .btn:focus,
  .btn:active {
    outline: none;
    box-shadow: none;
  }
  #cardBodyExpandButtonIcon {
    font-size: 130%;
    vertical-align: text-bottom;
  }
}
</style>
