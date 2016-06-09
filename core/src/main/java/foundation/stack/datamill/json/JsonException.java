package foundation.stack.datamill.json;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class JsonException extends RuntimeException {
    public JsonException() {
    }

    public JsonException(String message) {
        super(message);
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonException(Throwable cause) {
        super(cause);
    }
}
