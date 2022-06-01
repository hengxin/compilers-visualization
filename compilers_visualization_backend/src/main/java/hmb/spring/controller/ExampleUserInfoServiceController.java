package hmb.spring.controller;


import hmb.protobuf.User.ExampleUserInfoRequest;
import hmb.protobuf.User.ExampleUserInfoResponse;
import hmb.protobuf.User.ExampleUserInfoResponse.Builder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;

@Controller
@RequestMapping("/example")
public class ExampleUserInfoServiceController {

    @PostMapping(value = "/getUserInfo", produces = "application/x-protobuf")
    public @ResponseBody
    ExampleUserInfoResponse getUserInfo(@RequestBody ExampleUserInfoRequest request) {
        System.out.println(request.toString());
        System.out.println(Arrays.toString(request.toByteArray()));
        long userId = request.getUserId();
        Builder builder = ExampleUserInfoResponse.newBuilder();
        builder.setMobile("" + userId + "_123456");
        builder.setUserType((int) (userId % 1000007));
        return builder.build();
    }
}