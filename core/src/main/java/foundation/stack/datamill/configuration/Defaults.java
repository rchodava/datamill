package foundation.stack.datamill.configuration;

/**
 * Used in defining defaults for properties.
 *
 * @see PropertySourceChain
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Defaults {
    Defaults put(String name, String value);
}
