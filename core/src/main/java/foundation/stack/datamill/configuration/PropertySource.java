package foundation.stack.datamill.configuration;

import foundation.stack.datamill.values.Value;
import rx.functions.Action1;

import java.util.Optional;

/**
 * A source of properties.
 *
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface PropertySource {
    /**
     * Alias a property so that the alias can be used in a {@link #get(String)} call in order to retrieve the original
     * property's value.
     *
     * @param alias    New alias for the original property.
     * @param original Original property to create an alias for.
     */
    PropertySource alias(String alias, String original);

    /**
     * Get the specified property from the source, if it exists.
     *
     * @param name Name of property to retrieve.
     * @return Returns the value of the property as a string. Returns an empty {@link Optional} if the property
     * doesn't exist.
     */
    Optional<String> get(String name);

    /**
     * Get the specified property from the chain.
     *
     * @param name Name of property to retrieve.
     * @return The property value, as a {@link Value}.
     * @throws IllegalArgumentException If the specified property does not exist!
     */
    Value getRequired(String name);

    /**
     * Convenience method to receive the property source within a lambda.
     *
     * @param propertiesConsumer Lambda that receives this property source.
     */
    PropertySource with(Action1<PropertySource> propertiesConsumer);
}
