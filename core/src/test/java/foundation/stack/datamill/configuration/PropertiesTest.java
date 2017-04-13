package foundation.stack.datamill.configuration;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class PropertiesTest {
    @Rule public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Test
    public void environmentVariables() {
        environmentVariables.set("test", "value");
        environmentVariables.set("prefix_transformed", "value2");

        assertEquals("value", Properties.fromEnvironment().get("test").get());
        assertFalse(Properties.fromEnvironment().get("test2").isPresent());

        assertFalse(Properties.fromEnvironment().get("transformed").isPresent());
        assertEquals("value2", Properties.fromEnvironment(v -> "prefix_" + v).get("transformed").get());

        assertEquals("value", Properties.fromEnvironment().getRequired("test").asString());
    }

    @Test
    public void systemProperties() {
        System.setProperty("test", "value");
        System.setProperty("prefix_transformed", "value2");

        assertEquals("value", Properties.fromSystem().get("test").get());
        assertFalse(Properties.fromSystem().get("test2").isPresent());

        assertFalse(Properties.fromSystem().get("transformed").isPresent());
        assertEquals("value2", Properties.fromSystem(v -> "prefix_" + v).get("transformed").get());

        assertEquals("value", Properties.fromSystem().getRequired("test").asString());
    }

    @Test
    public void constantClasses() {
        assertEquals("value", Properties.fromConstantsClass(ConstantsClass.class).get(ConstantsClass.property).get());
        assertFalse(Properties.fromConstantsClass(ConstantsClass.class).get("property2").isPresent());
        assertFalse(Properties.fromConstantsClass(ConstantsClass.class).get("config/instance").isPresent());

        assertEquals("publicValue", Properties.fromConstantsClass(ConstantsClass.class).get(ConstantsClass.publicProperty).get());
        assertEquals("privateValue", Properties.fromConstantsClass(ConstantsClass.class).get(ConstantsClass.privateProperty).get());
        assertEquals("nonFinalValue", Properties.fromConstantsClass(ConstantsClass.class).get(ConstantsClass.nonFinalProperty).get());
        assertEquals("1", Properties.fromConstantsClass(ConstantsClass.class).get(ConstantsClass.integerProperty).get());
        assertEquals("true", Properties.fromConstantsClass(ConstantsClass.class).get(ConstantsClass.booleanProperty).get());

        assertEquals("value", Properties.fromConstantsClass(ConstantsClass.class).getRequired("config/property").asString());

        assertEquals("ifacePublic", Properties.fromConstantsClass(ConstantsClass.class).orConstantsClass(ConstantsInterface.class).get(ConstantsInterface.IFACE_PUBLIC_TEST).get());
        assertEquals("2", Properties.fromConstantsClass(ConstantsClass.class).orConstantsClass(ConstantsInterface.class).get(ConstantsInterface.IFACE_INTEGER).get());
        assertEquals("true", Properties.fromConstantsClass(ConstantsClass.class).orConstantsClass(ConstantsInterface.class).get(ConstantsInterface.IFACE_BOOLEAN).get());
        assertEquals("true", Properties.fromConstantsClass(ConstantsClass.class).orConstantsClass(ConstantsInterface.class).get(ConstantsClass.booleanProperty).get());
    }

    @Test
    public void files() {
        assertEquals("value", Properties.fromFile("test.properties").get("test").get());
        assertFalse(Properties.fromFile("test.properties").get("test2").isPresent());

        assertEquals("value3", Properties.fromFile("test.properties").orFile("test2.properties").get("test3").get());
        assertFalse(Properties.fromFile("test.properties").orFile("test2.properties").get("test4").isPresent());

        assertEquals("value", Properties.fromFile("test.properties").getRequired("test").asString());
        assertEquals("value3", Properties.fromFile("test.properties").orFile("test2.properties").getRequired("test3").asString());
    }

    @Test
    public void defaults() {
        System.setProperty("test", "value");

        assertEquals("value", Properties.fromSystem().orDefaults(d -> d.put("test", "value2")).get("test").get());
        assertEquals("value2", Properties.fromSystem().orDefaults(d -> d.put("test2", "value2")).get("test2").get());

        assertEquals("value", Properties.fromSystem().orDefaults(d -> d.put("test", "value2")).getRequired("test").asString());
        assertEquals("value2", Properties.fromSystem().orDefaults(d -> d.put("test2", "value2")).getRequired("test2").asString());
    }


    @Test
    public void aliases() {
        System.setProperty("test", "value");

        assertEquals("value", Properties.fromSystem().get("test").get());
        assertEquals("value", Properties.fromSystem().alias("test2", "test").get("test2").get());
    }

    @Test
    public void chains() {
        environmentVariables.set("test4", "value4");
        System.setProperty("test5", "value5");

        PropertySource chain = Properties.fromFile("test.properties").orFile("test2.properties").orSystem()
                .orEnvironment().orDefaults(d -> d.put("test6", "value6"));

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
    public void missingRequiredProperties() throws Exception {
        environmentVariables.set("test4", "value4");
        System.setProperty("test5", "value5");

        try {
            Properties.fromEnvironment().getRequired("test3");
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            Properties.fromSystem().getRequired("test3");
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            Properties.fromFile("test.properties").getRequired("test3");
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            Properties.fromFile("test.properties").orFile("test2.properties").getRequired("test4");
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            Properties.fromFile("test.properties").orFile("test2.properties").orSystem().orEnvironment()
                    .orDefaults(d -> d.put("test6", "value6")).getRequired("test2");
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    private static class ConstantsClass {
        @StringValue("value") static final String property = "config/property";
        @StringValue("publicValue") public static final String publicProperty = "config/public";
        @StringValue("privateValue") private static final String privateProperty = "config/private";
        @StringValue("nonFinalValue") static String nonFinalProperty = "config/nonFinal";
        @IntegerValue(1) static String integerProperty = "config/integer";
        @BooleanValue(true) static String booleanProperty = "config/boolean";
        @StringValue("instanceValue") String instanceProperty = "config/instance";
    }

    private interface ConstantsInterface {
        @StringValue("ifacePublic") String IFACE_PUBLIC_TEST = "iface/public";
        @IntegerValue(2) String IFACE_INTEGER = "iface/integer";
        @BooleanValue(true) String IFACE_BOOLEAN = "iface/boolean";
    }
}
