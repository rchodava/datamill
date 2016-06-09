package foundation.stack.datamill.cucumber;

import foundation.stack.datamill.security.impl.BCrypt;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class PlaceholderResolverTest {
    @Test
    public void resolve() {
        PropertyStore store = new PropertyStore();
        store.put("property1", "value1");
        store.put("prop2", "value2");
        store.put("base64Prop", "user:pass");
        store.put("json", "{\"name\": \"test\", \"count\": 3}");

        PlaceholderResolver resolver = new PlaceholderResolver(store);
        assertEquals("Hello value1!", resolver.resolve("Hello {property1}!"));
        assertEquals("Hello value1value2!", resolver.resolve("Hello {property1}{prop2}!"));
        assertEquals("Hello value1 value2", resolver.resolve("Hello {property1} {prop2}"));
        assertEquals("value2Hello value1 value2", resolver.resolve("{prop2}Hello {property1} {prop2}"));
        assertEquals("value2Hello {prop3} value1 value2", resolver.resolve("{prop2}Hello {prop3} {property1} {prop2}"));
        assertEquals("value2Hello {prop3}{prop4} value1 value2", resolver.resolve("{prop2}Hello {prop3}{prop4} {property1} {prop2}"));
        assertEquals("Hello value1 test!", resolver.resolve("Hello {property1} {json.name}!"));
        assertEquals("Hello value1 3!", resolver.resolve("Hello {property1} {json.count}!"));
        assertEquals("value2Hello value1 value2 {prop3}!", resolver.resolve("{prop2}Hello {property1} {prop2} {prop3}!"));
        assertEquals("{prop3} value2Hello value1 value2!", resolver.resolve("{prop3} {prop2}Hello {property1} {prop2}!"));
        assertEquals("Hello world!", resolver.resolve("Hello world!"));
        assertEquals(33, resolver.resolve("H{randomAlphanumeric32}").length());
        assertTrue(BCrypt.checkpw("pass", resolver.resolve("{blowfish:pass}")));
        assertTrue(BCrypt.checkpw("value2", resolver.resolve("{blowfish:prop2}")));
        assertEquals("dXNlcjpwYXNz", resolver.resolve("{base64:user:pass}"));
        assertEquals("dXNlcjpwYXNz", resolver.resolve("{base64:base64Prop}"));
    }
}
