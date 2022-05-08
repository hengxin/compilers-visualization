<template>
  <div>ShowAll</div>
</template>

<script>
import { getInitState } from "@/data/parse";

export default {
  name: "ShowAll",
  mounted() {
    if (!getInitState()) {
      alert("unloaded");
      this.$router.push("input");
    }
  }
};
</script>

<style scoped></style>
