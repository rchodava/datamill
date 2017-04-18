package foundation.stack.datamill.configuration.impl;

import foundation.stack.datamill.configuration.ImmediatePropertySource;
import foundation.stack.datamill.values.Value;

import java.text.MessageFormat;
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

    @Override
    public ImmediatePropertySource put(String name, String format, Object... arguments) {
        Object[] casted = new Object[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i] instanceof Value) {
                casted[i] = ((Value) arguments[i]).asString();
            } else {
                casted[i] = arguments[i];
            }
        }

        return put(name, MessageFormat.format(format, casted));
    }

}
