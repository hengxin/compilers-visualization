<template>
  <a-affix :offset-top="top">
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
    <a-input
      style="width: 15%; background: white; color: black"
      disabled="true"
      v-model:value="currentStateString"
    />
    <a-button
      @click="playbackOnce"
      style="
        width: 30%;
        text-align: left;
        background: #dcffff;
        font-weight: bold;
      "
      :loading="currentOperationButtonDisable"
    >
      当前操作: {{ currentOperationString }}
    </a-button>
    <a-button
      @click="nextOp"
      type="default"
      style="width: 30%; text-align: left"
    >
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
  </a-affix>

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
      <a-card style="border: 1px solid rgba(0, 0, 0); margin-top: 3%">
        <v-chart
          class="chart"
          :option="treeData"
          style="height: 270%; width: 100%"
        />
      </a-card>
      <a-row>
        <a-col :span="20">
          <a-table
            :dataSource="dfaStatesDataSource"
            :pagination="false"
            :bordered="true"
            :locale="{ emptyText: `DFA状态集合` }"
            style="border: 1px solid rgba(0, 0, 0, 0.5)"
          >
            <a-table-column key="num" data-index="num" title="num" :width="1">
            </a-table-column>
            <a-table-column key="dfa" data-index="dfa" v-if="!dfaSimplify">
              <template #title>
                <a @click="dfaSimplify = !dfaSimplify">dfa</a>
              </template>
            </a-table-column>
            <a-table-column key="dfaSimplify" data-index="dfaSimplify" v-else>
              <template #title>
                <a @click="dfaSimplify = !dfaSimplify">dfa</a>
              </template>
            </a-table-column>
          </a-table>
        </a-col>
        <a-col :span="4">
          <a-table
            :dataSource="edgesDataSource"
            :columns="edgesColumns"
            :pagination="false"
            :bordered="true"
            :locale="{ emptyText: `边集` }"
            style="border: 1px solid rgba(0, 0, 0, 0.5)"
          />
        </a-col>
      </a-row>
    </a-col>
  </a-row>
</template>

<script>
import {
  Affix,
  Button,
  Card,
  Row,
  Col,
  Dropdown,
  Menu,
  MenuItem,
  Table,
  TableColumn,
  message,
  Input
} from "ant-design-vue";
import VChart from "vue-echarts";

import {
  isNew,
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
  playbackAllOperations,
  listenDfdAndEdgeStateList,
  getCurrentState
} from "@/data/show_all";
import { input } from "@/router";

export default {
  name: "ShowAll",
  components: {
    VChart,
    AAffix: Affix,
    AButton: Button,
    ACard: Card,
    ARow: Row,
    ACol: Col,
    ADropdown: Dropdown,
    AInput: Input,
    AMenu: Menu,
    AMenuItem: MenuItem,
    ATable: Table,
    ATableColumn: TableColumn
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
      if (isNew()) {
        this.init();
      } else {
        this.playbackOnce().then(() => this.nextOp());
      }
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
      currentStateString: "",
      currentOperationString: "",
      currentOperationButtonDisable: false,
      nextOperationString: "next",
      operationStringList: [],
      dfaStatesDataSource: [],
      dfaSimplify: false,
      edgesDataSource: [],
      edgesColumns: [
        {
          title: "from",
          dataIndex: "from",
          key: "from",
          width: 1
        },
        {
          title: "upon",
          dataIndex: "upon",
          key: "upon"
        },
        {
          title: "to",
          dataIndex: "to",
          key: "to",
          width: 1
        }
      ]
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
      this.currentStateString = "";
      this.currentOperationString = "";
      this.operationStringList.length = 0;
      this.dfaStatesDataSource = [];
      this.edgesDataSource = [];
      listenDfdAndEdgeStateList(this.dfaStatesDataSource, this.edgesDataSource);
    },
    debug() {
      debug(this.num++);
      message.success("debug");
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
        this.currentStateString = getCurrentState();
      });
    },
    nextOp() {
      nextOperation().then(() => {
        this.currentStateString = getCurrentState();
      });
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
<style scoped>
/deep/ .ant-table-tbody > tr > td {
  padding: 0;
  text-align: center;
}
/deep/ .ant-table-thead > tr > th {
  padding: 0;
  text-align: center;
}
</style>
