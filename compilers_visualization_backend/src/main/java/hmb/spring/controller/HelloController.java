package hmb.spring.controller;

import com.google.protobuf.InvalidProtocolBufferException;
import hmb.protobuf.Response;
import hmb.spring.serviceimpl.HelloService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

@RequestMapping("/hello")
@RestController()
public class HelloController {

    private static final byte[] buffer;

    static {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/parser_result_buffer"))) {
            String line = reader.readLine().replaceFirst("\\[", "").replace("]", "");
            Byte[] res = Arrays.stream(line.split(","))
                    .map(String::trim)
                    .map(Byte::parseByte)
                    .toArray(Byte[]::new);
            buffer = new byte[res.length];
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = res[i];
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    final HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @RequestMapping("/login")
    public String login() {
        return helloService.login();
    }

    @GetMapping("/parser")
    public @ResponseBody
    Response.ParserResult parser() throws InvalidProtocolBufferException {
        System.out.println("getParser: size = " + buffer.length);
        return Response.ParserResult.parseFrom(buffer);
    }
}
