package foundation.stack.datamill.configuration.impl;

import foundation.stack.datamill.configuration.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class FactoryChainImpl implements FactoryChain {
    public static <T> Predicate<Class<?>> isExactType(Class<T> factoryType) {
        return type -> type == factoryType;
    }

    public static <T> Predicate<Class<?>> isSuperOf(Class<T> factoryType) {
        return type -> type.isAssignableFrom(factoryType);
    }

    public static Predicate<Class<?>> tautology() {
        return __ -> true;
    }

    private final List<FactoryDelegate> delegates;

    public FactoryChainImpl(Predicate<Class<?>> condition, TypeLessFactory<?> factory) {
        this(condition, Factory.wrap(factory));
    }

    public FactoryChainImpl(Predicate<Class<?>> condition, Factory<?, ?> factory) {
        this(condition, QualifyingFactory.wrap(factory));
    }

    public FactoryChainImpl(Predicate<Class<?>> condition, QualifyingFactory<?, ?> factory) {
        this.delegates = Collections.singletonList(new FactoryDelegate(condition, factory));
    }

    private FactoryChainImpl(List<FactoryDelegate> delegates) {
        this.delegates = delegates;
    }

    @Override
    public Object call(Wiring wiring, Class<? extends Object> type, Collection<String> qualifiers) {
        for (FactoryDelegate delegate : delegates) {
            if (delegate.condition.test(type)) {
                Object constructed = delegate.factory.call(wiring, (Class) type, qualifiers);
                if (constructed != null) {
                    return constructed;
                }
            }
        }

        return null;
    }

    private FactoryChain withAppendedDelegate(FactoryDelegate delegate) {
        ArrayList<FactoryDelegate> delegates = new ArrayList<>(this.delegates);
        delegates.add(delegate);
        return new FactoryChainImpl(delegates);
    }

    @Override
    public <T, R extends T> FactoryChain thenForAny(Factory<T, R> factory) {
        return thenForAny(QualifyingFactory.wrap(factory));
    }

    @Override
    public FactoryChain thenForAnyConcreteClass() {
        return thenForAny(ConcreteClassFactory.instance());
    }

    @Override
    public <T, R extends T> FactoryChain thenForSuperOf(Class<T> type, Factory<T, R> factory) {
        return thenForSuperOf((Class) type, QualifyingFactory.wrap(factory));
    }

    @Override
    public <T, R extends T> FactoryChain thenForType(Class<T> type, Factory<T, R> factory) {
        return thenForType((Class) type, QualifyingFactory.wrap(factory));
    }

    @Override
    public <R> FactoryChain thenForAny(TypeLessFactory<R> factory) {
        return thenForAny(Factory.wrap(factory));
    }

    @Override
    public <T, R extends T> FactoryChain thenForSuperOf(Class<T> type, TypeLessFactory<R> factory) {
        return thenForSuperOf((Class) type, Factory.wrap(factory));
    }

    @Override
    public <T, R extends T> FactoryChain thenForType(Class<T> type, TypeLessFactory<R> factory) {
        return thenForType((Class) type, Factory.wrap(factory));
    }

    @Override
    public <T, R extends T> FactoryChain thenForAny(QualifyingFactory<T, R> factory) {
        return withAppendedDelegate(new FactoryDelegate(tautology(), factory));
    }

    @Override
    public <T, R extends T> FactoryChain thenForSuperOf(Class<T> type, QualifyingFactory<T, R> factory) {
        return withAppendedDelegate(new FactoryDelegate(isSuperOf(type), factory));
    }

    @Override
    public <T, R extends T> FactoryChain thenForType(Class<T> type, QualifyingFactory<T, R> factory) {
        return withAppendedDelegate(new FactoryDelegate(isExactType(type), factory));
    }

    private static class FactoryDelegate {
        private final Predicate<Class<?>> condition;
        private final QualifyingFactory<?, ?> factory;

        public FactoryDelegate(Predicate<Class<?>> condition, QualifyingFactory<?, ?> factory) {
            this.condition = condition;
            this.factory = factory;
        }
    }
}
