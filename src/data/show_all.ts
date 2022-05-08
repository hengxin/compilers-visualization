import { hmb } from "../gen/proto";
import proto = hmb.protobuf;

let succeed = false;
let myInitState: proto.IInitialState;

export default function setMainResponse(resp: proto.MainResponse): boolean {
  if (!resp.initialState) {
    console.log("resp.initialState = " + resp.initialState);
    return false;
  }
  myInitState = resp.initialState;
  succeed = true;
  return true;
}

export function loaded(): boolean {
  return succeed;
}

export function getInitState(): proto.IInitialState {
  return myInitState;
}
