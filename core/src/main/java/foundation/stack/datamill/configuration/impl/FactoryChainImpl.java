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
    private static final Predicate<Class<?>> TAUTOLOGY = __ -> true;

    public static Predicate<Class<?>> isExactType(Class<?> factoryType) {
        return type -> type == factoryType;
    }

    public static Predicate<Class<?>> isSuperOf(Class<?> factoryType) {
        return type -> type.isAssignableFrom(factoryType);
    }

    public static Predicate<Class<?>> tautology() {
        return TAUTOLOGY;
    }

    private final List<FactoryDelegate> delegates;
    private final List<QualifyingFactory<?, ?>> exclusions;

    public FactoryChainImpl(Predicate<Class<?>> condition, TypeLessFactory<?> factory) {
        this(condition, Factory.wrap(factory));
    }

    public FactoryChainImpl(Predicate<Class<?>> condition, Factory<?, ?> factory) {
        this(condition, QualifyingFactory.wrap(factory));
    }

    public FactoryChainImpl(Predicate<Class<?>> condition, QualifyingFactory<?, ?> factory) {
        this.delegates = Collections.singletonList(new FactoryDelegate(condition, factory));
        this.exclusions = null;
    }

    private FactoryChainImpl(List<FactoryDelegate> delegates) {
        this(delegates, null);
    }

    private FactoryChainImpl(List<FactoryDelegate> delegates, List<QualifyingFactory<?, ?>> exclusions) {
        this.delegates = delegates;
        this.exclusions = exclusions;
    }

    @Override
    public Object call(Wiring wiring, Class<? extends Object> type, Collection<String> qualifiers) {
        for (FactoryDelegate delegate : delegates) {
            if (delegate.condition.test(type) &&
                    (exclusions == null || !exclusions.contains(delegate.factory))) {
                Wiring childWiring = shouldExcludeDelegateFactory(delegate) ?
                        new Wiring(wiring, wiring.getFactoryChain().exclude(delegate.factory)) : wiring;

                Object constructed = delegate.factory.call(childWiring, (Class) type, qualifiers);
                if (constructed != null) {
                    return constructed;
                }
            }
        }

        return null;
    }

    @Override
    public FactoryChain exclude(QualifyingFactory<?, ?> factory) {
        ArrayList<QualifyingFactory<?, ?>> exclusions =
                new ArrayList<>(this.exclusions != null ? this.exclusions.size() + 1 : 1);
        if (this.exclusions != null) {
            exclusions.addAll(this.exclusions);
        }

        exclusions.add(factory);

        return new FactoryChainImpl(delegates, exclusions);
    }

    private boolean shouldExcludeDelegateFactory(FactoryDelegate delegate) {
        return delegate.factory != ConcreteClassFactory.instance() && delegate.condition != TAUTOLOGY;
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
    public <T> FactoryChain thenForSuperOf(Class<T> type, Factory<T, ?> factory) {
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
    public <T> FactoryChain thenForSuperOf(Class<T> type, TypeLessFactory<?> factory) {
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
    public <T> FactoryChain thenForSuperOf(Class<T> type, QualifyingFactory<T, ?> factory) {
        return withAppendedDelegate(new FactoryDelegate(isSuperOf(type), factory));
    }

    @Override
    public <T, R extends T> FactoryChain thenForType(Class<T> type, QualifyingFactory<T, R> factory) {
        return withAppendedDelegate(new FactoryDelegate(isExactType(type), factory));
    }

    private FactoryChain withAppendedDelegate(FactoryDelegate delegate) {
        ArrayList<FactoryDelegate> delegates = new ArrayList<>(this.delegates);
        delegates.add(delegate);
        return new FactoryChainImpl(delegates);
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
