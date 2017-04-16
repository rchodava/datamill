package foundation.stack.datamill.configuration;

import foundation.stack.datamill.configuration.impl.*;
import rx.functions.Action1;
import rx.functions.Func1;

import java.io.IOException;

/**
 * <p>
 * Use this class to create some common {@link PropertySource}s. This class can also be used to compose together
 * {@link PropertySource}s into {@link PropertySourceChain}s so that alternative sources of desired properties can be
 * specified when looking up configuration properties. For example, consider a chain composed and used as follows:
 * <p/>
 * <pre>
 * chain = PropertySources.fromFile("service.properties").orFile("defaults.properties").orEnvironment()
 *     .orImmediate(defaults -> defaults.put("property1", "value1").put("property2", "value2"));
 *
 * chain.getRequired("property1");
 * chain.getRequired("property3");
 * </pre>
 * <p>
 * This chain will first attempt to retrieve the requested properties from the service.properties file. It will consult
 * the defaults.properties file next if it can't find properties in service.properties. Subsequently, it will attempt to
 * look for those properties amongst environment variables if it still fails to find a requested property. Finally,
 * it will return the defaults specified by the orImmediate(...) call.
 * <p/>
 * <p>
 * Note that each of the methods accepts a transformer to transform the names of the properties before looking up
 * the property in that particular property source. See {@link PropertyNameTransformers} for some useful common
 * transformers.
 * </p>
 * <p>
 * Note that property chains are immutable, and each of the chaining methods returns a new chain with the additional
 * property source added. This allows you to have common chains which you can combine in different ways, because each
 * of those combinations is a new chain. For example:
 * </p>
 * <pre>
 * chain1 = common.orEnvironment(PropertyNameTransformers.LOWER_CAMEL_TO_UPPER_UNDERSCORE);
 * chain2 = common.orFile("defaults.properties");
 * </pre>
 * <p>
 * Each of these chains is a separate chain that has some common section.
 * <p>
 *
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public final class PropertySources {
    /**
     * @see PropertySourceChain#or(PropertySource)
     */
    public static PropertySourceChain from(PropertySource source) {
        return from(source, null);
    }

    /**
     * @see PropertySourceChain#or(PropertySource, Func1)
     */
    public static PropertySourceChain from(PropertySource source, Func1<String, String> transformer) {
        return new PropertySourceChainImpl(source, transformer);
    }

    /**
     * @see PropertySourceChain#orConstantsClass(Class)
     */
    public static <T> PropertySourceChain fromConstantsClass(Class<T> constantsClass) {
        return fromConstantsClass(constantsClass, null);
    }

    /**
     * @see PropertySourceChain#orConstantsClass(Class, Func1)
     */
    public static <T> PropertySourceChain fromConstantsClass(Class<T> constantsClass, Func1<String, String> transformer) {
        return from(new ConstantsClassSource<>(constantsClass), transformer);
    }

    /**
     * @see PropertySourceChain#orComputed(Func1)
     */
    public static PropertySourceChain fromComputed(Func1<String, String> computation) {
        return fromComputed(computation, null);
    }

    /**
     * @see PropertySourceChain#orComputed(Func1, Func1)
     */
    public static PropertySourceChain fromComputed(Func1<String, String> computation, Func1<String, String> transformer) {
        return from(new ComputedSource(computation), transformer);
    }

    /**
     * @see PropertySourceChain#orEnvironment()
     */
    public static PropertySourceChain fromEnvironment() {
        return fromEnvironment(null);
    }

    /**
     * @see PropertySourceChain#orEnvironment(Func1)
     */
    public static PropertySourceChain fromEnvironment(Func1<String, String> transformer) {
        return from(EnvironmentPropertiesSource.DEFAULT, transformer);
    }

    public static PropertySourceChain fromFile(String path) {
        return fromFile(path, null);
    }

    /**
     * @see PropertySourceChain#orFile(String)
     */
    public static PropertySourceChain fromFile(String path, Func1<String, String> transformer) {
        try {
            return from(new FileSource(path), transformer);
        } catch (IOException e) {
            return from(EmptySource.INSTANCE);
        }
    }

    /**
     * @see PropertySourceChain#orImmediate(Action1)
     */
    public static PropertySourceChain fromImmediate(Action1<ImmediatePropertySource> initializer) {
        return fromImmediate(initializer, null);
    }

    /**
     * @see PropertySourceChain#orImmediate(Action1, Func1)
     */
    public static PropertySourceChain fromImmediate(
            Action1<ImmediatePropertySource> initializer,
            Func1<String, String> transformer) {
        ImmediatePropertySourceImpl immediateSource = new ImmediatePropertySourceImpl();
        initializer.call(immediateSource);

        return from(immediateSource, transformer);
    }

    /**
     * @see PropertySourceChain#orSystem()
     */
    public static PropertySourceChain fromSystem() {
        return fromSystem(null);
    }

    /**
     * @see PropertySourceChain#orSystem(Func1)
     */
    public static PropertySourceChain fromSystem(Func1<String, String> transformer) {
        return from(SystemPropertiesSource.DEFAULT, transformer);
    }

}
