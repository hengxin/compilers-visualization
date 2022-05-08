import { hmb } from "../gen/proto";

const proto = hmb.protobuf;

let succeed = false;
let myInitState: hmb.protobuf.IInitialState;

export default function setMainResponse(
  resp: hmb.protobuf.MainResponse
): boolean {
  if (!resp.initialState) {
    alert("resp.initialState = " + resp.initialState);
    return false;
  }
  myInitState = resp.initialState;
  succeed = true;
  return true;
}

export function loaded(): boolean {
  return succeed;
}

export function getInitState(): hmb.protobuf.IInitialState {
  return myInitState;
}
