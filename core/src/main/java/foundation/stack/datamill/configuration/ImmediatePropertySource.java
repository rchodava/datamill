package foundation.stack.datamill.configuration;

/**
 * Used in defining an immediate immutable set of properties.
 *
 * @see PropertySources
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface ImmediatePropertySource {
    ImmediatePropertySource put(String name, String value);
    ImmediatePropertySource put(String name, String format, Object... arguments);
}
