package foundation.stack.datamill.db.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class H2UrlTransformerTest {
    @Test
    public void transform() {
        assertEquals("jdbc:mysql://localhost",
                new H2DatabaseTypeAdapter.H2UrlTransformer().transform("jdbc:mysql://localhost"));
        assertEquals("jdbc:h2:mem:db1;MODE=MySQL",
                new H2DatabaseTypeAdapter.H2UrlTransformer().transform("jdbc:h2:mem:db1"));
        assertEquals("jdbc:h2:mem:db1;AUTO_SERVER=true;MODE=MySQL",
                new H2DatabaseTypeAdapter.H2UrlTransformer().transform("jdbc:h2:mem:db1;AUTO_SERVER=true"));
        assertEquals("jdbc:h2:mem:db1;MODE=MySQL",
                new H2DatabaseTypeAdapter.H2UrlTransformer().transform("jdbc:h2:mem:db1;MODE=MySQL"));
        assertEquals("jdbc:h2:mem:db1;MODE=MySQL;AUTO_SERVER=true",
                new H2DatabaseTypeAdapter.H2UrlTransformer().transform("jdbc:h2:mem:db1;MODE=MySQL;AUTO_SERVER=true"));
    }
}
