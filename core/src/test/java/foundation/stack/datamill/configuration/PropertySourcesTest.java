package foundation.stack.datamill.configuration;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class PropertySourcesTest {
    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Test
    public void environmentVariables() {
        environmentVariables.set("test", "value");
        environmentVariables.set("prefix_transformed", "value2");

        assertEquals("value", PropertySources.fromEnvironment().get("test").get());
        assertFalse(PropertySources.fromEnvironment().get("test2").isPresent());

        assertFalse(PropertySources.fromEnvironment().get("transformed").isPresent());
        assertEquals("value2", PropertySources.fromEnvironment(v -> "prefix_" + v).get("transformed").get());

        assertEquals("value", PropertySources.fromEnvironment().getRequired("test").asString());
    }

    @Test
    public void systemProperties() {
        System.setProperty("test", "value");
        System.setProperty("prefix_transformed", "value2");

        assertEquals("value", PropertySources.fromSystem().get("test").get());
        assertFalse(PropertySources.fromSystem().get("test2").isPresent());

        assertFalse(PropertySources.fromSystem().get("transformed").isPresent());
        assertEquals("value2", PropertySources.fromSystem(v -> "prefix_" + v).get("transformed").get());

        assertEquals("value", PropertySources.fromSystem().getRequired("test").asString());
    }

    @Test
    public void constantClasses() {
        assertEquals("value", PropertySources.fromConstantsClass(ConstantsClass.class).get(ConstantsClass.property).get());
        assertFalse(PropertySources.fromConstantsClass(ConstantsClass.class).get("property2").isPresent());
        assertFalse(PropertySources.fromConstantsClass(ConstantsClass.class).get("config/instance").isPresent());

        assertEquals("publicValue", PropertySources.fromConstantsClass(ConstantsClass.class).get(ConstantsClass.publicProperty).get());
        assertEquals("privateValue", PropertySources.fromConstantsClass(ConstantsClass.class).get(ConstantsClass.privateProperty).get());
        assertEquals("nonFinalValue", PropertySources.fromConstantsClass(ConstantsClass.class).get(ConstantsClass.nonFinalProperty).get());
        assertEquals("1", PropertySources.fromConstantsClass(ConstantsClass.class).get(ConstantsClass.integerProperty).get());
        assertEquals("true", PropertySources.fromConstantsClass(ConstantsClass.class).get(ConstantsClass.booleanProperty).get());

        assertEquals("value", PropertySources.fromConstantsClass(ConstantsClass.class).getRequired("config/property").asString());

        assertEquals("ifacePublic", PropertySources.fromConstantsClass(ConstantsClass.class).orConstantsClass(ConstantsInterface.class).get(ConstantsInterface.IFACE_PUBLIC_TEST).get());
        assertEquals("2", PropertySources.fromConstantsClass(ConstantsClass.class).orConstantsClass(ConstantsInterface.class).get(ConstantsInterface.IFACE_INTEGER).get());
        assertEquals("true", PropertySources.fromConstantsClass(ConstantsClass.class).orConstantsClass(ConstantsInterface.class).get(ConstantsInterface.IFACE_BOOLEAN).get());
        assertEquals("true", PropertySources.fromConstantsClass(ConstantsClass.class).orConstantsClass(ConstantsInterface.class).get(ConstantsClass.booleanProperty).get());
    }

    @Test
    public void computation() {
        assertEquals("computed", PropertySources.fromComputed(name -> "computed").orFile("test.properties").get("test").get());
        assertEquals("value", PropertySources.fromComputed(name -> "test2".equals(name) ? "computed" : null)
                .orFile("test.properties").get("test").get());
    }

    @Test
    public void files() throws IOException {
        assertFalse(PropertySources.fromFile("nonexistent.properties").get("test2").isPresent());

        File externalProperties = File.createTempFile("test", ".properties");
        try {
            Files.write("test4=value4\n", externalProperties, Charsets.UTF_8);

            assertEquals("value", PropertySources.fromFile("test.properties").get("test").get());
            assertFalse(PropertySources.fromFile("test.properties").get("test2").isPresent());

            assertEquals("value3", PropertySources.fromFile("test.properties").orFile("test2.properties").get("test3").get());
            assertFalse(PropertySources.fromFile("test.properties").orFile("test2.properties").get("test4").isPresent());

            assertEquals("value", PropertySources.fromFile("test.properties").getRequired("test").asString());
            assertEquals("value3", PropertySources.fromFile("test.properties").orFile("test2.properties").getRequired("test3").asString());

            assertEquals("value4", PropertySources.fromFile(externalProperties.getPath()).orFile("test2.properties").getRequired("test4").asString());
        } finally {
            externalProperties.delete();
        }
    }

    @Test
    public void immediates() {
        System.setProperty("test", "value");

        assertEquals("value", PropertySources.fromSystem().orImmediate(d -> d.put("test", "value2")).get("test").get());
        assertEquals("value2", PropertySources.fromSystem().orImmediate(d -> d.put("test2", "value2")).get("test2").get());

        assertEquals("value", PropertySources.fromSystem().orImmediate(d -> d.put("test", "value2")).getRequired("test").asString());
        assertEquals("value2", PropertySources.fromSystem().orImmediate(d -> d.put("test2", "value2")).getRequired("test2").asString());

        assertEquals("value2", PropertySources.fromImmediate(d -> d.put("test2", "value2")).getRequired("test2").asString());

        assertEquals("value-value2", PropertySources.fromImmediate(d -> d.put("test", "{0}-{1}","value", "value2")).getRequired("test").asString());
    }

    @Test
    public void chains() {
        environmentVariables.set("test4", "value4");
        System.setProperty("test5", "value5");

        PropertySource chain = PropertySources
                .fromFile("test.properties")
                .orFile("nonexistent.proeprties")
                .orFile("test2.properties").orSystem().orEnvironment()
                .orImmediate(p -> p.put("test6", "value6"));

        assertEquals("value", chain.get("test").get());
        assertEquals("value3", chain.get("test3").get());
        assertEquals("value4", chain.get("test4").get());
        assertEquals("value5", chain.get("test5").get());
        assertEquals("value6", chain.get("test6").get());

        assertEquals("value", chain.getRequired("test").asString());
        assertEquals("value3", chain.getRequired("test3").asString());
        assertEquals("value4", chain.getRequired("test4").asString());
        assertEquals("value5", chain.getRequired("test5").asString());
        assertEquals("value6", chain.getRequired("test6").asString());
    }

    @Test
    public void conveniences() {
        assertEquals("value", PropertySources.fromImmediate(s -> s.put("name", "value"))
            .with(s -> s.getRequired("name").asString()));
    }

    @Test
    public void delegating() {
        DelegatingPropertySource delegatingSource = new DelegatingPropertySource();
        assertEquals("value2", PropertySources.from(delegatingSource)
                .orImmediate(d -> d.put("test2", "value2")).getRequired("test2").asString());

        delegatingSource.setDelegate(PropertySources.fromComputed(name -> "value"));
        assertEquals("value", PropertySources.from(delegatingSource)
                .orImmediate(d -> d.put("test2", "value2")).getRequired("test2").asString());
    }

    @Test
    public void transformers() {
        environmentVariables.set("PROPERTY_NAME", "value4");
        System.setProperty("PROPERTY2_NAME", "value5");

        PropertySource base = PropertySources
                .fromFile("test.properties")
                .orFile("test2.properties")
                .orSystem()
                .orEnvironment()
                .orImmediate(d -> d.put("test6", "value6"));

        PropertySource leaf = PropertySources.from(base).or(base, PropertyNameTransformers.LEAF);
        PropertySource upper = PropertySources.from(base).or(base,
                PropertyNameTransformers.compose(
                        PropertyNameTransformers.LEAF,
                        PropertyNameTransformers.LOWER_CAMEL_TO_UPPER_UNDERSCORE));

        assertEquals("value", leaf.get("category/test").get());
        assertEquals("value3", leaf.get("category/test3").get());
        assertEquals("value4", leaf.get("category/PROPERTY_NAME").get());
        assertEquals("value5", leaf.get("category/PROPERTY2_NAME").get());
        assertEquals("value6", leaf.get("category/test6").get());

        assertEquals("value4", upper.getRequired("category/subcategory/propertyName").asString());
        assertEquals("value5", upper.getRequired("category/subcategory/property2Name").asString());
    }

    @Test
    public void missingRequiredProperties() throws Exception {
        environmentVariables.set("test4", "value4");
        System.setProperty("test5", "value5");

        try {
            PropertySources.fromEnvironment().getRequired("test3");
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            PropertySources.fromSystem().getRequired("test3");
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            PropertySources.fromFile("test.properties").getRequired("test3");
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            PropertySources.fromFile("test.properties").orFile("test2.properties").getRequired("test4");
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            PropertySources.fromFile("test.properties").orFile("test2.properties").orSystem().orEnvironment()
                    .orImmediate(d -> d.put("test6", "value6")).getRequired("test2");
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    private static class ConstantsClass {
        @Value("value")
        static final String property = "config/property";
        @Value("publicValue")
        public static final String publicProperty = "config/public";
        @Value("privateValue")
        private static final String privateProperty = "config/private";
        @Value("nonFinalValue")
        static String nonFinalProperty = "config/nonFinal";
        @Value("1")
        static String integerProperty = "config/integer";
        @Value("true")
        static String booleanProperty = "config/boolean";
        @Value("instanceValue")
        String instanceProperty = "config/instance";
    }

    private interface ConstantsInterface {
        @Value("ifacePublic")
        String IFACE_PUBLIC_TEST = "iface/public";
        @Value("2")
        String IFACE_INTEGER = "iface/integer";
        @Value("true")
        String IFACE_BOOLEAN = "iface/boolean";
    }
}
