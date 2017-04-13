package foundation.stack.datamill.configuration.impl;

import foundation.stack.datamill.configuration.Defaults;
import foundation.stack.datamill.configuration.PropertySource;
import foundation.stack.datamill.configuration.PropertySourceChain;
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
    private final List<PropertySource> chain;

    public PropertySourceChainImpl(PropertySource initialSource) {
        chain = Collections.singletonList(initialSource);
    }

    private PropertySourceChainImpl(List<PropertySource> chain) {
        this.chain = chain;
    }

    @Override
    public PropertySourceChain alias(String alias, String original) {
        return (PropertySourceChain) super.alias(alias, original);
    }

    @Override
    public Optional<String> getOptional(String name) {
        for (PropertySource source : chain) {
            Optional<String> value = source.get(name);
            if (value.isPresent()) {
                return value;
            }
        }

        return Optional.empty();
    }

    @Override
    public <T> PropertySourceChain orConstantsClass(Class<T> constantsClass) {
        return orSource(new ConstantsClassSource<>(constantsClass));
    }

    @Override
    public PropertySource orDefaults(Action1<Defaults> defaultsInitializer) {
        DefaultsSource defaults = new DefaultsSource();
        defaultsInitializer.call(defaults);

        return orSource(defaults);
    }

    @Override
    public PropertySourceChain orEnvironment() {
        return orSource(EnvironmentPropertiesSource.IDENTITY);
    }

    @Override
    public PropertySourceChain orEnvironment(Func1<String, String> transformer) {
        return orSource(new EnvironmentPropertiesSource(transformer));
    }

    @Override
    public PropertySourceChain orFile(String path) {
        try {
            return orSource(new FileSource(path));
        } catch (IOException e) {
            return orSource(EmptySource.INSTANCE);
        }
    }

    @Override
    public PropertySourceChain orSource(PropertySource source) {
        ArrayList<PropertySource> newChain = new ArrayList<>(chain);
        newChain.add(source);

        return new PropertySourceChainImpl(newChain);
    }

    @Override
    public PropertySourceChain orSystem() {
        return orSource(SystemPropertiesSource.IDENTITY);
    }

    @Override
    public PropertySourceChain orSystem(Func1<String, String> transformer) {
        return orSource(new SystemPropertiesSource(transformer));
    }
}
