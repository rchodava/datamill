package foundation.stack.datamill.configuration.impl;

import foundation.stack.datamill.configuration.PropertySource;
import foundation.stack.datamill.values.StringValue;
import foundation.stack.datamill.values.Value;
import rx.functions.Action1;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public abstract class AbstractSource implements PropertySource {
    private final Map<String, String> aliases = new HashMap<>();

    @Override
    public PropertySource alias(String alias, String original) {
        aliases.put(alias, original);
        return this;
    }

    protected abstract Optional<String> getOptional(String name);

    @Override
    public final Optional<String> get(String name) {
        Optional<String> value = getOptional(name);
        if (!value.isPresent()) {
            String original = aliases.get(name);
            if (original != null) {
                value = getOptional(original);
            }
        }

        return value;
    }

    @Override
    public Value getRequired(String name) {
        Optional<String> value = get(name);
        if (value.isPresent()) {
            return new StringValue(value.get());
        }

        throw new IllegalArgumentException("Required property " + name + " could not be found in chain!");
    }

    @Override
    public PropertySource with(Action1<PropertySource> propertiesConsumer) {
        propertiesConsumer.call(this);
        return this;
    }
}
