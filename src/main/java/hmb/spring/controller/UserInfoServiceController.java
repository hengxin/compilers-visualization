package hmb.spring.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import hmb.protobuf.User.UserInfoRequest;
import hmb.protobuf.User.UserInfoResponse;
import hmb.protobuf.User.UserInfoResponse.Builder;

@Controller
public class UserInfoServiceController {

    @RequestMapping(value = "/getUserInfo", method = RequestMethod.POST, produces = "application/x-protobuf")
    public @ResponseBody
    UserInfoResponse getUserInfo(@RequestBody UserInfoRequest request) {
        System.out.println(request.toString());
        long userId = request.getUserId();
        Builder builder = UserInfoResponse.newBuilder();
        builder.setMobile("" + userId + "_123456");
        builder.setUserType((int) (userId % 1000007));
        return builder.build();
    }
}