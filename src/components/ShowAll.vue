<template>
  <a-dropdown :trigger="['click']">
    <a @click.prevent>操作序列 </a>
    <template #overlay>
      <a-menu @click="onMenuClick">
        <a-menu-item v-for="(s, idx) in operationStringList" :key="idx">
          <span>{{ idx }}: {{ s }}</span>
        </a-menu-item>
      </a-menu>
    </template>
  </a-dropdown>
  <a-button @click="debug">debug</a-button>
  <a-button
    @click="playbackOnce"
    style="width: 30%; text-align: left"
    :loading="currentOperationButtonDisable"
  >
    当前操作: {{ currentOperationString }}
  </a-button>
  <a-button @click="nextOp" type="primary" style="width: 30%; text-align: left">
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
          style="height: 270%; width: 100%"
        />
      </a-card>
    </a-col>
  </a-row>
</template>

<script>
import {
  Button,
  Card,
  Row,
  Col,
  Dropdown,
  Menu,
  MenuItem,
  message
} from "ant-design-vue";
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
    ACol: Col,
    ADropdown: Dropdown,
    AMenu: Menu,
    AMenuItem: MenuItem
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
      document.onkeydown = () => {
        let key = window.event.keyCode;
        if (window.event.altKey) {
          // mac平台是option键
          if (key === 39) {
            // 右箭头, 前进一步
            window.event.preventDefault();
            this.nextOp();
          } else if (key === 37) {
            // 左箭头, 回退一步
            window.event.preventDefault();
            this.playbackOnce();
          } else if (key === 13) {
            // 回车, 刷新
            window.event.preventDefault();
            this.playbackOnce().then(() => this.nextOp());
          }
        }
      };
    }
  },
  unmounted() {
    document.onkeydown = () => {};
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
      nextOperationString: "next",
      operationStringList: []
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
        this.operationStringList.push(this.currentOperationString);
      });
      this.currentOperationString = "";
      this.operationStringList.length = 0;
    },
    debug() {
      ++this.num;
      debug(this.num);
      this.init();
    },
    playbackOnce() {
      this.currentOperationButtonDisable = true;
      const index = getOperatorIndex();
      return this.playbackTo(index - 1);
    },
    playbackTo(index) {
      reload();
      this.init();
      return playbackAllOperations(index).then(() => {
        this.currentOperationButtonDisable = false;
      });
    },
    nextOp() {
      nextOperation().then();
    },
    onMenuClick(item) {
      this.playbackTo(item.key);
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
