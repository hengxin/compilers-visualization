import axios from "axios";
import { hmb } from "../gen/proto";
import setMainResponse from "./show_all";
import { message } from "ant-design-vue";
import proto = hmb.protobuf;

async function _parse(data: proto.MainRequest): Promise<proto.MainResponse> {
  const httpService = axios.create({
    method: "post",
    headers: {
      Accept: "application/x-protobuf",
      "Content-Type": "application/x-protobuf"
    },
    responseType: "arraybuffer"
  });
  const uint8data = Uint8Array.from(proto.MainRequest.encode(data).finish());
  return httpService
    .post("http://127.0.0.1:9988/parse/parse", uint8data)
    .then(r => {
      return proto.MainResponse.decode(new Uint8Array(r.data));
    });
}

export async function parse(
  userId: number,
  name: string,
  lexer: string,
  parser: string,
  code: string,
  startRule: string
): Promise<boolean | void> {
  const r = new proto.MainRequest();
  r.userId = userId;
  r.name = name;
  r.lexer = lexer;
  r.parser = parser;
  r.code = code;
  r.startRule = startRule;
  return _parse(r).then(resp => {
    if (!resp.success) {
      message.error(resp.errorMessage);
      console.log(resp.errorMessage);
      return false;
    }
    console.log(resp);
    if (setMainResponse(resp)) {
      return true;
    } else {
      throw "parse error";
    }
  });
}
