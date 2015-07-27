package org.chodavarapu.datamill.reflection;

import org.junit.Test;

import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class BeanTest {
    public class TestBeanClass {
        public String getReadWriteProperty() {
            return "";
        }

        public boolean isBooleanProperty() {
            return false;
        }

        public String getReadOnlyProperty() {
            return "";
        }

        public void setReadWriteProperty(String value) {

        }
    }

    @Test
    public void testGetProperties() {
        TestBeanClass bean = new TestBeanClass();
        assertThat(new Bean<>(bean).properties().stream().map(p -> p.getName()).collect(Collectors.toList()),
                hasItems("readWriteProperty", "readOnlyProperty", "booleanProperty"));
    }

    @Test
    public void testGetProperty() {
        TestBeanClass bean = new TestBeanClass();
        assertTrue(new Bean<>(bean).property("readWriteProperty").isPresent());
        assertTrue(new Bean<>(bean).property("booleanProperty").isPresent());
        assertFalse(new Bean<>(bean).property("undefinedProperty").isPresent());
    }
}
