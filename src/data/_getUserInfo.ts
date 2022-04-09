import axios from "axios";
import { hmb } from "../gen/proto";

import $protobuf from "protobufjs/light";

const proto = hmb.protobuf;

export async function getUsrInfo(): Promise<hmb.protobuf.UserInfoResponse> {
  const s = new proto.UserInfoRequest();
  s.userId = 10086;
  // getUserInfo.UserInfoRequest.create(s)
  const writer = proto.UserInfoRequest.encode(s);
  console.log(s);
  const uint8Array = writer.finish();
  console.log(uint8Array);
  //
  // const s2 = getUserInfo.UserInfoRequest.decode(uint8Array, uint8Array.length);
  // console.log(s2)

  const httpService = axios.create({
    method: "post",
    headers: {
      // 'X-Requested-With': 'XMLHttpRequest',
      Accept: "application/x-protobuf",
      "Content-Type": "application/x-protobuf"
    },
    responseType: "arraybuffer"
  });

  const a = $protobuf.roots["default"];
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  const pb = a.hmb.protobuf;
  console.log("bbbb", pb);
  const arrayBuffer = new ArrayBuffer(uint8Array.length);
  const req = new Uint8Array(arrayBuffer);
  for (let i = 0; i < uint8Array.length; i++) {
    req[i] = uint8Array[i];
  }
  console.log(arrayBuffer);
  console.log(req);

  return httpService
    .post("http://127.0.0.1:9988/getUserInfo", req, {})
    .then(r => {
      console.log(r.status);
      console.log(r.data);
      const buffer = new Uint8Array(r.data);
      console.log("buffer", buffer);
      return proto.UserInfoResponse.decode(buffer);
    })
    .catch(reason => {
      console.log(reason);
      throw reason;
    })
    .finally(() => console.log("finished"));
}
