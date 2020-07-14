<template>
  <AppHeaderDropdown2 right no-caret>
    <template slot="header">
      <img src="/img/avatars/6.jpg" class="img-avatar" alt="admin@bootstrapmaster.com">
    </template>
    <!-- </template>\ -->
    <template slot="dropdown">
      <b-dropdown-header tag="div" class="text-center">
        <strong>Account</strong>
      </b-dropdown-header>
      <b-dropdown-item>
        <i class="fa fa-bell-o"/> Updates
        <b-badge variant="info">{{ itemsCount }}</b-badge>
      </b-dropdown-item>
      <b-dropdown-item>
        <i class="fa fa-envelope-o"/> Messages
        <b-badge variant="success">{{ itemsCount }}</b-badge>
      </b-dropdown-item>

      <b-dropdown-header tag="div" class="text-center">
        <strong>Settings</strong>
      </b-dropdown-header>

      <b-dropdown-item>
        <i class="fa fa-wrench"/> Settings
      </b-dropdown-item>
      <b-dropdown-item>
        <i class="fa fa-usd"/> Payments
        <b-badge variant="secondary">{{ itemsCount }}</b-badge>
      </b-dropdown-item>

      <b-dropdown-divider/>

      <b-dropdown-item @click="onLogout">
        <i class="fa fa-lock"/>Logout
      </b-dropdown-item>
    </template>
  </AppHeaderDropdown2>
</template>

<script>
import { HeaderDropdown as AppHeaderDropdown2 } from "@coreui/vue";
import authService from "../auth/authService";

export default {
  name: "RRFheaderDropdownAccnt",
  components: {
    AppHeaderDropdown2
  },
  data: () => ({
    itemsCount: 42
  }),
  methods: {
    onLogout(e) {
      authService.logOut();
    }
  }
};
</script>
