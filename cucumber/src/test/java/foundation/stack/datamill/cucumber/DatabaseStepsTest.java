package foundation.stack.datamill.cucumber;

import foundation.stack.datamill.db.DatabaseClient;
import org.junit.Test;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class DatabaseStepsTest {
    @Test
    public void storeAndCheckRows() throws Exception {
        PropertyStore store = new PropertyStore();
        store.put("db", "jdbc:hsqldb:mem:test");
        store.put("quark", "charm");
        store.put("quark2", "strange");
        store.put("spin", "50");

        PlaceholderResolver resolver = new PlaceholderResolver(store);
        DatabaseSteps steps = new DatabaseSteps(resolver);

        new DatabaseClient("jdbc:hsqldb:mem:test")
                .update("create table quarks(name varchar(64), spin integer)", 0)
                .count()
                .toBlocking()
                .last();

        steps.storeDatabaseRow("quarks", "jdbc:hsqldb:mem:test", "{\"name\":\"up\",\"spin\":100}");
        steps.checkDatabaseRowExists("quarks", "jdbc:hsqldb:mem:test", "name = 'up'");

        steps.storeDatabaseRow("quarks", "jdbc:hsqldb:mem:test", "{\"name\": \"down\",\"spin\":null}");
        steps.checkDatabaseRowExists("quarks", "jdbc:hsqldb:mem:test", "name = 'down' and spin is null");
        steps.checkDatabaseRowExists("quarks", "jdbc:hsqldb:mem:test", "name = 'down'", "spin is null");

        steps.storeDatabaseRow("quarks", "{db}", "{\"name\":\"{quark}\",\"spin\":100}");
        steps.checkDatabaseRowExists("quarks", "jdbc:hsqldb:mem:test", "name = 'charm' and spin = 100");

        steps.storeDatabaseRow("quarks", "{db}", "{\"name\":\"strange\",\"spin\":50}");
        steps.checkDatabaseRowExists("quarks", "{db}", "name = '{quark2}' and spin = 50");
        steps.checkDatabaseRowExists("quarks", "{db}", "name = '{quark2}'", "spin = {spin}");
    }
}
