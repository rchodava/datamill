package foundation.stack.datamill.configuration;

/**
 * Used in defining an immediate immutable set of properties.
 *
 * @see PropertySources
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface ImmediatePropertySource {
    ImmediatePropertySource put(String name, String value);

    /**
     * Add a formatted value to the immediate set of properties. Uses the
     * {@link java.text.MessageFormat#format(String, Object...)} method to format the value.
     */
    ImmediatePropertySource put(String name, String format, Object... arguments);
}
