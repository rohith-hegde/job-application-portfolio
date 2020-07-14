<template>
  <div class="animated fadeIn">
    <b-row>
      <b-col sm="9" md="6">
        <b-card header="Group configuration" border-variant="warning">
          Sorry but this <strong>feature is unavailable</strong>. The system currently doesn't
          support grouping machines and/or hubs together and assigning routines to them. Please
          <strong>contact support</strong> if you want to have the deviceroutines edited
          for each individual machine.
        </b-card>
      </b-col>
    </b-row>
  </div>
</template>

<script>
export default {
	name: 'groupconfiguration',
	data() {
		return {
			show: true,
		}
	},
}
</script>

<style scoped>
.fade-enter-active {
	transition: all 0.3s ease;
}
.fade-leave-active {
	transition: all 0.8s cubic-bezier(1, 0.5, 0.8, 1);
}
.fade-enter,
.fade-leave-to {
	transform: translateX(10px);
	opacity: 0;
}
</style>
