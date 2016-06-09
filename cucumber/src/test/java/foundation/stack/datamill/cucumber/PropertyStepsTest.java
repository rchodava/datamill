package foundation.stack.datamill.cucumber;

import foundation.stack.datamill.security.impl.BCrypt;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class PropertyStepsTest {
    private PlaceholderResolver resolver;
    private PropertyStore store;
    private PropertySteps steps;

    @Before
    public void setUp() {
        store = new PropertyStore();
        resolver = new PlaceholderResolver(store);
        steps = new PropertySteps(store, resolver);
    }

    @Test
    public void generation() {
        steps.generateRandomAlphanumeric("random");
        assertEquals(16, ((String) store.get("random")).length());

        steps.generateRandomAlphanumericAlternate("random2");
        assertEquals(16, ((String) store.get("random2")).length());

        steps.generatePassword("pass", "password");
        assertTrue(BCrypt.checkpw("pass", (String) store.get("password")));
    }

    @Test
    public void extraction() {
        store.put("existing", "capture/123/test");
        steps.captureUsingRegex("capture/(\\d+)", "existing", "digits");
        assertEquals("123", store.get("digits"));

        store.put("html", "<html><body>This is some text\nMore text\n<p>In a paragraph</p><a href=\"target\">" +
                "link text</a> And of course we want some surrounding text<br /><a href=\"second\">2nd</a></body></html>");
        steps.extractFirstLinkFromStoredHtml("html", "link");
        assertEquals("target", store.get("link"));
    }

    @Test
    public void storeAndRetreive() {
        steps.storeProperty("value", "test");
        assertEquals("value", store.get("test"));
        steps.removeProperty("test");
        assertNull(store.get("test"));
        store.put("property1", "value1");
        steps.storeProperty("{property1}", "test2");
        assertEquals("value1", store.get("test2"));
    }
}
