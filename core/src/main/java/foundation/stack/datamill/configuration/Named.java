package foundation.stack.datamill.configuration;

import java.lang.annotation.*;

/**
 * Annotation to be used to add a name qualifier to constructor parameters.
 *
 * @see Wiring
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Named {
    String value();
}
