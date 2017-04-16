package foundation.stack.datamill.configuration;

import java.lang.annotation.*;

/**
 * Annotation to be used to add a qualifier to constructor parameters, and to classes. Adding this annotation to a
 * constructor means that a {@link QualifyingFactory} must have been
 *
 * @see Wiring
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Repeatable(Qualifiers.class)
public @interface Qualifier {
    String value();
}
