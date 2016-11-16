package foundation.stack.datamill.configuration.impl;

import foundation.stack.datamill.configuration.Defaults;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class DefaultsSource extends AbstractSource implements Defaults {
    private final Map<String, String> defaults = new HashMap<>();

    @Override
    public Optional<String> getOptional(String name) {
        return Optional.ofNullable(defaults.get(name));
    }

    @Override
    public Defaults put(String name, String value) {
        defaults.put(name, value);
        return this;
    }
}
