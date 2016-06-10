package foundation.stack.datamill.http;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class HttpException extends RuntimeException {
    public HttpException() {
    }

    public HttpException(String message) {
        super(message);
    }

    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpException(Throwable cause) {
        super(cause);
    }
}
