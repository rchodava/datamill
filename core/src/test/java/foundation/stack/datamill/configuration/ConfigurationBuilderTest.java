package foundation.stack.datamill.configuration;

import foundation.stack.datamill.reflection.OutlineBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ConfigurationBuilderTest {
    private static class Configuration {
        private String property;

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }
    }

    @Rule public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Test
    public void buildFromMiscellaneous() throws Exception {
        Configuration configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .configure(c -> c.setProperty("set"))
                .get();
        assertEquals("set", configuration.getProperty());

        configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .configure((b, c) -> c.setProperty("builder_set"))
                .get();
        assertEquals("builder_set", configuration.getProperty());

        configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .fromLocalAddress(c -> c.getProperty())
                .get();
        assertEquals(InetAddress.getLocalHost().getHostAddress(), configuration.getProperty());

        configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .fromLocalAddress(c -> c.getProperty(), a -> a + "_derived")
                .get();
        assertEquals(InetAddress.getLocalHost().getHostAddress() + "_derived", configuration.getProperty());
    }

    @Test
    public void buildFromEnvironmentVariables() throws Exception {
        environmentVariables.set("test", "value");
        environmentVariables.set("test2", "_value2");
        environmentVariables.set("test3", "_value3");
        environmentVariables.set("test4", "_value4");
        environmentVariables.set("test5", "_value5");

        Configuration configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .fromRequiredSystemProperty(c -> c.getProperty(), "test")
                .get();
        assertEquals("value", configuration.getProperty());

        configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .fromOptionalSystemProperty(c -> c.getProperty(), "test_unknown", "default")
                .get();
        assertEquals("default", configuration.getProperty());

        configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .fromRequiredSystemProperty(c -> c.getProperty(), "test", v -> v + "_derived")
                .get();
        assertEquals("value_derived", configuration.getProperty());

        configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .fromRequiredSystemProperties(c -> c.getProperty(), "test", "test2", (v1, v2) -> v1 + v2 + "_derived")
                .get();
        assertEquals("value_value2_derived", configuration.getProperty());

        configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .fromRequiredSystemProperties(c -> c.getProperty(), "test", "test2", "test3",
                        (v1, v2, v3) -> v1 + v2 + v3 + "_derived")
                .get();
        assertEquals("value_value2_value3_derived", configuration.getProperty());

        configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .fromRequiredSystemProperties(c -> c.getProperty(), "test", "test2", "test3", "test4",
                        (v1, v2, v3, v4) -> v1 + v2 + v3 + v4 + "_derived")
                .get();
        assertEquals("value_value2_value3_value4_derived", configuration.getProperty());

        configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .fromRequiredSystemProperties(c -> c.getProperty(), "test", "test2", "test3", "test4", "test5",
                        (v1, v2, v3, v4, v5) -> v1 + v2 + v3 + v4 + v5 + "_derived")
                .get();
        assertEquals("value_value2_value3_value4_value5_derived", configuration.getProperty());

        configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .ifSystemPropertyExists("test", b -> b.configure(c -> c.setProperty("exists")))
                .get();
        assertEquals("exists", configuration.getProperty());

        configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .ifSystemPropertyExists("test_non_existent",
                        b -> b.configure(c -> c.setProperty("exists")),
                        b -> b.configure(c -> c.setProperty("non_existent")))
                .get();
        assertEquals("non_existent", configuration.getProperty());

        configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .configure((b, c) -> c.setProperty(b.getRequiredSystemProperty("test").asString()))
                .get();
        assertEquals("value", configuration.getProperty());
    }

    @Test
    public void printProperties() {
        System.setProperty("test", "value");

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream outStream = System.out;
        System.setOut(new PrintStream(output));
        new ConfigurationBuilder(new OutlineBuilder().wrap(new Configuration())).printSystemProperties();
        String properties = new String(output.toByteArray());

        assertTrue(properties.contains("test"));
        assertTrue(properties.contains("value"));

        System.setOut(outStream);
    }

    @Test
    public void buildFromSystemProperties() throws Exception {
        System.setProperty("test", "value");
        System.setProperty("test2", "_value2");
        System.setProperty("test3", "_value3");
        System.setProperty("test4", "_value4");
        System.setProperty("test5", "_value5");

        Configuration configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .fromRequiredSystemProperty(c -> c.getProperty(), "test")
                .get();
        assertEquals("value", configuration.getProperty());

        configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .fromOptionalSystemProperty(c -> c.getProperty(), "test_unknown", "default")
                .get();
        assertEquals("default", configuration.getProperty());

        configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .fromRequiredSystemProperty(c -> c.getProperty(), "test", v -> v + "_derived")
                .get();
        assertEquals("value_derived", configuration.getProperty());

        configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .fromRequiredSystemProperties(c -> c.getProperty(), "test", "test2", (v1, v2) -> v1 + v2 + "_derived")
                .get();
        assertEquals("value_value2_derived", configuration.getProperty());

        configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .fromRequiredSystemProperties(c -> c.getProperty(), "test", "test2", "test3",
                        (v1, v2, v3) -> v1 + v2 + v3 + "_derived")
                .get();
        assertEquals("value_value2_value3_derived", configuration.getProperty());

        configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .fromRequiredSystemProperties(c -> c.getProperty(), "test", "test2", "test3", "test4",
                        (v1, v2, v3, v4) -> v1 + v2 + v3 + v4 + "_derived")
                .get();
        assertEquals("value_value2_value3_value4_derived", configuration.getProperty());

        configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .fromRequiredSystemProperties(c -> c.getProperty(), "test", "test2", "test3", "test4", "test5",
                        (v1, v2, v3, v4, v5) -> v1 + v2 + v3 + v4 + v5 + "_derived")
                .get();
        assertEquals("value_value2_value3_value4_value5_derived", configuration.getProperty());

        configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .ifSystemPropertyExists("test", b -> b.configure(c -> c.setProperty("exists")))
                .get();
        assertEquals("exists", configuration.getProperty());

        configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .ifSystemPropertyExists("test_non_existent",
                        b -> b.configure(c -> c.setProperty("exists")),
                        b -> b.configure(c -> c.setProperty("non_existent")))
                .get();
        assertEquals("non_existent", configuration.getProperty());

        configuration = new ConfigurationBuilder<>(new OutlineBuilder().wrap(new Configuration()))
                .configure((b, c) -> c.setProperty(b.getRequiredSystemProperty("test").asString()))
                .get();
        assertEquals("value", configuration.getProperty());
    }
}
