<template>
  <div>ShowAll</div>
</template>

<script>
import { loaded, getInitState } from "@/data/show_all";

export default {
  name: "ShowAll",
  mounted() {
    if (!loaded()) {
      alert("unloaded");
      this.$router.push("input");
    } else {
      console.log(getInitState());
    }
  }
};
</script>

<style scoped></style>
