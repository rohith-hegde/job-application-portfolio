<template>
  <b-modal :title="dialogTitle"></b-modal>
</template>


<script lang="ts">
import Vue from 'vue';
const Props = Vue.extend({
  props: {
    dialogTitle: String,
    innerComponent: Object
  }
})
export default class MachineStatusModal extends Props {
  open = false
  
  hello(){
    
  }
};
</script>

<style>
</style>
