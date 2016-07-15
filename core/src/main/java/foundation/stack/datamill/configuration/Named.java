package foundation.stack.datamill.configuration;

import java.lang.annotation.*;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Named {
    String value();
}
