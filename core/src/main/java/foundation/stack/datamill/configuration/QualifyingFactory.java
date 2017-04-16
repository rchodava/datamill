package foundation.stack.datamill.configuration;

import rx.functions.Func3;

import java.util.Collection;

/**
 * An object factory like {@link Factory} but one that requires qualifiers (see {@link Qualifier}) to be present before
 * it constructs objects. The qualifying factory is passed a set of qualifiers (as a {@link Collection} of Strings to
 * allow the factory to test which qualifiers are present.
 *
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
@FunctionalInterface
public interface QualifyingFactory<T, R extends T> extends Func3<Wiring, Class<? extends T>, Collection<String>, R> {
    static QualifyingFactory<?, ?> wrap(Factory<?, ?> factory) {
        return (w, c, q) -> factory.call(w, (Class) c);
    }
}
