<template>
  <a-button @click="back">back</a-button>
  <a-button @click="debug">debug</a-button>
  <a-col :span="11">
    <a-card v-for="(op, idx) in optionList" :key="idx" ref="vAtnList">
      <v-chart
        class="chart"
        :option="op"
        style="height: 300%; width: 100%"
      ></v-chart>
    </a-card>
  </a-col>
</template>

<script>
import { Button, Card, Row, Col, message } from "ant-design-vue";
import VChart from "vue-echarts";

import {
  loaded,
  getInitState,
  getOptionsList,
  debug,
  listenOptionList
} from "@/data/show_all";

export default {
  name: "ShowAll",
  components: {
    VChart,
    AButton: Button,
    ACard: Card,
    // eslint-disable-next-line vue/no-unused-components
    ARow: Row,
    ACol: Col
  },
  beforeCreate() {
    if (!loaded()) {
      message.error("unloaded");
      this.$router.push("input");
    } else {
      console.log(getInitState());
    }
  },
  mounted() {
    this.optionList = getOptionsList();
    listenOptionList(this.optionList);
  },
  data() {
    return {
      optionList: [],
      num: 0
    };
  },
  methods: {
    back() {
      this.$router.push("input");
    },
    debug() {
      ++this.num;
      debug(this.num);
    }
  }
};
</script>
<style scoped>
.chart {
  width: 100%;
  height: 100%;
}
</style>
