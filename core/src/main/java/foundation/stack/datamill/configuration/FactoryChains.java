package foundation.stack.datamill.configuration;

import foundation.stack.datamill.configuration.impl.ConcreteClassFactory;
import foundation.stack.datamill.configuration.impl.FactoryChainImpl;

/**
 * <p>
 * Use this class to create{@link FactoryChain}s. For example, consider a chain composed and used as follows:
 * <p/>
 * <pre>
 * chain = FactoryChains.forType(DatabaseClient.class, w -> createDatabaseClient())
 *     .thenForSuperOf(UserRepository.class, w -> new UserRepository(...))
 *     .thenForAny(Core.FACTORY)
 *     .thenForAnyConcreteClass();
 * </pre>
 * <p>
 * This chain will first check if the class requested is a DatabaseClient, and if so, will construct it. Then, it will
 * check if the class requested is a super-class or interface of the UserRepository class, and if so will construct it.
 * Then, it will delegate to the Core.FACTORY factory chain. Finally, if none of those factories are able to construct
 * the requested instance, the chain will ask the concrete class factory to construct an instance.
 * <p/>
 * <p>
 * Note that if you do not add the concrete class factory at the end of a chain, your chain will only construct the
 * exact types for which your factories can construct instances. Thus, it is often useful to add the concrete class
 * factory to the end of a factory chain.
 * </p>
 * <p>
 * Note that it is easy to construct factory chains that result in infinite recursion. For example, consider the
 * following:
 * </p>
 * <pre>
 * chain = FactoryChains.forSuperOf(EmailService.class, w -> w.singleton(EmailService.class))
 *     .thenForAnyConcreteClass();
 *
 * new Wiring(chain).singleton(EmailService.class);
 * </pre>
 * <p>
 * If the chain is used in this way to construct an instance of the EmailService class (a concrete class), the first
 * factory in the chain is used. This results in turn to a call to the wiring to construct a singleton instance of
 * EmailService. The Wiring will use the same chain, starting at the beginning of the chain. This will again result
 * in the first factory in the chain to be used, and would lead to an infinite recursion. To solve this, when a particular
 * factory in the chain in turn uses the Wiring to construct an instance, the factory will be excluded from the chain in
 * the next call. This will essentially break the infinite recursion - the call to construct the EmailService from the
 * first factory in the chain will go directly to the concrete class factory in the second pass through the chain.
 * </p>
 * <p>
 * Note that factory chains are immutable, and each of the chaining methods returns a new chain with the additional
 * factory added. This allows you to have common chains which you can combine in different ways, because each
 * of those combinations is a new chain.
 * </p>
 *
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
    public static FactoryChain forSuperOf(Class<?> type, Factory<?, ?> factory) {
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
    public static FactoryChain forSuperOf(Class<?> type, QualifyingFactory<?, ?> factory) {
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
    public static FactoryChain forSuperOf(Class<?> type, TypeLessFactory<?> factory) {
        return new FactoryChainImpl(FactoryChainImpl.isSuperOf(type), factory);
    }

    /**
     * @see FactoryChain#thenForType(Class, TypeLessFactory)
     */
    public static <T, R extends T> FactoryChain forType(Class<T> type, TypeLessFactory<R> factory) {
        return new FactoryChainImpl(FactoryChainImpl.isExactType(type), factory);
    }
}
