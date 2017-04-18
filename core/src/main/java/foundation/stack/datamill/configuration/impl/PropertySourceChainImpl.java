package foundation.stack.datamill.configuration.impl;

import foundation.stack.datamill.configuration.*;
import rx.functions.Action1;
import rx.functions.Func1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class PropertySourceChainImpl extends AbstractSource implements PropertySourceChain {
    private static final Func1<String, String> IDENTITY = name -> name;

    private final List<TransformedSource> chain;

    public PropertySourceChainImpl(PropertySource initialSource, Func1<String, String> transformer) {
        chain = Collections.singletonList(new TransformedSource(initialSource, transformer));
    }

    private PropertySourceChainImpl(List<TransformedSource> chain) {
        this.chain = chain;
    }

    @Override
    public Optional<String> getOptional(String name) {
        for (TransformedSource source : chain) {
            Optional<String> value = source.propertySource.get(source.transformer.call(name));
            if (value.isPresent()) {
                return value;
            }
        }

        return Optional.empty();
    }

    @Override
    public PropertySourceChain or(PropertySource source) {
        return or(source, null);
    }

    @Override
    public PropertySourceChain or(PropertySource source, Func1<String, String> transformer) {
        ArrayList<TransformedSource> newChain = new ArrayList<>(chain);
        newChain.add(new TransformedSource(source, transformer));

        return new PropertySourceChainImpl(newChain);
    }

    @Override
    public PropertySourceChain orComputed(Func1<String, String> computation) {
        return or(new ComputedSource(computation), null);
    }

    @Override
    public PropertySourceChain orComputed(Func1<String, String> computation, Func1<String, String> transformer) {
        return or(new ComputedSource(computation), transformer);
    }

    @Override
    public <T> PropertySourceChain orConstantsClass(Class<T> constantsClass) {
        return orConstantsClass(constantsClass, null);
    }

    @Override
    public <T> PropertySourceChain orConstantsClass(Class<T> constantsClass, Func1<String, String> transformer) {
        return or(new ConstantsClassSource<>(constantsClass), transformer);
    }

    @Override
    public PropertySourceChain orEnvironment() {
        return orEnvironment(null);
    }

    @Override
    public PropertySourceChain orEnvironment(Func1<String, String> transformer) {
        return or(EnvironmentPropertiesSource.DEFAULT, transformer);
    }

    @Override
    public PropertySourceChain orFile(String path) {
        return orFile(path, null);
    }

    @Override
    public PropertySourceChain orFile(String path, Func1<String, String> transformer) {
        try {
            return or(new FileSource(path), transformer);
        } catch (IOException e) {
            return or(EmptySource.INSTANCE);
        }
    }

    @Override
    public PropertySourceChain orImmediate(Action1<ImmediatePropertySource> initializer) {
        return orImmediate(initializer, null);
    }

    @Override
    public PropertySourceChain orImmediate(Action1<ImmediatePropertySource> initializer, Func1<String, String> transformer) {
        ImmediatePropertySourceImpl defaults = new ImmediatePropertySourceImpl();
        initializer.call(defaults);

        return or(defaults, transformer);
    }

    @Override
    public PropertySourceChain orSystem() {
        return orSystem(null);
    }

    @Override
    public PropertySourceChain orSystem(Func1<String, String> transformer) {
        return or(SystemPropertiesSource.DEFAULT, transformer);
    }

    private static class TransformedSource {
        private final PropertySource propertySource;
        private final Func1<String, String> transformer;

        public TransformedSource(PropertySource propertySource, Func1<String, String> transformer) {
            this.propertySource = propertySource;
            this.transformer = transformer != null ? transformer : IDENTITY;
        }
    }
}
