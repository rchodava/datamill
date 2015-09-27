package org.chodavarapu.datamill.reflection.impl;

import org.chodavarapu.datamill.reflection.Outline;
import org.chodavarapu.datamill.reflection.OutlineBuilder;
import org.junit.Test;

import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
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
    }

    @Test
    public void camelCasedNames() {
        OutlineBuilder<TestBeanClass> outlineBuilder = new OutlineBuilder<>(TestBeanClass.class);
        Outline<TestBeanClass> outline = outlineBuilder.defaultSnakeCased().build();

        assertEquals("readOnlyProperty", outline.camelCasedName(outline.members().getReadOnlyProperty()));
        assertEquals("booleanProperty", outline.camelCasedName(outline.members().isBooleanProperty()));
        assertEquals("readWriteProperty", outline.camelCasedName(members -> members.setReadWriteProperty("")));

        assertEquals("TestBeanClass", outline.camelCasedName());

        assertEquals(0, actualBeanMethodInvocations);
    }

    @Test
    public void propertyNamesCamelCased() {
        assertThat(new OutlineBuilder<>(TestBeanClass.class).defaultCamelCased().build().propertyNames(),
                hasItems("readWriteProperty", "readOnlyProperty", "booleanProperty"));

        assertEquals(0, actualBeanMethodInvocations);
    }

    @Test
    public void propertyNamesSnakeCased() {
        assertThat(new OutlineBuilder<>(TestBeanClass.class).defaultSnakeCased().build().propertyNames(),
                hasItems("read_write_property", "read_only_property", "boolean_property"));

        assertEquals(0, actualBeanMethodInvocations);
    }

    @Test
    public void getPropertiesCamelCased() {
        assertThat(new OutlineBuilder<>(TestBeanClass.class).defaultCamelCased().build().properties().stream()
                        .map(p -> p.getName()).collect(Collectors.toList()),
                hasItems("readWriteProperty", "readOnlyProperty", "booleanProperty"));

        assertEquals(0, actualBeanMethodInvocations);
    }

    @Test
    public void getPropertiesSnakeCased() {
        assertThat(new OutlineBuilder<>(TestBeanClass.class).defaultSnakeCased().build().properties().stream()
                        .map(p -> p.getName()).collect(Collectors.toList()),
                hasItems("read_write_property", "read_only_property", "boolean_property"));

        assertEquals(0, actualBeanMethodInvocations);
    }

    @Test
    public void getPropertyCamelCased() {
        Outline<TestBeanClass> outline = new OutlineBuilder<>(TestBeanClass.class).defaultCamelCased().build();
        assertEquals("readWriteProperty", outline.property(outline.members().getReadWriteProperty()).getName());
        assertEquals("booleanProperty", outline.property(outline.members().isBooleanProperty()).getName());
        assertEquals("readWriteProperty", outline.property(outline.members().getReadWriteProperty()).getName());

        assertEquals(0, actualBeanMethodInvocations);
    }

    @Test
    public void getPropertySnakeCased() {
        Outline<TestBeanClass> outline = new OutlineBuilder<>(TestBeanClass.class).defaultSnakeCased().build();
        assertEquals("read_write_property", outline.property(outline.members().getReadWriteProperty()).getName());
        assertEquals("boolean_property", outline.property(outline.members().isBooleanProperty()).getName());
        assertEquals("read_write_property", outline.property(outline.members().getReadWriteProperty()).getName());

        assertEquals(0, actualBeanMethodInvocations);
    }

    @Test
    public void snakeCasedNames() {
        OutlineBuilder<TestBeanClass> outlineBuilder = new OutlineBuilder<>(TestBeanClass.class);
        Outline<TestBeanClass> outline = outlineBuilder.defaultCamelCased().build();

        assertEquals("read_only_property", outline.snakeCasedName(outline.members().getReadOnlyProperty()));
        assertEquals("boolean_property", outline.snakeCasedName(outline.members().isBooleanProperty()));
        assertEquals("read_write_property", outline.snakeCasedName(members -> members.setReadWriteProperty("")));

        assertEquals("test_bean_class", outline.snakeCasedName());

        assertEquals(0, actualBeanMethodInvocations);
    }

    @Test
    public void wrapAndBeanSet() {
        OutlineBuilder<TestBeanClass> outlineBuilder = new OutlineBuilder<>(TestBeanClass.class);
        Outline<TestBeanClass> outline = outlineBuilder.defaultCamelCased().build();

        TestBeanClass instance = new TestBeanClass();
        outline.wrap(instance).set(outline.members().getReadWriteProperty(), "value1");

        assertEquals(1, actualBeanMethodInvocations);
        assertEquals("value1", instance.getReadWriteProperty());
    }
}
