package foundation.stack.datamill.configuration;

import rx.functions.Func2;

/**
 * A factory for constructing objects of particular types. Use {@link FactoryChains} to create factories and compose
 * them together into chains. Note that the contract of a factory is to return a fully constructed instance of the
 * requested type (it can use the provided {@link Wiring} to construct the instance, or return null if the factory
 * cannot construct an instance.
 *
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
@FunctionalInterface
public interface Factory<T, R extends T> extends Func2<Wiring, Class<? extends T>, R> {
    static Factory<?, ?> wrap(TypeLessFactory<?> factory) {
        return (w, c) -> factory.call(w);
    }
}
