<template>
	<div class="spinner-container" :ref="containerRef">
		<div class="spinner">
			<div class="bounce1"></div>
			<div class="bounce2"></div>
			<div class="bounce3"></div>
		</div>
	</div>
</template>

<script>
export default {
	name: 'SubheadingSpinner',
	props: {
		containerRef: String,
	},
	data() {
		return {
			visible: false,
		}
	},
	methods: {
		setSpinnerShow(visible) {
			this.visible = visible

			if (visible) this.$refs[this.containerRef].style.display = 'block'
			else this.$refs[this.containerRef].style.display = 'none'
		},
	},
}
</script>

<style scoped>
.spinner-container {
	display: none; /* 'block' when visible */
	margin-top: 8px;
	margin-left: 10px;
	margin-right: 10px;
}
</style>
