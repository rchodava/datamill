import org.chodavarapu.datamill.reflection.Bean;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Collectors;

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
        Assert.assertThat(new Bean<>(bean).properties().stream().map(p -> p.getName()).collect(Collectors.toList()),
                CoreMatchers.hasItems("readWriteProperty", "readOnlyProperty", "booleanProperty"));
    }

    @Test
    public void testGetProperty() {
        TestBeanClass bean = new TestBeanClass();
        Assert.assertTrue(new Bean<>(bean).property("readWriteProperty").isPresent());
        Assert.assertTrue(new Bean<>(bean).property("booleanProperty").isPresent());
        Assert.assertFalse(new Bean<>(bean).property("undefinedProperty").isPresent());
    }
}
