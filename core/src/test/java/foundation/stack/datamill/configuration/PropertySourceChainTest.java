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
public class PropertySourceChainTest {
    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Test
    public void environmentVariables() {
        environmentVariables.set("test", "value");
        environmentVariables.set("prefix_transformed", "value2");

        assertEquals("value", PropertySourceChain.ofEnvironment().get("test").get());
        assertFalse(PropertySourceChain.ofEnvironment().get("test2").isPresent());

        assertFalse(PropertySourceChain.ofEnvironment().get("transformed").isPresent());
        assertEquals("value2", PropertySourceChain.ofEnvironment(v -> "prefix_" + v).get("transformed").get());

        assertEquals("value", PropertySourceChain.ofEnvironment().getRequired("test").asString());
    }

    @Test
    public void systemProperties() {
        System.setProperty("test", "value");
        System.setProperty("prefix_transformed", "value2");

        assertEquals("value", PropertySourceChain.ofSystem().get("test").get());
        assertFalse(PropertySourceChain.ofSystem().get("test2").isPresent());

        assertFalse(PropertySourceChain.ofSystem().get("transformed").isPresent());
        assertEquals("value2", PropertySourceChain.ofSystem(v -> "prefix_" + v).get("transformed").get());

        assertEquals("value", PropertySourceChain.ofSystem().getRequired("test").asString());
    }

    @Test
    public void constantClasses() {
        assertEquals("value", PropertySourceChain.ofConstantsClass(ConstantsClass.class).get(ConstantsClass.property).get());
        assertFalse(PropertySourceChain.ofConstantsClass(ConstantsClass.class).get("property2").isPresent());
        assertFalse(PropertySourceChain.ofConstantsClass(ConstantsClass.class).get("config/instance").isPresent());

        assertEquals("publicValue", PropertySourceChain.ofConstantsClass(ConstantsClass.class).get(ConstantsClass.publicProperty).get());
        assertEquals("privateValue", PropertySourceChain.ofConstantsClass(ConstantsClass.class).get(ConstantsClass.privateProperty).get());
        assertEquals("nonFinalValue", PropertySourceChain.ofConstantsClass(ConstantsClass.class).get(ConstantsClass.nonFinalProperty).get());
        assertEquals("1", PropertySourceChain.ofConstantsClass(ConstantsClass.class).get(ConstantsClass.integerProperty).get());
        assertEquals("true", PropertySourceChain.ofConstantsClass(ConstantsClass.class).get(ConstantsClass.booleanProperty).get());

        assertEquals("value", PropertySourceChain.ofConstantsClass(ConstantsClass.class).getRequired("config/property").asString());

        assertEquals("ifacePublic", PropertySourceChain.ofConstantsClass(ConstantsClass.class).orConstantsClass(ConstantsInterface.class).get(ConstantsInterface.IFACE_PUBLIC_TEST).get());
        assertEquals("2", PropertySourceChain.ofConstantsClass(ConstantsClass.class).orConstantsClass(ConstantsInterface.class).get(ConstantsInterface.IFACE_INTEGER).get());
        assertEquals("true", PropertySourceChain.ofConstantsClass(ConstantsClass.class).orConstantsClass(ConstantsInterface.class).get(ConstantsInterface.IFACE_BOOLEAN).get());
        assertEquals("true", PropertySourceChain.ofConstantsClass(ConstantsClass.class).orConstantsClass(ConstantsInterface.class).get(ConstantsClass.booleanProperty).get());
    }

    @Test
    public void computation() {
        assertEquals("computed", PropertySourceChain.ofComputed(name -> "computed").orFile("test.properties").get("test").get());
        assertEquals("value", PropertySourceChain.ofComputed(name -> "test2".equals(name) ? "computed" : null)
                .orFile("test.properties").get("test").get());
    }

    @Test
    public void files() throws IOException {
        File externalProperties = File.createTempFile("test", ".properties");
        try {
            Files.write("test4=value4\n", externalProperties, Charsets.UTF_8);

            assertEquals("value", PropertySourceChain.ofFile("test.properties").get("test").get());
            assertFalse(PropertySourceChain.ofFile("test.properties").get("test2").isPresent());

            assertEquals("value3", PropertySourceChain.ofFile("test.properties").orFile("test2.properties").get("test3").get());
            assertFalse(PropertySourceChain.ofFile("test.properties").orFile("test2.properties").get("test4").isPresent());

            assertEquals("value", PropertySourceChain.ofFile("test.properties").getRequired("test").asString());
            assertEquals("value3", PropertySourceChain.ofFile("test.properties").orFile("test2.properties").getRequired("test3").asString());

            assertEquals("value4", PropertySourceChain.ofFile(externalProperties.getPath()).orFile("test2.properties").getRequired("test4").asString());
        } finally {
            externalProperties.delete();
        }
    }

    @Test
    public void immediates() {
        System.setProperty("test", "value");

        assertEquals("value", PropertySourceChain.ofSystem().orImmediate(d -> d.put("test", "value2")).get("test").get());
        assertEquals("value2", PropertySourceChain.ofSystem().orImmediate(d -> d.put("test2", "value2")).get("test2").get());

        assertEquals("value", PropertySourceChain.ofSystem().orImmediate(d -> d.put("test", "value2")).getRequired("test").asString());
        assertEquals("value2", PropertySourceChain.ofSystem().orImmediate(d -> d.put("test2", "value2")).getRequired("test2").asString());

        assertEquals("value2", PropertySourceChain.ofImmediate(d -> d.put("test2", "value2")).getRequired("test2").asString());
    }

    @Test
    public void chains() {
        environmentVariables.set("test4", "value4");
        System.setProperty("test5", "value5");

        PropertySource chain = PropertySourceChain
                .ofFile("test.properties")
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
    public void delegating() {
        DelegatingPropertySource delegatingSource = new DelegatingPropertySource();
        assertEquals("value2", PropertySourceChain.ofSource(delegatingSource)
                .orImmediate(d -> d.put("test2", "value2")).getRequired("test2").asString());

        delegatingSource.setDelegate(PropertySourceChain.ofComputed(name -> "value"));
        assertEquals("value", PropertySourceChain.ofSource(delegatingSource)
                .orImmediate(d -> d.put("test2", "value2")).getRequired("test2").asString());
    }

    @Test
    public void transformers() {
        environmentVariables.set("PROPERTY_NAME", "value4");
        System.setProperty("PROPERTY2_NAME", "value5");

        PropertySource base = PropertySourceChain
                .ofFile("test.properties")
                .orFile("test2.properties")
                .orSystem()
                .orEnvironment()
                .orImmediate(d -> d.put("test6", "value6"));

        PropertySource leaf = PropertySourceChain.ofSource(base).orSource(base, PropertySourceChain.Transformers.LEAF);
        PropertySource upper = PropertySourceChain.ofSource(base).orSource(base,
                PropertySourceChain.Transformers.compose(
                        PropertySourceChain.Transformers.LEAF,
                        PropertySourceChain.Transformers.LOWER_CAMEL_TO_UPPER_UNDERSCORE));

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
            PropertySourceChain.ofEnvironment().getRequired("test3");
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            PropertySourceChain.ofSystem().getRequired("test3");
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            PropertySourceChain.ofFile("test.properties").getRequired("test3");
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            PropertySourceChain.ofFile("test.properties").orFile("test2.properties").getRequired("test4");
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            PropertySourceChain.ofFile("test.properties").orFile("test2.properties").orSystem().orEnvironment()
                    .orImmediate(d -> d.put("test6", "value6")).getRequired("test2");
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    private static class ConstantsClass {
        @StringValue("value")
        static final String property = "config/property";
        @StringValue("publicValue")
        public static final String publicProperty = "config/public";
        @StringValue("privateValue")
        private static final String privateProperty = "config/private";
        @StringValue("nonFinalValue")
        static String nonFinalProperty = "config/nonFinal";
        @IntegerValue(1)
        static String integerProperty = "config/integer";
        @BooleanValue(true)
        static String booleanProperty = "config/boolean";
        @StringValue("instanceValue")
        String instanceProperty = "config/instance";
    }

    private interface ConstantsInterface {
        @StringValue("ifacePublic")
        String IFACE_PUBLIC_TEST = "iface/public";
        @IntegerValue(2)
        String IFACE_INTEGER = "iface/integer";
        @BooleanValue(true)
        String IFACE_BOOLEAN = "iface/boolean";
    }
}
