package foundation.stack.datamill.configuration;

import rx.functions.Action1;
import rx.functions.Func1;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface PropertySourceChainContinuation extends PropertySource {
    /**
     * Add a property source to the chain which computes the value of property using the specified function. The
     * function can return either a value, or a null to indicate it cannot compute the value of the property - in this
     * case, the chain will look to the next source.
     *
     * @param computation Computation function for the source.
     */
    PropertySourceChain orComputed(Func1<String, String> computation);

    /**
     * @See PropertySourceChain
     * @see #orComputed(Func1)
     */
    PropertySourceChain orComputed(Func1<String, String> computation, Func1<String, String> transformer);

    /**
     * Add a property source to the chain which retrieves properties from a constants interface or class. The interface
     * or class is expected to define String constants that are annotated with {@link StringValue} annotations.
     *
     * @param constantsClass Constants class to add as a source.
     */
    <T> PropertySourceChain orConstantsClass(Class<T> constantsClass);

    /**
     * @See PropertySourceChain
     * @see #orConstantsClass(Class)
     */
    <T> PropertySourceChain orConstantsClass(Class<T> constantsClass, Func1<String, String> transformer);

    /**
     * Add immediate set of properties that can be looked up at this point in the chain.
     *
     * @param initializer Function which sets up immediate properties at this point in the chain.
     */
    PropertySourceChain orImmediate(Action1<ImmediatePropertySource> initializer);

    /**
     * @See PropertySourceChain
     * @see #orImmediate(Action1)
     */
    PropertySourceChain orImmediate(Action1<ImmediatePropertySource> initializer, Func1<String, String> transformer);

    /**
     * Add a property source to the chain which retrieves properties specified in the file at the specified path.
     *
     * @param path Path to properties file to add as a source.
     */
    PropertySourceChain orFile(String path);

    /**
     * @See PropertySourceChain
     * @see #orFile(String)
     */
    PropertySourceChain orFile(String path, Func1<String, String> transformer);

    /**
     * Add a source which looks up properties defined as environment variables to the chain.
     */
    PropertySourceChain orEnvironment();

    /**
     * @See PropertySourceChain
     * @see #orEnvironment()
     */
    PropertySourceChain orEnvironment(Func1<String, String> transformer);

    /**
     * Add a custom property source to the chain at this point.
     *
     * @param source Source to add to the chain.
     */
    PropertySourceChain orSource(PropertySource source);

    /**
     * @See PropertySourceChain
     * @see #orSource(PropertySource)
     */
    PropertySourceChain orSource(PropertySource source, Func1<String, String> transformer);

    /**
     * Add a source which looks up properties defined as system properties to the chain.
     */
    PropertySourceChain orSystem();

    /**
     * @See PropertySourceChain
     * @see #orSystem()
     */
    PropertySourceChain orSystem(Func1<String, String> transformer);
}
