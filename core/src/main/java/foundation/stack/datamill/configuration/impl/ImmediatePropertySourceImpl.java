package foundation.stack.datamill.configuration.impl;

import foundation.stack.datamill.configuration.ImmediatePropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ImmediatePropertySourceImpl extends AbstractSource implements ImmediatePropertySource {
    private final Map<String, String> immediates = new HashMap<>();

    @Override
    public Optional<String> getOptional(String name) {
        return Optional.ofNullable(immediates.get(name));
    }

    @Override
    public ImmediatePropertySource put(String name, String value) {
        immediates.put(name, value);
        return this;
    }
}
