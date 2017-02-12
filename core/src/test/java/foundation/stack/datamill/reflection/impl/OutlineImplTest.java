package foundation.stack.datamill.reflection.impl;

import foundation.stack.datamill.json.JsonObject;
import foundation.stack.datamill.reflection.Outline;
import foundation.stack.datamill.reflection.OutlineBuilder;
import foundation.stack.datamill.values.StringValue;
import org.junit.Test;

import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class OutlineImplTest {
    private int actualBeanMethodInvocations = 0;

    public class TestBeanClass {
        private String readWriteProperty;
        public String getReadWriteProperty() {
            actualBeanMethodInvocations++;
            return readWriteProperty;
        }

        public boolean isBooleanProperty() {
            actualBeanMethodInvocations++;
            return false;
        }

        public String getReadOnlyProperty() {
            actualBeanMethodInvocations++;
            return "";
        }

        public void setReadWriteProperty(String value) {
            actualBeanMethodInvocations++;
            this.readWriteProperty = value;
        }

        public void nonPropertyMethod() {

        }
    }

    public class TestBeanClassWithVariousProperties {
        private boolean booleanProperty;
        private Boolean booleanWrapperProperty;
        private byte byteProperty;
        private char charProperty;
        private short shortProperty;
        private Short shortWrapperProperty;
        private int intProperty;
        private Integer intWrapperProperty;
        private long longProperty;
        private Long longWrapperProperty;
        private float floatProperty;
        private Float floatWrapperProperty;
        private double doubleProperty;
        private Double doubleWrapperProperty;
        private String stringProperty;

        public boolean isBooleanProperty() {
            return booleanProperty;
        }

        public Boolean getBooleanWrapperProperty() {
            return booleanWrapperProperty;
        }

        public byte getByteProperty() {
            return byteProperty;
        }

        public char getCharProperty() {
            return charProperty;
        }

        public short getShortProperty() {
            return shortProperty;
        }

        public Short getShortWrapperProperty() {
            return shortWrapperProperty;
        }

        public int getIntProperty() {
            return intProperty;
        }

        public Integer getIntWrapperProperty() {
            return intWrapperProperty;
        }

        public long getLongProperty() {
            return longProperty;
        }

        public Long getLongWrapperProperty() {
            return longWrapperProperty;
        }

        public float getFloatProperty() {
            return floatProperty;
        }

        public Float getFloatWrapperProperty() {
            return floatWrapperProperty;
        }

        public double getDoubleProperty() {
            return doubleProperty;
        }

        public Double getDoubleWrapperProperty() {
            return doubleWrapperProperty;
        }

        public String getStringProperty() {
            return stringProperty;
        }

        public void setBooleanProperty(boolean booleanProperty) {
            this.booleanProperty = booleanProperty;
        }

        public void setBooleanWrapperProperty(Boolean booleanWrapperProperty) {
            this.booleanWrapperProperty = booleanWrapperProperty;
        }

        public void setByteProperty(byte byteProperty) {
            this.byteProperty = byteProperty;
        }

        public void setCharProperty(char charProperty) {
            this.charProperty = charProperty;
        }

        public void setShortProperty(short shortProperty) {
            this.shortProperty = shortProperty;
        }

        public void setShortWrapperProperty(Short shortWrapperProperty) {
            this.shortWrapperProperty = shortWrapperProperty;
        }

        public void setIntProperty(int intProperty) {
            this.intProperty = intProperty;
        }

        public void setIntWrapperProperty(Integer intWrapperProperty) {
            this.intWrapperProperty = intWrapperProperty;
        }

        public void setLongProperty(long longProperty) {
            this.longProperty = longProperty;
        }

        public void setLongWrapperProperty(Long longWrapperProperty) {
            this.longWrapperProperty = longWrapperProperty;
        }

        public void setFloatProperty(float floatProperty) {
            this.floatProperty = floatProperty;
        }

        public void setFloatWrapperProperty(Float floatWrapperProperty) {
            this.floatWrapperProperty = floatWrapperProperty;
        }

        public void setDoubleProperty(double doubleProperty) {
            this.doubleProperty = doubleProperty;
        }

        public void setDoubleWrapperProperty(Double doubleWrapperProperty) {
            this.doubleWrapperProperty = doubleWrapperProperty;
        }

        public void setStringProperty(String stringProperty) {
            this.stringProperty = stringProperty;
        }
    }

    @Test
    public void camelCasedNames() {
        Outline<TestBeanClass> outline = OutlineBuilder.DEFAULT.defaultSnakeCased().build(TestBeanClass.class);

        assertEquals("readOnlyProperty", outline.member(m -> m.getReadOnlyProperty()).camelCasedName());
        assertEquals("booleanProperty", outline.member(m -> m.isBooleanProperty()).camelCasedName());
        assertEquals("readWriteProperty", outline.member(m -> m.setReadWriteProperty("")).camelCasedName());

        assertEquals("TestBeanClass", outline.camelCasedName());
        assertEquals("TestBeanClasses", outline.camelCasedPluralName());

        assertEquals(0, actualBeanMethodInvocations);
    }

    @Test
    public void methods() {
        Outline<TestBeanClass> outline = OutlineBuilder.DEFAULT.defaultSnakeCased().build(TestBeanClass.class);

        // Test all methods in class are present
        assertEquals(5, outline.methods().stream().mapToInt(m -> {
            switch (m.getName()) {
                case "getReadOnlyProperty":
                    return 1;
                case "isBooleanProperty":
                    return 1;
                case "getReadWriteProperty":
                    return 1;
                case "setReadWriteProperty":
                    return 1;
                case "nonPropertyMethod":
                    return 1;
                default:
                    return 0;
            }
        }).sum());
    }

    @Test
    public void propertyNamesCamelCased() {
        assertThat(OutlineBuilder.DEFAULT.defaultCamelCased().build(TestBeanClass.class).propertyNames(),
                hasItems("readWriteProperty", "readOnlyProperty", "booleanProperty"));

        assertEquals(0, actualBeanMethodInvocations);
    }

    @Test
    public void propertyNamesSnakeCased() {
        assertThat(OutlineBuilder.DEFAULT.defaultSnakeCased().build(TestBeanClass.class).propertyNames(),
                hasItems("read_write_property", "read_only_property", "boolean_property"));

        assertEquals(0, actualBeanMethodInvocations);
    }

    @Test
    public void getPropertiesCamelCasedDoesNotHaveGetClass() {
        assertThat(OutlineBuilder.DEFAULT.defaultCamelCased().build(TestBeanClass.class).properties().stream()
                        .map(p -> p.name()).collect(Collectors.toList()),
                not(hasItems("class")));

        assertEquals(0, actualBeanMethodInvocations);
    }

    @Test
    public void getPropertiesCamelCased() {
        assertThat(OutlineBuilder.DEFAULT.defaultCamelCased().build(TestBeanClass.class).properties().stream()
                        .map(p -> p.name()).collect(Collectors.toList()),
                hasItems("readWriteProperty", "readOnlyProperty", "booleanProperty"));

        assertEquals(0, actualBeanMethodInvocations);
    }

    @Test
    public void getPropertiesSnakeCased() {
        assertThat(OutlineBuilder.DEFAULT.defaultSnakeCased().build(TestBeanClass.class).properties().stream()
                        .map(p -> p.name()).collect(Collectors.toList()),
                hasItems("read_write_property", "read_only_property", "boolean_property"));

        assertEquals(0, actualBeanMethodInvocations);
    }

    @Test
    public void getPropertyCamelCased() {
        Outline<TestBeanClass> outline = OutlineBuilder.DEFAULT.defaultCamelCased().build(TestBeanClass.class);
        assertEquals("readWriteProperty", outline.property(m -> m.getReadWriteProperty()).name());
        assertEquals("booleanProperty", outline.property(m -> m.isBooleanProperty()).name());
        assertEquals("readWriteProperty", outline.property(m -> m.getReadWriteProperty()).name());

        assertEquals(0, actualBeanMethodInvocations);
    }

    @Test
    public void getPropertySnakeCased() {
        Outline<TestBeanClass> outline = OutlineBuilder.DEFAULT.defaultSnakeCased().build(TestBeanClass.class);
        assertEquals("read_write_property", outline.property(m -> m.getReadWriteProperty()).name());
        assertEquals("boolean_property", outline.property(m -> m.isBooleanProperty()).name());
        assertEquals("read_write_property", outline.property(m -> m.getReadWriteProperty()).name());

        assertEquals(0, actualBeanMethodInvocations);
    }

    @Test
    public void snakeCasedNames() {
        Outline<TestBeanClass> outline = OutlineBuilder.DEFAULT.defaultCamelCased().build(TestBeanClass.class);

        assertEquals("read_only_property", outline.member(m -> m.getReadOnlyProperty()).snakeCasedName());
        assertEquals("boolean_property", outline.member(m -> m.isBooleanProperty()).snakeCasedName());
        assertEquals("read_write_property", outline.member(m -> m.setReadWriteProperty("")).snakeCasedName());

        assertEquals("test_bean_class", outline.snakeCasedName());
        assertEquals("test_bean_classes", outline.snakeCasedPluralName());

        assertEquals(0, actualBeanMethodInvocations);
    }

    @Test
    public void wrapAndBeanGet() {
        Outline<TestBeanClass> outline = OutlineBuilder.DEFAULT.defaultCamelCased().build(TestBeanClass.class);

        TestBeanClass instance = new TestBeanClass();
        instance.setReadWriteProperty("value1");

        assertEquals("value1", outline.wrap(instance).get(m -> m.getReadWriteProperty()));
        assertEquals(2, actualBeanMethodInvocations);
    }

    @Test
    public void wrapAndBeanSet() {
        Outline<TestBeanClass> outline = OutlineBuilder.DEFAULT.defaultCamelCased().build(TestBeanClass.class);

        TestBeanClass instance = new TestBeanClass();
        outline.wrap(instance).set(m -> m.getReadWriteProperty(), "value1");

        assertEquals(1, actualBeanMethodInvocations);
        assertEquals("value1", instance.getReadWriteProperty());
    }

    @Test
    public void wrapAndBeanSetCastedValue() {
        Outline<TestBeanClassWithVariousProperties> outline = OutlineBuilder.DEFAULT.defaultCamelCased()
                .build(TestBeanClassWithVariousProperties.class);

        TestBeanClassWithVariousProperties instance = new TestBeanClassWithVariousProperties();
        outline.wrap(instance)
                .set(m -> m.isBooleanProperty(), new StringValue("true"))
                .set(m -> m.getBooleanWrapperProperty(), null)
                .set(m -> m.getByteProperty(), new StringValue("10"))
                .set(m -> m.getCharProperty(), new StringValue("c"))
                .set(m -> m.getDoubleProperty(), new StringValue("1.0"))
                .set(m -> m.getDoubleWrapperProperty(), null)
                .set(m -> m.getFloatProperty(), new StringValue("2.0"))
                .set(m -> m.getFloatWrapperProperty(), null)
                .set(m -> m.getIntProperty(), new StringValue("1"))
                .set(m -> m.getIntWrapperProperty(), null)
                .set(m -> m.getLongProperty(), new StringValue("2"))
                .set(m -> m.getLongWrapperProperty(), null)
                .set(m -> m.getShortProperty(), new StringValue("3"))
                .set(m -> m.getShortWrapperProperty(), null)
                .set(m -> m.getStringProperty(), new StringValue("string"));

        assertEquals(true, instance.isBooleanProperty());
        assertNull(instance.getBooleanWrapperProperty());
        assertEquals(10, instance.getByteProperty());
        assertEquals('c', instance.getCharProperty());
        assertEquals(1.0d, instance.getDoubleProperty(), 0.1);
        assertNull(instance.getDoubleWrapperProperty());
        assertEquals(2.0f, instance.getFloatProperty(), 0.1);
        assertNull(instance.getFloatWrapperProperty());
        assertEquals(1, instance.getIntProperty());
        assertNull(instance.getIntWrapperProperty());
        assertEquals(2, instance.getLongProperty());
        assertNull(instance.getLongWrapperProperty());
        assertEquals(3, instance.getShortProperty());
        assertNull(instance.getShortWrapperProperty());
        assertEquals("string", instance.getStringProperty());

        JsonObject json = new JsonObject("{\"nullBoolean\": null, \"nullNumeric\": null}");
        outline.wrap(instance)
                .set(m -> m.getBooleanWrapperProperty(), json.get("nullBoolean"))
                .set(m -> m.getDoubleWrapperProperty(), json.get("nullNumeric"))
                .set(m -> m.getFloatWrapperProperty(), json.get("nullNumeric"))
                .set(m -> m.getIntWrapperProperty(), json.get("nullNumeric"))
                .set(m -> m.getLongWrapperProperty(), json.get("nullNumeric"))
                .set(m -> m.getShortWrapperProperty(), json.get("nullNumeric"));

        assertNull(instance.getBooleanWrapperProperty());
        assertNull(instance.getDoubleWrapperProperty());
        assertNull(instance.getFloatWrapperProperty());
        assertNull(instance.getIntWrapperProperty());
        assertNull(instance.getLongWrapperProperty());
        assertNull(instance.getShortWrapperProperty());
    }

    @Test
    public void unwrapWrappedBean() {
        Outline<TestBeanClass> outline = OutlineBuilder.DEFAULT.defaultCamelCased().build(TestBeanClass.class);

        TestBeanClass instance = new TestBeanClass();

        assertEquals(instance, outline.wrap(instance).get());
    }
}
