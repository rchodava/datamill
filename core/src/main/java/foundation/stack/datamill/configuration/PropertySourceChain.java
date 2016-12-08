package foundation.stack.datamill.configuration;

import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Allows {@link PropertySource}s to be linked together into a chain so that alternative sources of desired properties
 * can be specified when looking up configuration properties. For example, consider a chain composed and used as follows:
 * <p/>
 * <pre>
 * chain = Properties.fromFile("service.properties").orFile("defaults.properties").orEnvironment()
 *     .orDefaults(defaults -> defaults.put("property1", "value1").put("property2", "value2"));
 *
 * chain.getRequired("property1");
 * chain.getRequired("property3");
 * </pre>
 * <p/>
 * This chain will first attempt to retrieve requested properties from the service.properties file. It will consult the
 * defaults.properties file next if it can't find properties in service.properties. Subsequently, it will attempt to
 * look for those properties amongst environment variables if it still fails to find a requested property. Finally,
 * it will return the defaults specified by the orDefaults(...) call.
 * <p/>
 * Note that each of the chaining methods returns a new chain with the additional property source added.
 *
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface PropertySourceChain extends PropertySource {
    /**
     * @see PropertySource#alias(String, String)
     */
    PropertySourceChain alias(String alias, String original);

    /**
     * Add a property source to the chain which retrieves properties specified in the file at the specified path.
     *
     * @param path Path to properties file to add as a source.
     */
    PropertySourceChain orFile(String path);

    /**
     * Add a source which looks up properties defined as environment variables to the chain.
     */
    PropertySourceChain orEnvironment();

    /**
     * Add a source which looks up properties defined as environment variables but first transforms the property names
     * using the specified function before looking up the environment variable.
     *
     * @param transformer Transformation function used to transform property names before looking them up as
     *                    environment variables. For example, you may want to prefix property names before looking them
     *                    up as environment variables.
     */
    PropertySourceChain orEnvironment(Func1<String, String> transformer);

    /**
     * Add a custom property source to the chain at this point..
     *
     * @param source Source to add to the chain.
     */
    PropertySourceChain orSource(PropertySource source);

    /**
     * Add a source which looks up properties defined as system properties to the chain.
     */
    PropertySourceChain orSystem();

    /**
     * Add a source which looks up properties defined as system properties but first transforms the property names
     * using the specified function before looking up the system property.
     *
     * @see #orEnvironment(Func1)
     */
    PropertySourceChain orSystem(Func1<String, String> transformer);

    /**
     * Add defaults for properties that cannot be found by any other source in the chain.
     *
     * @param defaultsInitializer Function which sets up defaults for properties not found in the chain.
     */
    PropertySource orDefaults(Action1<Defaults> defaultsInitializer);
}
