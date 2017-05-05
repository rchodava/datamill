package foundation.stack.datamill.db;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class DatabaseTypeTest {
    @Test
    public void guess() {
        assertEquals(DatabaseType.H2, DatabaseType.guess("jdbc:h2:mem"));
        assertEquals(DatabaseType.H2, DatabaseType.guess("jdbc:h2:tcp://localhost/~/test"));
        assertEquals(DatabaseType.MYSQL, DatabaseType.guess("jdbc:mysql:tcp://localhost:3306"));
    }
}
