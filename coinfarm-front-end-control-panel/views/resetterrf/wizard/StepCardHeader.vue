<template>
	<section slot="header" class="flex-container">
		<b-badge class="stepNameBadge" :variant="accent">
			<span style="vertical-align: text-bottom;">{{ badgeText }}</span>
		</b-badge>
		<h4 class="stepCardHeading">{{ heading }}</h4>
	</section>
</template>

<script>
export default {
	name: 'StepCardHeader',
	components: {},
	data() {
		return {}
	},
	props: {
		accent: String,
		heading: String,
		badgeText: String,
	},
}
</script>

<style scoped>
.stepNameBadge {
	font-size: 110%;
}
.flex-container {
	display: flex;
	height: 30px;
	vertical-align: text-bottom;
}

.stepCardHeading {
	padding-left: 10px;
	vertical-align: text-bottom;
}
</style>
