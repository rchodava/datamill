package foundation.stack.datamill.configuration;

import com.google.common.base.CaseFormat;
import foundation.stack.datamill.configuration.impl.*;
import rx.functions.Action1;
import rx.functions.Func1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * Allows {@link PropertySource}s to be composed together into a chain so that alternative sources of desired properties
 * can be specified when looking up configuration properties. For example, consider a chain composed and used as follows:
 * <p/>
 * <pre>
 * chain = PropertySourceChain.ofFile("service.properties").orFile("defaults.properties").orEnvironment()
 *     .orImmediate(defaults -> defaults.put("property1", "value1").put("property2", "value2"));
 *
 * chain.getRequired("property1");
 * chain.getRequired("property3");
 * </pre>
 * <p>
 * This chain will first attempt to retrieve requested properties from the service.properties file. It will consult the
 * defaults.properties file next if it can't find properties in service.properties. Subsequently, it will attempt to
 * look for those properties amongst environment variables if it still fails to find a requested property. Finally,
 * it will return the defaults specified by the orImmediate(...) call.
 * <p/>
 * <p>
 * Note that each of the methods accepts a transformer to transform the names of the properties before looking up
 * the property in that particular property source. See {@link Transformers} for some useful common transformers.
 * </p>
 * <p>
 * Note that each of the chaining methods returns a new chain with the additional property source added. This allows you
 * to have common chains which you can combine in different ways, because each of those combinations is a new chain. For
 * example:
 * </p>
 * <pre>
 * chain1 = COMMON.orEnvironment(property -> CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, property));
 * chain2 = COMMON.orFile("defaults.properties");
 * </pre>
 * <p>
 * Each of these chains is a separate chain that has some common section.
 * <p>
 *
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public final class PropertySourceChain extends AbstractSource implements PropertySourceChainContinuation {
    /**
     * @see PropertySourceChainContinuation#orConstantsClass(Class)
     */
    public static <T> PropertySourceChainContinuation ofConstantsClass(Class<T> constantsClass) {
        return ofSource(new ConstantsClassSource<>(constantsClass));
    }

    /**
     * @see PropertySourceChainContinuation#orConstantsClass(Class, Func1)
     */
    public static <T> PropertySourceChainContinuation ofConstantsClass(Class<T> constantsClass, Func1<String, String> transformer) {
        return ofSource(new ConstantsClassSource<>(constantsClass), transformer);
    }

    /**
     * @see PropertySourceChainContinuation#orComputed(Func1)
     */
    public static PropertySourceChainContinuation ofComputed(Func1<String, String> computation) {
        return ofComputed(computation, null);
    }

    /**
     * @see PropertySourceChainContinuation#orComputed(Func1, Func1)
     */
    public static PropertySourceChainContinuation ofComputed(Func1<String, String> computation, Func1<String, String> transformer) {
        return ofSource(new ComputedSource(computation), transformer);
    }

    public static PropertySourceChainContinuation ofFile(String path) {
        return ofFile(path, null);
    }

    /**
     * @see PropertySourceChainContinuation#orFile(String)
     */
    public static PropertySourceChainContinuation ofFile(String path, Func1<String, String> transformer) {
        try {
            return ofSource(new FileSource(path), transformer);
        } catch (IOException e) {
            return ofSource(EmptySource.INSTANCE);
        }
    }

    /**
     * @see PropertySourceChainContinuation#orImmediate(Action1)
     */
    public static PropertySourceChainContinuation ofImmediate(Action1<ImmediatePropertySource> initializer) {
        return ofImmediate(initializer, null);
    }

    /**
     * @see PropertySourceChainContinuation#orImmediate(Action1, Func1)
     */
    public static PropertySourceChainContinuation ofImmediate(Action1<ImmediatePropertySource> initializer, Func1<String, String> transformer) {
        ImmediatePropertySourceImpl immediateSource = new ImmediatePropertySourceImpl();
        initializer.call(immediateSource);

        return ofSource(immediateSource, transformer);
    }

    /**
     * @see PropertySourceChainContinuation#orSource(PropertySource)
     */
    public static PropertySourceChainContinuation ofSource(PropertySource source) {
        return new PropertySourceChain(source, null);
    }

    /**
     * @see PropertySourceChainContinuation#orSource(PropertySource, Func1)
     */
    public static PropertySourceChainContinuation ofSource(PropertySource source, Func1<String, String> transformer) {
        return new PropertySourceChain(source, transformer);
    }

    /**
     * @see PropertySourceChainContinuation#orEnvironment()
     */
    public static PropertySourceChainContinuation ofEnvironment() {
        return ofEnvironment(null);
    }

    /**
     * @see PropertySourceChainContinuation#orEnvironment(Func1)
     */
    public static PropertySourceChainContinuation ofEnvironment(Func1<String, String> transformer) {
        return ofSource(EnvironmentPropertiesSource.DEFAULT, transformer);
    }

    /**
     * @see PropertySourceChainContinuation#orSystem()
     */
    public static PropertySourceChainContinuation ofSystem() {
        return ofSystem(null);
    }

    /**
     * @see PropertySourceChainContinuation#orSystem(Func1)
     */
    public static PropertySourceChainContinuation ofSystem(Func1<String, String> transformer) {
        return ofSource(SystemPropertiesSource.DEFAULT, transformer);
    }

    private final List<TransformedSource> chain;

    private PropertySourceChain(PropertySource initialSource, Func1<String, String> transformer) {
        chain = Collections.singletonList(new TransformedSource(initialSource, transformer));
    }

    private PropertySourceChain(List<TransformedSource> chain) {
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
    public PropertySourceChain orComputed(Func1<String, String> computation) {
        return orSource(new ComputedSource(computation), null);
    }

    @Override
    public PropertySourceChain orComputed(Func1<String, String> computation, Func1<String, String> transformer) {
        return orSource(new ComputedSource(computation), transformer);
    }

    @Override
    public <T> PropertySourceChain orConstantsClass(Class<T> constantsClass) {
        return orConstantsClass(constantsClass, null);
    }

    @Override
    public <T> PropertySourceChain orConstantsClass(Class<T> constantsClass, Func1<String, String> transformer) {
        return orSource(new ConstantsClassSource<>(constantsClass), transformer);
    }

    @Override
    public PropertySourceChain orImmediate(Action1<ImmediatePropertySource> initializer) {
        return orImmediate(initializer, null);
    }

    @Override
    public PropertySourceChain orImmediate(Action1<ImmediatePropertySource> initializer, Func1<String, String> transformer) {
        ImmediatePropertySourceImpl defaults = new ImmediatePropertySourceImpl();
        initializer.call(defaults);

        return orSource(defaults, transformer);
    }

    @Override
    public PropertySourceChain orFile(String path) {
        return orFile(path, null);
    }

    @Override
    public PropertySourceChain orFile(String path, Func1<String, String> transformer) {
        try {
            return orSource(new FileSource(path), transformer);
        } catch (IOException e) {
            return orSource(EmptySource.INSTANCE);
        }
    }

    @Override
    public PropertySourceChain orEnvironment() {
        return orEnvironment(null);
    }

    @Override
    public PropertySourceChain orEnvironment(Func1<String, String> transformer) {
        return orSource(EnvironmentPropertiesSource.DEFAULT, transformer);
    }

    @Override
    public PropertySourceChain orSource(PropertySource source) {
        return orSource(source, null);
    }

    @Override
    public PropertySourceChain orSource(PropertySource source, Func1<String, String> transformer) {
        ArrayList<TransformedSource> newChain = new ArrayList<>(chain);
        newChain.add(new TransformedSource(source, transformer));

        return new PropertySourceChain(newChain);
    }

    @Override
    public PropertySourceChain orSystem() {
        return orSystem(null);
    }

    @Override
    public PropertySourceChain orSystem(Func1<String, String> transformer) {
        return orSource(SystemPropertiesSource.DEFAULT, transformer);
    }

    private static class TransformedSource {
        private final PropertySource propertySource;
        private final Func1<String, String> transformer;

        public TransformedSource(PropertySource propertySource, Func1<String, String> transformer) {
            this.propertySource = propertySource;
            this.transformer = transformer != null ? transformer : Transformers.IDENTITY;
        }
    }

    /**
     * Some common transformers that are useful when creating {@link PropertySourceChain}.
     */
    public static class Transformers {
        private static final Func1<String, String> IDENTITY = name -> name;

        /**
         * Transforms from lower-camel case to upper-underscore case, for example: propertyName -> PROPERTY_NAME.
         */
        public static final Func1<String, String> LOWER_CAMEL_TO_UPPER_UNDERSCORE =
                name -> CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);

        /**
         * Same as calling {@link #leaf(char)} with '/'.
         */
        public static final Func1<String, String> LEAF = leaf('/');

        /**
         * <p>
         * Returns a transformation function that applies the first function, and then the second function on the result
         * of the first. So compose(LEAF, LOWER_CAMEL_TO_UPPER_UNDERSCORE) will return a function that transforms:
         * </p>
         * <p>
         * category/propertyName -> PROPERTY_NAME
         * </p>
         */
        public static final Func1<String, String> compose(Func1<String, String> a, Func1<String, String> b) {
            return name -> b.call(a.call(name));
        }

        /**
         * <p>
         * Returns a transformation function that returns the leaf name of a name separated by the specified separator
         * character. For example:
         * </p>
         * <p>
         * leaf('/') returns a function which transforms: category/propertyName -> propertyName
         * leaf(':') returns a function which transforms: category:propertyName -> propertyName
         * </p>
         */
        public static final Func1<String, String> leaf(char separator) {
            return name -> {
                if (name != null) {
                    int leafSeparator = name.lastIndexOf('/');
                    if (leafSeparator > 0) {
                        return name.substring(leafSeparator + 1);
                    }
                }

                return name;
            };
        }
    }

}
