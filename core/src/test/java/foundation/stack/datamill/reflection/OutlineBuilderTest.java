package foundation.stack.datamill.reflection;

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
        OutlineBuilder outlineBuilder = new OutlineBuilder();
        Outline<TestBeanClass> outline = outlineBuilder.defaultSnakeCased().build(TestBeanClass.class);

        assertEquals("read_only_property", outline.member(m -> m.getReadOnlyProperty()).name());
        assertEquals("boolean_property", outline.member(m -> m.isBooleanProperty()).name());
        assertEquals("read_write_property", outline.member(m -> m.setReadWriteProperty("")).name());

        assertEquals("test_bean_class", outline.name());
        assertEquals("test_bean_classes", outline.pluralName());

        assertEquals(0, actualBeanMethodInvocations);
    }

    @Test
    public void buildDefaultCamelCased() {
        OutlineBuilder outlineBuilder = new OutlineBuilder();
        Outline<TestBeanClass> outline = outlineBuilder.defaultCamelCased().build(TestBeanClass.class);

        assertEquals("readOnlyProperty", outline.member(m -> m.getReadOnlyProperty()).name());
        assertEquals("booleanProperty", outline.member(m -> m.isBooleanProperty()).name());
        assertEquals("readWriteProperty", outline.member(m -> m.setReadWriteProperty("")).name());

        assertEquals("TestBeanClass", outline.name());
        assertEquals("TestBeanClasses", outline.pluralName());

        assertEquals(0, actualBeanMethodInvocations);
    }
}
