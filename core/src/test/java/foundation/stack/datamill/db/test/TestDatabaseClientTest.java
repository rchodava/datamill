package foundation.stack.datamill.db.test;

import foundation.stack.datamill.reflection.Outline;
import foundation.stack.datamill.reflection.OutlineBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class TestDatabaseClientTest {
    private static class TestModel {
        private int property;
        private String stringProperty = "default";

        public int getProperty() {
            return property;
        }

        public void setProperty(int property) {
            this.property = property;
        }

        public String getStringProperty() {
            return stringProperty;
        }

        public void setStringProperty(String stringProperty) {
            this.stringProperty = stringProperty;
        }
    }

    @Mock
    private Database database;

    private DatabaseRowBuilder<TestModel> rowBuilder = new DatabaseRowBuilder<>(TestModel.class);

    @Test
    public void databaseMethodsInvoked() {
        TestDatabaseClient client = new TestDatabaseClient(database);

        client.clean();
        verify(database).clean();

        client.migrate();
        verify(database).migrate();

        client.getVersion();
        verify(database).getVersion();

        client.getURL();
        verify(database).getURL();

        client.changeCatalog("catalog");
        verify(database).changeCatalog("catalog");

        client.query("SELECT * FROM table").stream();
        verify(database).query("SELECT * FROM table", new Object[0]);

        client.update("UPDATE table SET column = NULL", new Object[0]).count();
        verify(database).updateAndGetAffectedCount("UPDATE table SET column = NULL", new Object[0]);

        client.update("UPDATE table SET column = NULL", new Object[0]).getIds();
        verify(database).updateAndGetIds("UPDATE table SET column = NULL", new Object[0]);
    }

    @Test
    public void rowBuilding() {
        Outline<TestModel> testModelOutline = new OutlineBuilder().build(TestModel.class);

        TestDatabaseClient client = new TestDatabaseClient(database);

        when(database.query("SELECT test_models.property FROM test_models")).thenReturn(
                Observable.just(rowBuilder.build((b, outline) -> b
                        .put(outline.member(m -> m.getProperty()), 5)
                        .build())));

        TestSubscriber<TestModel> testSubscriber = new TestSubscriber<>();

        client.select(testModelOutline.member(m -> m.getProperty()))
                .from(testModelOutline)
                .all()
                .firstAs(row -> testModelOutline.wrap(new TestModel())
                        .set(m -> m.getProperty(), row.column(testModelOutline.member(m -> m.getProperty())))
                        .set(m -> m.getStringProperty(), row.column(testModelOutline.member(m -> m.getStringProperty())))
                        .unwrap())
                .subscribe(testSubscriber);

        assertEquals(5, testSubscriber.getOnNextEvents().get(0).getProperty());
        assertNull(testSubscriber.getOnNextEvents().get(0).getStringProperty());
    }
}
