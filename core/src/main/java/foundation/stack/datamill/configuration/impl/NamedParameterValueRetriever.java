package foundation.stack.datamill.configuration.impl;

import foundation.stack.datamill.configuration.Named;
import foundation.stack.datamill.configuration.Wiring;
import foundation.stack.datamill.configuration.WiringException;

import java.lang.reflect.Parameter;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class NamedParameterValueRetriever {
    private static Object getValueForNamedParameter(Wiring wiring, Parameter parameter, Named[] names) {
        for (Named name : names) {
            Object value = getValueForNamedParameter(wiring, parameter, name);
            if (value != null) {
                return value;
            }
        }

        throwUnsatisfiedNamedParameterException(names);
        return null;
    }

    private static Object getValueForNamedParameter(Wiring wiring, Parameter parameter, Named name) {
        return SimpleValueConverter.convert(wiring.getNamed(name.value()).orElse(null), parameter.getType());
    }

    public static Object retrieveValueIfNamedParameter(Wiring wiring, Parameter parameter) {
        Named[] names = parameter.getAnnotationsByType(Named.class);
        if (names != null && names.length > 0) {
            Object namedValue = getValueForNamedParameter(wiring, parameter, names);
            if (namedValue != null) {
                return namedValue;
            }
        }

        return null;
    }

    private static void throwUnsatisfiedNamedParameterException(Named[] names) {
        StringBuilder message = new StringBuilder("Failed to satisfy named parameter [");

        for (int i = 0; i < names.length; i++) {
            if (names[i].value() != null) {
                message.append(names[i].value());
                if (i < names.length - 1) {
                    message.append(", ");
                }
            }
        }

        message.append(']');

        throw new WiringException(message.toString());
    }
}
