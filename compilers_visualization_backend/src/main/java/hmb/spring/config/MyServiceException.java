package hmb.spring.config;

public class MyServiceException extends RuntimeException {
    public MyServiceException() {
        super();
    }
    public MyServiceException(String msg) {
        super(msg);
    }
    public MyServiceException(Throwable cause) {
        super(cause);
    }
}
