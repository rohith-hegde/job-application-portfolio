<template>
  <div class="app">
    <AppHeader fixed>
      <SidebarToggler class="d-lg-none" display="md" mobile/>
      <b-link class="navbar-brand" to="#">
        <img
          class="navbar-brand-full"
          src="/img/brand/logo.svg"
          width="89"
          height="25"
          alt="CoreUI Logo"
        >
        <img
          class="navbar-brand-minimized"
          src="/img/brand/sygnet.svg"
          width="30"
          height="30"
          alt="CoreUI Logo"
        >
      </b-link>
      <SidebarToggler class="d-md-down-none" display="lg"/>
      <b-navbar-nav class="ml-auto">
        <b-nav-item class="d-md-down-none">
          <DefaultHeaderDropdownNotif/>
        </b-nav-item>

        <b-nav-item class="d-md-down-none" style="padding-right: 20px;">
          <DefaultHeaderDropdownMssgs/>
        </b-nav-item>
        <!-- <b-nav-item class="d-md-down-none">
          <DefaultHeaderDropdown/>
        </b-nav-item>-->
        <RRFheaderUserWelcome/>

        <!-- <DefaultHeaderDropdownAccnt/> -->
        <RRFheaderDropdownAccnt/>
      </b-navbar-nav>
      <!-- <AsideToggler class="d-none d-lg-block" />
      <AsideToggler class="d-lg-none" mobile />-->
    </AppHeader>
    <div class="app-body">
      <div class="sidebar sidebar-fixed">
        <SidebarHeader/>
        <SidebarForm/>
        <SidebarNav :navItems="nav"></SidebarNav>
        <SidebarFooter/>
        <!--<SidebarMinimizer/>-->
      </div>
      <main class="main">
        <div class="container-fluid">
          <router-view></router-view>
        </div>
      </main>
      <!-- <AppAside fixed>

        <DefaultAside/>
      </AppAside>-->
    </div>
    <TheFooter>
      <!--footer-->
      <div>
        <a v-bind:href="ipCopyright.productURL">{{ ipCopyright.productName }}</a>
        <span class="ml-1">&copy; {{ ipCopyright.annum }} {{ ipCopyright.companyName }}</span>
        <span v-bind:class="ipCopyrightAddTextCssClass">{{ ipCopyright.additionalText }}</span>
      </div>

      <div class="ml-auto" style="display: none;">
        <span class="mr-1">Powered by</span>
        <a href="https://coreui.io">CoreUI for Vue</a>
      </div>
    </TheFooter>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import nav from "../_nav";
const {
	Header: AppHeader,
	SidebarToggler,
	Sidebar: AppSidebar,
	SidebarFooter,
	SidebarForm,
	SidebarHeader,
	SidebarMinimizer,
	SidebarNav,
	Aside: AppAside,
	AsideToggler,
	Footer: TheFooter,
	Breadcrumb
} = require("@coreui/vue");

//import { Header as AppHeader, SidebarToggler, Sidebar as AppSidebar, SidebarFooter, SidebarForm, SidebarHeader, SidebarMinimizer, SidebarNav, Footer as TheFooter, Breadcrumb } from '@coreui/vue'

import DefaultAside from "./DefaultAside.vue";
import DefaultHeaderDropdown from "./DefaultHeaderDropdown.vue";
import DefaultHeaderDropdownNotif from "./DefaultHeaderDropdownNotif.vue";
import DefaultHeaderDropdownAccnt from "./DefaultHeaderDropdownAccnt.vue";
import RRFheaderDropdownAccnt from "./RRFheaderDropdownAccnt.vue";
import DefaultHeaderDropdownMssgs from "./DefaultHeaderDropdownMssgs.vue";
import RRFheaderUserWelcome from "./RRFheaderUserWelcome.vue";
import { RouteRecord } from "vue-router";

const extraData = {
	ipCopyright: {
		additionalTextLook: "ml-1 text-muted"
	}
};

export default Vue.extend({
	name: "RRFcontainer",
	components: {
		AppHeader,
		// AppSidebar,
		TheFooter,
		// Breadcrumb,
		DefaultHeaderDropdownMssgs,
		DefaultHeaderDropdownNotif,
		RRFheaderDropdownAccnt,
		//DefaultHeaderDropdownAccnt,
		RRFheaderUserWelcome,
		SidebarForm,
		SidebarFooter,
		SidebarToggler,
		SidebarHeader,
		SidebarNav
		// SidebarMinimizer,
	},
	data() {
		return {
			nav: nav.items,
			ipCopyright: {
				productName: "Resetter RF",
				productURL: "https://rrf.coinfarm.us/",
				companyName: "Coinfarm Ventures L.L.C.",
				//annum: 2019,
				annum: new Date().getFullYear(),
				additionalText: "All rights reserved.",
				additionalTextLook: "ml-1 text-muted"
			}
		};
	},
	computed: {
		name(): string {
			return this.$route.name || "";
		},
		list() {
			const result: RouteRecord[] = this.$route.matched.filter(
				(route: RouteRecord) => route.name || route.meta.label
			);
			return result;
		},
		ipCopyrightAddTextCssClass() {
			return extraData.ipCopyright.additionalTextLook;
		}
	}
});
</script>

<style scoped>
.container-fluid {
	padding-top: 15px;
}
</style>

