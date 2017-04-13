package foundation.stack.datamill.configuration;

import java.lang.annotation.*;

/**
 * Annotation to be used together with a {@link foundation.stack.datamill.configuration.impl.ConstantsClassSource} to
 * specify String values. For example, a constant declared in a constants class as:
 * <code>
 * \@StringValue("value") public static final String PROPERTY_NAME = "configuration/propertyName";
 * </code>
 * This defines a property called "configuration/propertyName" that has the value "value".
 *
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface StringValue {
    String value();
}
