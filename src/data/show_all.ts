/* eslint-disable @typescript-eslint/no-explicit-any */

import { hmb } from "../gen/proto";
import { message } from "ant-design-vue";
import { input } from "../router";
import proto = hmb.protobuf;

const globalConstOption = {
  title: {
    text: "",
    textStyle: {
      color: "rgb(0,0,255)",
      fontSize: 15
    }
  },
  tooltip: {},
  animationDurationUpdate: 1,
  animationEasingUpdate: "quinticInOut",
  series: [
    {
      type: "graph",
      layout: "force",
      symbolSize: 25,
      animation: false,
      draggable: true,
      roam: true,
      label: {
        show: true
      },
      edgeSymbol: ["circle", "arrow"],
      edgeSymbolSize: [4, 10],
      edgeLabel: {
        fontSize: 12
      },
      data: [],
      links: [],
      lineStyle: {
        opacity: 0.9,
        width: 2
      },
      autoCurveness: true,
      force: {
        repulsion: 4000,
        edgeLength: [30, 50],
        layoutAnimation: true,
        // friction: 1,
        gravity: 0.2
      }
    }
  ]
};

let succeed_ = false;
let initState_: proto.IInitialState;
let globalOptionList_: any[] = [];

function _setOptionList(list: proto.ISubAugmentedTransitionNetwork[]) {
  globalOptionList_ = []; //必须要清空，否则会元素重复
  for (const subATNElement of list) {
    const option = JSON.parse(JSON.stringify(globalConstOption));
    option.title.text = subATNElement.ruleName;
    option.series[0].data = subATNElement.graphNode;
    option.series[0].links = subATNElement.graphEdge;
    globalOptionList_.push(option);
  }
}

export default function setMainResponse(resp: proto.MainResponse): boolean {
  if (succeed_) {
    console.log("re init");
  }
  if (!resp.initialState) {
    console.log("resp.initialState = " + resp.initialState);
    return false;
  }
  initState_ = resp.initialState;
  if (!resp.initialState.parserATN.subATN) {
    console.log("parserATN = " + resp.initialState.parserATN.subATN);
    return false;
  }
  _setOptionList(resp.initialState.parserATN.subATN);
  succeed_ = true;
  return true;
}

export function loaded(): boolean {
  return succeed_;
}

function check(): void {
  if (!succeed_) {
    message.error("unloaded!").then();
    input().then(() => {
      throw "";
    });
  }
}

export function getInitState(): proto.IInitialState {
  return initState_;
}

export function getOptionsList(): any[] {
  check();
  return globalOptionList_;
}

const colorList = [
  "rgb(0,0,255)",
  "rgb(0,255,0)",
  "rgb(255,0,0)",
  "rgb(0,255,255)",
  "rgb(255,255,0)",
  "rgb(255,0,255)"
];

export function listenOptionList(optionList: any[]): void {
  globalOptionList_ = optionList;
}

export function debug(num: number): void {
  for (const op of globalOptionList_) {
    op.series[0].data[0].itemStyle.color = colorList[num % colorList.length];
  }
}
