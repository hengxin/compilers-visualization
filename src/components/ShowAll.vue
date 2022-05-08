<template>
  <div>ShowAll</div>
</template>

<script>
import { message } from "ant-design-vue";
import { loaded, getInitState } from "@/data/show_all";

export default {
  name: "ShowAll",
  mounted() {
    if (!loaded()) {
      message.error("unloaded");
      this.$router.push("input");
    } else {
      console.log(getInitState());
    }
  }
};
</script>

<style scoped></style>
