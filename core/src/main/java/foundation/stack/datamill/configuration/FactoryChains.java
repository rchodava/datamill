package foundation.stack.datamill.configuration;

import foundation.stack.datamill.configuration.impl.ConcreteClassFactory;
import foundation.stack.datamill.configuration.impl.FactoryChainImpl;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class FactoryChains {
    /**
     * @see FactoryChain#thenForAnyConcreteClass()
     */
    public static FactoryChain forAnyConcreteClass() {
        return forAny(ConcreteClassFactory.instance());
    }

    /**
     * @see FactoryChain#thenForAny(Factory)
     */
    public static <T, R extends T> FactoryChain forAny(Factory<T, R> factory) {
        return new FactoryChainImpl(FactoryChainImpl.tautology(), factory);
    }

    /**
     * @see FactoryChain#thenForSuperOf(Class, Factory)
     */
    public static <T, R extends T> FactoryChain forSuperOf(Class<T> type, Factory<T, R> factory) {
        return new FactoryChainImpl(FactoryChainImpl.isSuperOf(type), factory);
    }

    /**
     * @see FactoryChain#thenForType(Class, Factory)
     */
    public static <T, R extends T> FactoryChain forType(Class<T> type, Factory<T, R> factory) {
        return new FactoryChainImpl(FactoryChainImpl.isExactType(type), factory);
    }

    /**
     * @see FactoryChain#thenForAny(QualifyingFactory)
     */
    public static <T, R extends T> FactoryChain forAny(QualifyingFactory<T, R> factory) {
        return new FactoryChainImpl(FactoryChainImpl.tautology(), factory);
    }

    /**
     * @see FactoryChain#thenForSuperOf(Class, QualifyingFactory)
     */
    public static <T, R extends T> FactoryChain forSuperOf(Class<T> type, QualifyingFactory<T, R> factory) {
        return new FactoryChainImpl(FactoryChainImpl.isSuperOf(type), factory);
    }

    /**
     * @see FactoryChain#thenForType(Class, QualifyingFactory)
     */
    public static <T, R extends T> FactoryChain forType(Class<T> type, QualifyingFactory<T, R> factory) {
        return new FactoryChainImpl(FactoryChainImpl.isExactType(type), factory);
    }

    /**
     * @see FactoryChain#thenForAny(TypeLessFactory)
     */
    public static <R> FactoryChain forAny(TypeLessFactory<R> factory) {
        return new FactoryChainImpl(FactoryChainImpl.tautology(), factory);
    }

    /**
     * @see FactoryChain#thenForSuperOf(Class, TypeLessFactory)
     */
    public static <T, R extends T> FactoryChain forSuperOf(Class<T> type, TypeLessFactory<R> factory) {
        return new FactoryChainImpl(FactoryChainImpl.isSuperOf(type), factory);
    }

    /**
     * @see FactoryChain#thenForType(Class, TypeLessFactory)
     */
    public static <T, R extends T> FactoryChain forType(Class<T> type, TypeLessFactory<R> factory) {
        return new FactoryChainImpl(FactoryChainImpl.isExactType(type), factory);
    }
}
