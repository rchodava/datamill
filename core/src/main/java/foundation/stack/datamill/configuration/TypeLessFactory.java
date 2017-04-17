package foundation.stack.datamill.configuration;

import rx.functions.Func1;

/**
 * An object factory like {@link Factory} but one that doesn't need the {@link Class} parameter to be passed into it in
 * order to construct objects.
 *
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
@FunctionalInterface
public interface TypeLessFactory<R> extends Func1<Wiring, R> {
}
