package foundation.stack.datamill.security;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class SecurityException extends RuntimeException {
    public SecurityException() {
    }

    public SecurityException(String message) {
        super(message);
    }

    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public SecurityException(Throwable cause) {
        super(cause);
    }
}
