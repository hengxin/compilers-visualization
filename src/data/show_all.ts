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
      symbolSize: 13,
      animation: false,
      draggable: true,
      roam: true,
      label: {
        show: true
      },
      edgeSymbol: ["circle", "arrow"],
      edgeSymbolSize: [3, 4],
      edgeLabel: {
        fontSize: 8,
        color: "rgb(0, 0, 0, 0.9)"
      },
      tooltip: {},
      data: [],
      links: [],
      lineStyle: {
        opacity: 0.9,
        width: 2
      },
      autoCurveness: true,
      force: {
        repulsion: 2000,
        edgeLength: [50, 100],
        layoutAnimation: false,
        // friction: 1,
        gravity: 0.4
      }
    }
  ]
};

let succeed_ = false;
let initState_: proto.IInitialState;
let globalOptionList_: any[] = [];

let operatorIndex_ = 0;
let operationList_: proto.IOperationWrapper[] = [];

let currentTokenIndex_ = 0;
let tryPredictTokenIndex_ = 0;
let tokenList_: proto.ITokenMsg[] = [];

function _setOptionList(list: proto.ISubAugmentedTransitionNetwork[]) {
  globalOptionList_ = []; //必须要清空，否则会元素重复
  for (const subATNElement of list) {
    const option = JSON.parse(JSON.stringify(globalConstOption));
    option.title.text = subATNElement.ruleName;
    option.series[0].data = subATNElement.graphNode;
    option.series[0].links = subATNElement.graphEdge;
    option.series[0].links.forEach(
      (link: any) => (link.label.formatter = (v: any) => v.data.name)
    );
    option.tooltip.formatter = (v: any) => {
      const data = v.data;
      if (data.source === undefined || data.target == undefined) {
        return data.name;
      } else {
        return "" + data.source + " - " + data.name + " → " + data.target;
      }
    };
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
  {
    operatorIndex_ = 0;
    operationList_ = resp.operation;
  }
  {
    setTokenList(resp.token);
  }

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

// eslint-disable-next-line @typescript-eslint/no-unused-vars
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
  resetDefaultColors();
}

export function getTokenList(): proto.ITokenMsg[] {
  check();
  return tokenList_;
}

function resetTokenColor(): void {
  tokenList_.forEach(token => (token.background = "rgb(255, 255, 255)"));
  for (let i = 0; i < currentTokenIndex_; i++) {
    tokenList_[i].background = visitedTokenColor;
  }
  if (currentTokenIndex_ < tokenList_.length) {
    tokenList_[currentTokenIndex_].background = currentIndexTokenColor;
  }
  for (let i = currentTokenIndex_; i < tryPredictTokenIndex_; i++) {
    tokenList_[i].background = tryAdaptivePredictColor;
  }
}

function setTokenList(list: proto.ITokenMsg[]): void {
  tokenList_ = list;
  currentTokenIndex_ = 0;
  tryPredictTokenIndex_ = 0;
  resetTokenColor();
}

export function listenTokenList(list: proto.ITokenMsg[]): void {
  setTokenList(list);
}

function nextToken(): void {
  currentTokenIndex_++;
}

function adaptivePredict(): void {
  tryPredictTokenIndex_++;
}

// eslint-disable-next-line @typescript-eslint/no-empty-function,@typescript-eslint/no-unused-vars
export function debug(num: number): void {}

export async function nextOperation(): Promise<void> {
  if (operatorIndex_ >= operationList_.length) {
    message.success("finished").then();
  } else {
    const operation = operationList_[operatorIndex_];
    operatorIndex_++;
    switch (operation.operationType) {
      case proto.OperationType.StartStateClosure:
        if (operation.startStateClosureOperation) {
          handleStartStates(operation.startStateClosureOperation);
          return;
        }
        break;
      case proto.OperationType.AddNewDFAState:
        if (operation.addNewDFAStateOperation) {
          handleAddNewDFAState(operation.addNewDFAStateOperation);
          return;
        }
        break;
      case proto.OperationType.AddNewEdge:
        if (operation.addNewEdgeOperation) {
          await handleAddNewEdge(operation.addNewEdgeOperation);
          return;
        }
        break;
      case proto.OperationType.ReuseState:
        if (operation.reuseStateOperation) {
          await handleReuseState(operation.reuseStateOperation);
          return;
        }
        break;
      case proto.OperationType.SwitchTable:
        if (operation.switchTableOperation) {
          message.success("switchTable");
          handleSwitchTable(operation.switchTableOperation);
          return;
        }
        break;
      case proto.OperationType.ConsumeToken:
        if (operation.consumeTokenOperation) {
          message.success("ConsumeToken");
          handleConsumeToken(operation.consumeTokenOperation);
          return;
        }
        break;
      case proto.OperationType.EditTree:
        if (operation.editTreeOperation) {
          message.success("EditTree");
          handleEditTree(operation.editTreeOperation);
          return;
        }
        break;
      case proto.OperationType.EndAdaptive:
        if (operation.endAdaptiveOperation) {
          message.success("EndAdaptivePredict");
          handleEndAdaptive(operation.endAdaptiveOperation);
          return;
        }
        break;
      default:
        message.error("unknown type " + operation.operationType).then();
    }
    message.error("empty operation").then();
  }
}

const defaultColor = "rgb(0,0,192)";
const startStatesColor = "rgb(128, 128, 255)";
const addNewDFAStateColor = "rgb(0, 255, 255)";
const addNewEdgeFromColor = "rgb(255, 0, 0)";
const addNewEdgeToColor = "rgb(64, 0, 0)";
const reuseFromColor = "rgb(168, 255, 0)";
const reuseToColor = "rgb(255, 168, 0)";

const currentIndexTokenColor = "rgb(0, 128, 255)";
const visitedTokenColor = "rgb(192, 192, 192)";
const tryAdaptivePredictColor = "rgb(0, 255, 255)";

function resetDefaultColors() {
  for (const op of globalOptionList_) {
    for (const data of op.series[0].data) {
      data.itemStyle.color = defaultColor;
    }
  }
}

function handleStartStates(operation: proto.IStartStateClosureOperation): void {
  console.log(operation);
  resetDefaultColors();
  for (const op of globalOptionList_) {
    for (const data of op.series[0].data) {
      if (operation.startingClosure.atnState) {
        for (const n of operation.startingClosure.atnState) {
          if (parseInt(data.id) === n.atnStateNumber) {
            data.itemStyle.color = startStatesColor;
            break;
          }
        } // end-for
      }
    }
  }
}
function handleAddNewDFAState(operation: proto.IAddNewDFAStateOperation): void {
  console.log(operation);
  resetDefaultColors();
  for (const op of globalOptionList_) {
    for (const data of op.series[0].data) {
      if (operation.newDfaState.atnState) {
        for (const n of operation.newDfaState.atnState) {
          if (parseInt(data.id) === n.atnStateNumber) {
            data.itemStyle.color = addNewDFAStateColor;
            break;
          }
        } // end-for
      }
    }
  }
}

async function handleAddNewEdge(
  operation: proto.IAddNewEdgeOperation
): Promise<void> {
  console.log(operation);
  resetDefaultColors();
  adaptivePredict();
  resetTokenColor();
  for (const op of globalOptionList_) {
    for (const data of op.series[0].data) {
      if (operation.newEdge.from.atnState) {
        for (const n of operation.newEdge.from.atnState) {
          if (parseInt(data.id) === n.atnStateNumber) {
            data.itemStyle.color = addNewEdgeFromColor;
            break;
          }
        } // end-for
      }
    }
  }
  await sleep(200);
  for (const op of globalOptionList_) {
    for (const data of op.series[0].data) {
      if (operation.newEdge.to.atnState) {
        for (const n of operation.newEdge.to.atnState) {
          if (parseInt(data.id) === n.atnStateNumber) {
            data.itemStyle.color = addNewEdgeToColor;
            break;
          }
        } // end-for
      }
    }
  }
}

async function handleReuseState(
  operation: proto.IReuseStateOperation
): Promise<void> {
  resetDefaultColors();
  adaptivePredict();
  resetTokenColor();
  console.log(operation);
  for (const op of globalOptionList_) {
    for (const data of op.series[0].data) {
      if (operation.reuse.from.atnState) {
        for (const n of operation.reuse.from.atnState) {
          if (parseInt(data.id) === n.atnStateNumber) {
            data.itemStyle.color = reuseFromColor;
            break;
          }
        } // end-for
      }
    }
  }
  await sleep(200);
  for (const op of globalOptionList_) {
    for (const data of op.series[0].data) {
      if (operation.reuse.to.atnState) {
        for (const n of operation.reuse.to.atnState) {
          if (parseInt(data.id) === n.atnStateNumber) {
            data.itemStyle.color = reuseToColor;
            break;
          }
        } // end-for
      }
    }
  }
}

function handleSwitchTable(operation: proto.ISwitchTableOperation): void {
  console.log(operation);
}

function handleConsumeToken(operation: proto.IConsumeTokenOperation): void {
  console.log(operation);
  nextToken();
  tryPredictTokenIndex_ = currentTokenIndex_;
  resetTokenColor();
}

function handleEditTree(operation: proto.IEditTreeOperation): void {
  console.log(operation);
}

function handleEndAdaptive(operation: proto.IEndAdaptiveOperation): void {
  console.log(operation);
  tryPredictTokenIndex_ = currentTokenIndex_;
  resetTokenColor();
}
async function sleep(ms: number): Promise<void> {
  return new Promise(resolve => setTimeout(resolve, ms));
}
