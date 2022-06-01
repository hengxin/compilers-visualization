<template>
  <a-button @click="nextTree" type="primary">next</a-button>
  <EchartsTree ref="echartsTree" />
</template>

<script>
import { Button } from "ant-design-vue";
import EchartsTree from "@/components/EchartsTree";

export default {
  name: "CompileShow",
  components: {
    EchartsTree,
    AButton: Button
  },
  methods: {
    nextTree() {
      this.$refs.echartsTree.updateData();
    }
  }
};
</script>

<style scoped></style>
