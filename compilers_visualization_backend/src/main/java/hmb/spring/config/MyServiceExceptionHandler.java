package hmb.spring.config;

import hmb.protobuf.Response;
import hmb.spring.serviceimpl.ParseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.stream.Collectors;

@ControllerAdvice
public class MyServiceExceptionHandler {

    private final static Logger logger = LoggerFactory.getLogger(MyServiceException.class);

    @ExceptionHandler(value = MyServiceException.class)
    @ResponseBody
    public Response.MainResponse serviceExceptionHandler(MyServiceException e) {
        logger.warn(e.getStackTrace()[0].toString() + "\n" + e.getMessage());
        return Response.MainResponse.newBuilder().setSuccess(false).setErrorMessage(e.getMessage()).build();
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Response.MainResponse exceptionHandler(Exception e) {
        logger.error(e.getMessage(), e);
        return Response.MainResponse.newBuilder()
                .setSuccess(false)
                .setErrorMessage(e.getClass().getName() + ";  msg = " + e.getMessage() +
                        Arrays.stream(e.getStackTrace()).map(s -> "\n  " + s.toString()).collect(Collectors.joining()))
                .build();
    }
}
