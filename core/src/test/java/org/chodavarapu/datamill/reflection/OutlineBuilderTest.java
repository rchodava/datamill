package org.chodavarapu.datamill.reflection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class OutlineBuilderTest {
    private int actualBeanMethodInvocations = 0;

    public class TestBeanClass {
        public String getReadWriteProperty() {
            actualBeanMethodInvocations++;
            return "";
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
        }
    }

    @Test
    public void buildDefaultSnakeCased() {
        OutlineBuilder<TestBeanClass> outlineBuilder = new OutlineBuilder<>(TestBeanClass.class);
        Outline<TestBeanClass> outline = outlineBuilder.defaultSnakeCased().build();

        assertEquals("read_only_property", outline.name(outline.members().getReadOnlyProperty()));
        assertEquals("boolean_property", outline.name(outline.members().isBooleanProperty()));
        assertEquals("read_write_property", outline.name(members -> members.setReadWriteProperty("")));

        assertEquals("test_bean_class", outline.name());

        assertEquals(0, actualBeanMethodInvocations);
    }

    @Test
    public void buildDefaultCamelCased() {
        OutlineBuilder<TestBeanClass> outlineBuilder = new OutlineBuilder<>(TestBeanClass.class);
        Outline<TestBeanClass> outline = outlineBuilder.defaultCamelCased().build();

        assertEquals("readOnlyProperty", outline.name(outline.members().getReadOnlyProperty()));
        assertEquals("booleanProperty", outline.name(outline.members().isBooleanProperty()));
        assertEquals("readWriteProperty", outline.name(members -> members.setReadWriteProperty("")));

        assertEquals("TestBeanClass", outline.name());

        assertEquals(0, actualBeanMethodInvocations);
    }
}
