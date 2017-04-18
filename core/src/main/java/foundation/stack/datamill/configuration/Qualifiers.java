package foundation.stack.datamill.configuration;

import java.lang.annotation.*;

/**
 * Annotation to be used to add qualifiers to constructor parameters, and to classes.
 *
 * @see Wiring
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.TYPE})
public @interface Qualifiers {
    Qualifier[] value();
}
