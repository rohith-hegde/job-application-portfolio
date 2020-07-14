<template>
  <div id="dynamicdiv">
    <b-nav-item class="d-md-down-none h6">
      Welcome,
      <strong>{{ usernickname }}</strong>
    </b-nav-item>
  </div>
</template>

<script>
import axios from "axios"

export default {
  name: "RRFheaderUserWelcome",
  components: {},
  async mounted() {
    try {
      const response = await axios.get("/api/user")
      console.log("axios.get() response: ")
      console.log(response)
      //this.setUsername(this, response.data.email);
      // TODO: refactor this code to submit one action instead
      this.$store.commit("setUsername", response.data.name)
      this.$store.commit("setUserID", response.data.id)
      this.$store.commit("setEmail", response.data.email)
      this.$store.commit("setTimestampCreated", response.data.created_at)  
    } catch (error) {
      this.$store.commit("setUsername", `Error: ${error}`)
      this.$store.commit("setUserID", `Error: ${error}`)
      this.$store.commit("setEmail", `Error: ${error}`)
      this.$store.commit("setTimestampCreated", `Error: ${error}`)
    }
  },
  methods: {
    setUsername(vueC, newN) {
      console.log("'vueC' value: ")
      console.log(vueC)
      console.log(`New username to set: ${newN}`)
      vueC.username = newN
    },
  },
  computed: {
    usernickname() {
      return this.$store.getters.getEmail
    },
  },
}
</script>
