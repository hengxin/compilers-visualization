import axios from "axios";
import { hmb } from "../gen/proto";
import proto = hmb.protobuf;

export async function getUsrInfo(): Promise<proto.ExampleUserInfoResponse> {
  const s = new proto.ExampleUserInfoRequest();
  s.userId = 10086;
  // getUserInfo.UserInfoRequest.create(s)
  const writer = proto.ExampleUserInfoRequest.encode(s);
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

  const req = Uint8Array.from(uint8Array);
  console.log(req);

  return httpService
    .post("http://127.0.0.1:9988/example/getUserInfo", req, {})
    .then(r => {
      console.log(r.status);
      console.log(r.data);
      const buffer = new Uint8Array(r.data);
      console.log("buffer", buffer);
      return proto.ExampleUserInfoResponse.decode(buffer);
    })
    .catch(reason => {
      console.log(reason);
      throw reason;
    })
    .finally(() => console.log("finished"));
}
