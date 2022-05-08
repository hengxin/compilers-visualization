package hmb.spring.controller;

import hmb.protobuf.Response.MainResponse;
import hmb.protobuf.Request.MainRequest;
import hmb.spring.serviceimpl.ParseService;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/parse")
@RestController()
public class ParseController {

    private final ParseService parseService;

    public ParseController(ParseService parseService) {
        this.parseService = parseService;
    }

    @PostMapping(value = "/parse", produces = "application/x-protobuf")
    public @ResponseBody
    MainResponse parse(@RequestBody MainRequest mainRequest) throws Exception {
        return parseService.parse(mainRequest);
    }

}
