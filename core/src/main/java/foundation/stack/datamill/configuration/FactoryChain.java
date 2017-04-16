package foundation.stack.datamill.configuration;

/**
 * A chain of object factories used by {@link Wiring}s to create objects. Use {@link FactoryChains} to create
 * {@link FactoryChain}s.
 *
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface FactoryChain extends QualifyingFactory<Object, Object> {
    /**
     * Remove a factory from the chain, returning a chain without the specified factory.
     */
    FactoryChain exclude(QualifyingFactory<?, ?> factory);

    /**
     * Add a factory which will be used to attempt construction of instances for all types.
     */
    <T, R extends T> FactoryChain thenForAny(Factory<T, R> factory);

    /**
     * Add a factory which will be used to attempt construction of instances of concrete classes.
     */
    FactoryChain thenForAnyConcreteClass();

    /**
     * Add a factory for a type, and it's super-classes and interfaces. Note that this factory will be invoked if the
     * type requested is exactly the specified type, or one of it's super-classes or interfaces.
     */
    <T> FactoryChain thenForSuperOf(Class<T> type, Factory<T, ?> factory);

    /**
     * Add a factory for a specific type. Note that this factory is only invoked if the type being constructed matches
     * the exact type specified.
     *
     * @param type Exact type for which the specified factory will be used.
     */
    <T, R extends T> FactoryChain thenForType(Class<T> type, Factory<T, R> factory);

    /**
     * @see #thenForAny(Factory)
     */
    <R> FactoryChain thenForAny(TypeLessFactory<R> factory);

    /**
     * @see #thenForSuperOf(Class, Factory)
     */
    <T> FactoryChain thenForSuperOf(Class<T> type, TypeLessFactory<?> factory);

    /**
     * @see #thenForType(Class, Factory)
     */
    <T, R extends T> FactoryChain thenForType(Class<T> type, TypeLessFactory<R> factory);

    /**
     * Add a qualifying factory which will be used to attempt construction of instances for all types.
     *
     * @see #thenForAny(Factory)
     */
    <T, R extends T> FactoryChain thenForAny(QualifyingFactory<T, R> factory);

    /**
     * Add a qualifying factory for a type, and it's super-classes and interfaces.
     *
     * @see #thenForSuperOf(Class, Factory)
     */
    <T> FactoryChain thenForSuperOf(Class<T> type, QualifyingFactory<T, ?> factory);

    /**
     * Add a qualifying factory for a specific type.
     *
     * @see #thenForType(Class, Factory)
     */
    <T, R extends T> FactoryChain thenForType(Class<T> type, QualifyingFactory<T, R> factory);
}
