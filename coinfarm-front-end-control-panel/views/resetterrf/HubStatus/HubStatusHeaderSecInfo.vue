<template>
  <span class="secInfo" v-bind:style="additionalStyle">
    <i id="hshsiIcon" v-bind:class="iconClassName" />
    {{ displayText }}
  </span>
</template>

<script>
export default {
  name: "HubStatusHeaderSecInfo",
  components: {},
  data() {
    return {};
  },

  props: {
    iconClassName: String,
    displayText: String,
    additionalStyle: String
  }
};
</script>

<style>
#hshsiIcon {
  font-size: 100%;
}
.secInfo {
  font-size: 90%;
  font-weight: bold;
}
</style>
