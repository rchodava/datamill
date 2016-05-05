package org.chodavarapu.datamill.configuration;

import org.chodavarapu.datamill.reflection.Bean;
import org.chodavarapu.datamill.values.StringValue;
import org.chodavarapu.datamill.values.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.functions.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.Consumer;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ConfigurationBuilder<T> {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationBuilder.class);

    private final Bean<T> bean;

    public ConfigurationBuilder(Bean<T> bean) {
        this.bean = bean;
    }

    public ConfigurationBuilder<T> configure(Consumer<T> configuration) {
        configuration.accept(get());
        return this;
    }

    public ConfigurationBuilder<T> configure(Action2<ConfigurationBuilder<T>, T> configuration) {
        configuration.call(this, get());
        return this;
    }

    public T get() {
        return bean.unwrap();
    }

    public ConfigurationBuilder<T> ifSystemPropertyExists(String name, Consumer<ConfigurationBuilder<T>> configuration) {
        return ifSystemPropertyExists(name, configuration, null);
    }

    public ConfigurationBuilder<T> ifSystemPropertyExists(String name,
                                                          Consumer<ConfigurationBuilder<T>> configuration,
                                                          Consumer<ConfigurationBuilder<T>> elseConfiguration) {
        String value = System.getProperty(name);
        if (value != null) {
            if (configuration != null) {
                configuration.accept(this);
            }
        } else {
            if (elseConfiguration != null) {
                elseConfiguration.accept(this);
            }
        }

        return this;
    }

    private String getSystemProperty(String name, boolean required) {
        String value = System.getProperty(name);
        if (value == null && required) {
            throw new IllegalStateException("Expected " + name + " to be found in the system properties list!");
        }

        return value;
    }

    public Value getRequiredSystemProperty(String name) {
        return new StringValue(getSystemProperty(name, true));
    }

    private <P> ConfigurationBuilder<T> fromSystemProperty(Consumer<T> propertyInvoker, String name,
                                                           Func1<String, P> derivation, boolean required) {
        String value = getSystemProperty(name, required);
        bean.set(propertyInvoker, derivation != null ? derivation.call(value) : value);

        return this;
    }

    public <P> ConfigurationBuilder<T> fromRequiredSystemProperty(Consumer<T> propertyInvoker, String name,
                                                                  Func1<String, P> derivation) {
        return fromSystemProperty(propertyInvoker, name, derivation, true);
    }

    private <P> ConfigurationBuilder<T> fromSystemProperties(Consumer<T> propertyInvoker,
                                                             String name1, String name2,
                                                             Func2<String, String, P> derivation,
                                                             boolean required) {
        String value1 = getSystemProperty(name1, required);
        String value2 = getSystemProperty(name2, required);
        bean.set(propertyInvoker, derivation.call(value1, value2));

        return this;
    }

    public <P> ConfigurationBuilder<T> fromRequiredSystemProperties(Consumer<T> propertyInvoker,
                                                                    String name1, String name2,
                                                                    Func2<String, String, P> derivation) {
        return fromSystemProperties(propertyInvoker, name1, name2, derivation, true);
    }

    private <P> ConfigurationBuilder<T> fromSystemProperties(Consumer<T> propertyInvoker,
                                                             String name1, String name2, String name3,
                                                             Func3<String, String, String, P> derivation,
                                                             boolean required) {
        String value1 = getSystemProperty(name1, required);
        String value2 = getSystemProperty(name2, required);
        String value3 = getSystemProperty(name3, required);
        bean.set(propertyInvoker, derivation.call(value1, value2, value3));

        return this;
    }

    public <P> ConfigurationBuilder<T> fromRequiredSystemProperties(Consumer<T> propertyInvoker,
                                                                    String name1, String name2, String name3,
                                                                    Func3<String, String, String, P> derivation) {
        return fromSystemProperties(propertyInvoker, name1, name2, name3, derivation, true);
    }

    private <P> ConfigurationBuilder<T> fromSystemProperties(Consumer<T> propertyInvoker,
                                                             String name1, String name2, String name3, String name4,
                                                             Func4<String, String, String, String, P> derivation,
                                                             boolean required) {
        String value1 = getSystemProperty(name1, required);
        String value2 = getSystemProperty(name2, required);
        String value3 = getSystemProperty(name3, required);
        String value4 = getSystemProperty(name4, required);
        bean.set(propertyInvoker, derivation.call(value1, value2, value3, value4));

        return this;
    }

    public <P> ConfigurationBuilder<T> fromRequiredSystemProperties(Consumer<T> propertyInvoker,
                                                                    String name1, String name2, String name3, String name4,
                                                                    Func4<String, String, String, String, P> derivation) {
        return fromSystemProperties(propertyInvoker, name1, name2, name3, name4, derivation, true);
    }

    private <P> ConfigurationBuilder<T> fromSystemProperties(Consumer<T> propertyInvoker,
                                                             String name1, String name2, String name3, String name4, String name5,
                                                             Func5<String, String, String, String, String, P> derivation,
                                                             boolean required) {
        String value1 = getSystemProperty(name1, required);
        String value2 = getSystemProperty(name2, required);
        String value3 = getSystemProperty(name3, required);
        String value4 = getSystemProperty(name4, required);
        String value5 = getSystemProperty(name5, required);
        bean.set(propertyInvoker, derivation.call(value1, value2, value3, value4, value5));

        return this;
    }

    public <P> ConfigurationBuilder<T> fromRequiredSystemProperties(Consumer<T> propertyInvoker,
                                                                    String name1, String name2, String name3, String name4, String name5,
                                                                    Func5<String, String, String, String, String, P> derivation) {
        return fromSystemProperties(propertyInvoker, name1, name2, name3, name4, name5, derivation, true);
    }

    private <V> ConfigurationBuilder<T> fromSystemProperty(Consumer<T> propertyInvoker, String name,
                                                           V defaultValue, boolean required) {
        String value = getSystemProperty(name, required);
        if (value != null) {
            bean.set(propertyInvoker, new StringValue(value));
        } else {
            bean.set(propertyInvoker, defaultValue);
        }

        return this;
    }

    public <V> ConfigurationBuilder<T> fromOptionalSystemProperty(Consumer<T> propertyInvoker, String name,
                                                                  V defaultValue) {
        return fromSystemProperty(propertyInvoker, name, defaultValue, false);
    }

    public ConfigurationBuilder<T> fromRequiredSystemProperty(Consumer<T> propertyInvoker, String name) {
        return fromRequiredSystemProperty(propertyInvoker, name, null);
    }

    public ConfigurationBuilder<T> fromOptionalSystemProperty(Consumer<T> propertyInvoker, String name) {
        return fromSystemProperty(propertyInvoker, name, null, false);
    }

    public ConfigurationBuilder<T> fromLocalAddress(Consumer<T> propertyInvoker) {
        try {
            bean.set(propertyInvoker, InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            logger.debug("Error retrieving local host name, using localhost", e);
            bean.set(propertyInvoker, "localhost");
        }

        return this;
    }

    public <P> ConfigurationBuilder<T> fromLocalAddress(Consumer<T> propertyInvoker, Func1<String, P> derivation) {
        String localAddress;
        try {
            localAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.debug("Error retrieving local host name, using localhost", e);
            localAddress = "localhost";
        }

        bean.set(propertyInvoker, derivation.call(localAddress));
        return this;
    }
}
