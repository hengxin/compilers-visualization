<template>
  <a-button @click="debug">debug</a-button>
  <a-button
    @click="playbackCurrent"
    style="width: 30%; text-align: left"
    :disable="currentOperationButtonDisable"
  >
    当前操作: {{ currentOperationString }}
  </a-button>
  <a-button @click="next" type="primary" style="width: 30%; text-align: left">
    {{ nextOperationButtonString }}
  </a-button>
  <br />
  <div v-for="(token, idx) in tokenList" :key="idx" style="display: inline">
    <div
      style="border: 1px solid #c6c6c6; display: inline-block"
      :style="'background: ' + token.background"
    >
      <div>
        {{ token.tokenRule }}
        <br />
        {{ token.tokenText }}
      </div>
    </div>
  </div>
  <a-row>
    <a-col :span="9">
      <a-card v-for="(op, idx) in optionList" :key="idx" ref="vAtnList">
        <v-chart
          class="chart"
          :option="op"
          style="height: 90%; width: 100%"
        ></v-chart>
      </a-card>
    </a-col>
    <a-col :span="15">
      <a-card>
        <v-chart
          class="chart"
          :option="treeData"
          style="height: 400%; width: 100%"
        />
      </a-card>
    </a-col>
  </a-row>
</template>

<script>
import { Button, Card, Row, Col, message } from "ant-design-vue";
import VChart from "vue-echarts";

import {
  loaded,
  getInitState,
  getOptionsList,
  debug,
  nextOperation,
  listenOptionList,
  getTokenList,
  listenTokenList,
  getTreeOption,
  listenTreeOption,
  setNextButtonStringOnChanged,
  reload,
  getOperatorIndex,
  playbackAllOperations
} from "@/data/show_all";
import { input } from "@/router";

export default {
  name: "ShowAll",
  components: {
    VChart,
    AButton: Button,
    ACard: Card,
    ARow: Row,
    ACol: Col
  },
  beforeCreate() {
    if (!loaded()) {
      message.error("unloaded");
      input();
    } else {
      console.log(getInitState());
    }
  },
  mounted() {
    if (loaded()) {
      this.init();
    }
  },
  data() {
    return {
      optionList: [],
      num: 0,
      tokenList: [
        {
          tokenType: 0,
          tokenText: "",
          tokenRule: "",
          channel: 0,
          background: "rgb(255,255,255)"
        }
      ],
      treeData: getTreeOption(),
      currentOperationString: "",
      currentOperationButtonDisable: false,
      nextOperationString: "next"
    };
  },
  methods: {
    init() {
      this.optionList = getOptionsList();
      listenOptionList(this.optionList);
      this.tokenList = getTokenList();
      listenTokenList(this.tokenList);
      this.treeData = getTreeOption();
      listenTreeOption(this.treeData);
      this.nextOperationString = "next";
      setNextButtonStringOnChanged(next => {
        this.currentOperationString = this.nextOperationString;
        this.nextOperationString = next;
      });
      this.currentOperationString = "";
    },
    debug() {
      ++this.num;
      debug(this.num);
      this.init();
    },
    playbackCurrent() {
      this.currentOperationButtonDisable = true;
      const index = getOperatorIndex();
      reload();
      this.init();
      playbackAllOperations(index - 1).then(() => {
        this.currentOperationButtonDisable = false;
      });
    },
    next() {
      nextOperation().then();
    }
  },
  computed: {
    nextOperationButtonString() {
      if (this.nextOperationString) {
        return "next: " + this.nextOperationString;
      } else {
        return "Finished";
      }
    }
  }
};
</script>
<style scoped></style>
