package foundation.stack.datamill.configuration;

import java.lang.annotation.*;

/**
 * Annotation to be used to request a named property to be injected into a constructor parameter.
 *
 * @see Wiring
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Named {
    String value();
}
