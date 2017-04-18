package foundation.stack.datamill.configuration;

import foundation.stack.datamill.values.StringValue;
import foundation.stack.datamill.values.Value;
import rx.functions.Func1;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * <p>
 * Wirings form the basis of a lightweight dependency injection (DI) mechanism. Wirings support DI through public
 * constructor injection - they are in fact a version of the factory pattern.
 * </p>
 * <p>
 * For example, consider:
 * <pre>
 * public class UserRepository {
 *     public UserRepository(DataStore dataStore) {
 *     }
 * }
 * </pre>
 * </p>
 * <p>
 * You can use a Wiring to create a UserRepository instance:
 * <pre>
 * UserRepository repository = new Wiring().newInstance(UserRepository.class);
 * </pre>
 * </p>
 * <p>
 * This constructs a new UserRepository using the public constructor, creating an instance of DataStore, and any
 * dependencies DataStore may have in the process. How did the wiring know how to construct DataStore and it's
 * dependencies? By default, a Wiring will automatically create instances of concrete classes for which it can
 * transitively create dependencies. But what if we have an interface, or a class that has dependencies that we cannot
 * be satisfied? You setup the Wiring with a {@link FactoryChain} that tells it how to construct specific classes.
 * For example, consider:
 * </p>
 * <pre>
 * public class ApplicationRepository {
 *     public ApplicationRepository(DatabaseClient databaseClient, OutlineBuilder outlineBuilder) {
 *     }
 * }
 * </pre>
 * <pre>
 * FactoryChain chain =
 *     FactoryChains.forType(DatabaseClient.class, w -> new DatabaseClient(...))
 *         .thenForType(OutlineBuilder.class, w -> OutlineBuilder.DEFAULT)
 *         .thenForAnyConcreteClass();
 * ApplicationRepository repository = new Wiring(chain).newInstance(ApplicationRepository.class);
 * </pre>
 * <p>
 * Note that the {@link FactoryChain} we built and passed in to the Wiring will tell it exactly how to build objects of
 * the types it encounters. The chain will be consulted in the order it is composed - in this case, when the Wiring
 * needs to construct a DatabaseClient, it will use the first lambda provided. If it needs an OutlineBuilder, it
 * will use the second lambda and for all other concrete classes, it will use a concrete class factory to construct them.
 * It will use the chain every time a dependency needs to be constructed, starting at the beginning. Use
 * {@link FactoryChains} as a starting point for creating factory chains.
 * </p>
 * <p>
 * Notice that in the previous examples we called the newInstance method on the Wiring. This will create a new instance
 * of that class every single time. While this is sometimes the desired behavior, most of the time, you will want to
 * use {@link Wiring#singleton(Class)} instead to create a singleton instance of a type. If you call singleton to
 * construct instances of some type multiple times, you will get back the same singleton instance every time. The
 * singletons are scoped to the {@link Wiring} itself, and it's default singleton {@link Scope}. A different
 * {@link Scope} can be passed in to a Wiring when it is constructed to change how instances are scoped. If you pass in
 * a different scope, you can use the {@link Wiring#defaultScoped(Class)} method to construct instances using that scope.
 * </p>
 * <p>
 * Occasionally, you may have multiple parameters of the same type in a constructor and you want to have the Wiring
 * create different instances for each parameter. For example, consider:
 * </p>
 * <pre>
 * public class UserController {
 *     public UserController(Func1&lt;ServerRequest, Boolean&gt; authenticationFilter, Func1&lt;ServerRequest, Boolean&gt; authorizationFilter) {
 *     }
 * }
 * </pre>
 * <p>
 * You can add {@link Qualifier}s to these parameters to tell the Wiring to inject different instances:
 * </p>
 * <pre>
 * public class UserController {
 *     public UserController(
 *         @Qualifier("authentication") Func1&lt;ServerRequest, Boolean&gt; authenticationFilter,
 *         @Qualifier("authorization") Func1&lt;ServerRequest, Boolean&gt; authorizationFilter) {
 *     }
 * }
 * </pre>
 * <p>
 * You can now add a {@link QualifyingFactory} to your {@link FactoryChain} in order to construct different instances
 * based on the qualifier:
 * </p>
 * <pre>
 * FactoryChain chain =
 *     FactoryChains.forType(Func1.class, (w, c, q) -> q.contains("authentication") ?
 *             authenticationFilter() : authorizationFilter())
 *         .thenForAnyConcreteClass();
 * UserController controller = new Wiring(chain).singleton(UserController.class);
 * </pre>
 * <p>
 * In addition to using the factory chain to create dependencies, a Wiring will also resolve any {@link Named}
 * constructor parameters using a {@link PropertySource} it is setup with. This is useful for picking up externalized
 * properties from files, environment variables, system properties, constants classes and other sources. For example,
 * consider:
 * <pre>
 * public class DatabaseClient {
 *     public DatabaseClient(@Named("url") String url, @Named("username") String username, @Named("password") String password) {
 *     }
 * }
 * </pre>
 * </p>
 * <p>
 * You can use a Wiring to construct a DatabaseClient using the named parameters:
 * <pre>
 * DatabaseClient client = new Wiring(
 *     PropertySources.fromFile("database.properties")
 *         .orImmediate(s -> s
 *             .put("url", "jdbc:mysql://localhost:3306")
 *             .put("username", "dbuser")
 *             .put("password", "dbpass")))
 *     .singleton(DatabaseClient.class);
 * </pre>
 * </p>
 * <p>
 * This constructs a new DatabaseClient using the constructor shown, injecting the provided named values as parameters.
 * As with factories, property sources are setup as a chain - you create {@link PropertySourceChain}s using
 * {@link PropertySources} as a starting point. You can see how in this example, the properties will first be looked up
 * in a "database.properties" file, and will use some defaults as a fallback. Note that only Strings, primitives and
 * primitive wrappers are supported for {@link Named} parameters.
 * </p>
 *
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Wiring {
    private final PropertySource propertySource;
    private final FactoryChain factoryChain;
    private final Scope defaultScope;
    private final Scope singletonScope;

    public Wiring(PropertySource propertySource, FactoryChain factoryChain, Scope defaultScope) {
        this.propertySource = propertySource;
        this.singletonScope = new SingletonScope();
        this.defaultScope = defaultScope != null ? defaultScope : singletonScope;
        this.factoryChain = factoryChain;
    }

    public Wiring(PropertySource propertySource, FactoryChain factoryChain) {
        this(propertySource, factoryChain, null);
    }

    public Wiring(FactoryChain factoryChain, Scope defaultScope) {
        this(null, factoryChain, defaultScope);
    }

    public Wiring(FactoryChain factoryChain) {
        this(factoryChain, null);
    }

    public Wiring(PropertySource propertySource, Scope defaultScope) {
        this(propertySource, FactoryChains.forAnyConcreteClass(), defaultScope);
    }

    public Wiring(PropertySource propertySource) {
        this(propertySource, (Scope) null);
    }

    public Wiring() {
        this((PropertySource) null);
    }

    public Wiring(Wiring wiring, FactoryChain factoryChain) {
        this(wiring.propertySource, factoryChain, wiring.defaultScope, wiring.singletonScope);
    }

    private Wiring(PropertySource propertySource, FactoryChain factoryChain, Scope defaultScope, Scope singletonScope) {
        this.propertySource = propertySource;
        this.singletonScope = singletonScope;
        this.defaultScope = defaultScope;
        this.factoryChain = factoryChain;
    }

    public FactoryChain getFactoryChain() {
        return factoryChain;
    }

    public Value getRequiredNamed(String property) {
        return getNamed(property)
                .orElseThrow(() -> new IllegalStateException("Required named property [" + property + "] not present!"));
    }

    public Optional<Value> getNamed(String property) {
        return propertySource != null ?
                propertySource.get(property).map(value -> new StringValue(value)) :
                Optional.empty();
    }

    private <T, R extends T> R construct(Scope scope, Class<? extends T> type, Collection<String> qualifiers) {
        return scope != null ?
                (R) scope.resolve(this, factoryChain, (Class) type, qualifiers) :
                (R) factoryChain.call(this, (Class) type, qualifiers);
    }

    public <T, R extends T> R defaultScoped(Class<? extends T> type) {
        return (R) defaultScoped(type, Collections.emptyList());
    }

    public <T, R extends T> R defaultScoped(Class<? extends T> type, String... qualifiers) {
        return (R) defaultScoped(type, Arrays.asList(qualifiers));
    }

    public <T, R extends T> R defaultScoped(Class<? extends T> type, Collection<String> qualifiers) {
        return (R) construct(defaultScope, type, qualifiers);
    }

    public <T, R extends T> R newInstance(Class<T> type) {
        return newInstance(type, Collections.emptyList());
    }

    public <T, R extends T> R newInstance(Class<T> type, String... qualifiers) {
        return newInstance(type, Arrays.asList(qualifiers));
    }

    public <T, R extends T> R newInstance(Class<T> type, Collection<String> qualifiers) {
        return construct(null, type, qualifiers);
    }

    public <T, R extends T> R singleton(Class<T> type) {
        return singleton(type, Collections.emptyList());
    }

    public <T, R extends T> R singleton(Class<T> type, String... qualifiers) {
        return singleton(type, Arrays.asList(qualifiers));
    }

    public <T, R extends T> R singleton(Class<T> type, Collection<String> qualifiers) {
        return construct(singletonScope, type, qualifiers);
    }

    public <R> R with(PropertySource propertySource, FactoryChain factoryChain, Func1<Wiring, R> wiringConsumer) {
        return wiringConsumer.call(new Wiring(propertySource, factoryChain, defaultScope, singletonScope));
    }

    public <R> R with(PropertySource propertySource, Func1<Wiring, R> wiringConsumer) {
        return with(propertySource, factoryChain, wiringConsumer);
    }

    public <R> R with(FactoryChain factoryChain, Func1<Wiring, R> wiringConsumer) {
        return with(propertySource, factoryChain, wiringConsumer);
    }

    public <R> R with(Func1<Wiring, R> wiringConsumer) {
        return wiringConsumer.call(this);
    }
}
