package foundation.stack.datamill.reflection;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ReflectionException extends RuntimeException {
    public ReflectionException() {
    }

    public ReflectionException(String message) {
        super(message);
    }

    public ReflectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflectionException(Throwable cause) {
        super(cause);
    }
}
