package foundation.stack.datamill.configuration.impl;

import foundation.stack.datamill.configuration.BooleanValue;
import foundation.stack.datamill.configuration.IntegerValue;
import foundation.stack.datamill.configuration.StringValue;
import javassist.Modifier;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ConstantsClassSource<T> extends AbstractSource {
    private static String getConstantValue(Field field) {
        if (field != null && Modifier.isStatic(field.getModifiers())) {
            try {
                performSecure(() -> field.setAccessible(true));
                return String.valueOf(field.get(null));
            } catch (IllegalAccessException e) {
                return null;
            }
        }

        return null;
    }

    private static void performSecure(Runnable runnable) {
        if (System.getSecurityManager() != null) {
            AccessController.doPrivileged((PrivilegedAction<?>) () -> {
                runnable.run();
                return null;
            });
        } else {
            runnable.run();
        }
    }

    private final Class<T> constantsClass;
    private Map<String, String> annotatedConstants;

    public ConstantsClassSource(Class<T> constantsClass) {
        this.constantsClass = constantsClass;
    }

    private Map<String, String> buildAnnotatedConstants() {
        HashMap<String, String> constants = new HashMap<>();

        Field[] fields = constantsClass.getDeclaredFields();
        for (Field field : fields) {
            String propertyName = getConstantValue(field);
            if (propertyName != null) {
                StringValue stringValue = field.getAnnotation(StringValue.class);
                if (stringValue != null) {
                    constants.put(propertyName, stringValue.value());
                } else {
                    BooleanValue booleanValue = field.getAnnotation(BooleanValue.class);
                    if (booleanValue != null) {
                        constants.put(propertyName, String.valueOf(booleanValue.value()));
                    } else {
                        IntegerValue integerValue = field.getAnnotation(IntegerValue.class);
                        if (integerValue != null) {
                            constants.put(propertyName, String.valueOf(integerValue.value()));
                        }
                    }
                }
            }
        }

        return constants;
    }

    @Override
    protected Optional<String> getOptional(String name) {
        if (annotatedConstants == null) {
            annotatedConstants = buildAnnotatedConstants();
        }

        return Optional.ofNullable(annotatedConstants.get(name));
    }
}
