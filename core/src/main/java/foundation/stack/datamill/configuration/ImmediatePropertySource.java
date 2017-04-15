package foundation.stack.datamill.configuration;

/**
 * Used in defining an immediate immutable set of properties.
 *
 * @see PropertySourceChain
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface ImmediatePropertySource {
    ImmediatePropertySource put(String name, String value);
}
