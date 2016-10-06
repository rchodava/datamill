package foundation.stack.datamill.db.impl;

import foundation.stack.datamill.db.DatabaseClient;
import foundation.stack.datamill.reflection.Outline;
import foundation.stack.datamill.reflection.OutlineBuilder;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class DatabaseClientTest {
    private static class Quark {
        private String name;
        private int spin;

        public String getName() {
            return name;
        }

        public Quark setName(String name) {
            this.name = name;
            return this;
        }

        public int getSpin() {
            return spin;
        }

        public Quark setSpin(int spin) {
            this.spin = spin;
            return this;
        }
    }

    @Test
    public void queries() {
        DatabaseClient client = new DatabaseClient("jdbc:hsqldb:mem:test");
        Outline<Quark> outline = new OutlineBuilder().build(Quark.class);

        client.update("create table quarks(name varchar(64), spin integer)", 0)
                .count()
                .toBlocking()
                .last();

        client.insertInto(outline).row(rb -> rb
                .put(outline.member(m -> m.getName()), "up")
                .put(outline.member(m -> m.getSpin()), 1)
                .build())
                .count()
                .toBlocking()
                .last();

        List<Quark> quarks = client.selectAll().from(outline).all().getAs(r -> outline.wrap(new Quark())
                .set(p -> p.getName(), r.column(outline.member(m -> m.getName())))
                .set(p -> p.getSpin(), r.column(outline.member(m -> m.getSpin())))
                .unwrap())
                .toBlocking().last();

        assertEquals(1, quarks.size());
        assertEquals("up", quarks.get(0).getName());
        assertEquals(1, quarks.get(0).getSpin());

        Quark quark = client.selectAll().from(outline).all().firstAs(r -> outline.wrap(new Quark())
                .set(p -> p.getName(), r.column(outline.member(m -> m.getName())))
                .set(p -> p.getSpin(), r.column(outline.member(m -> m.getSpin())))
                .unwrap())
                .toBlocking().last();

        assertEquals(1, quarks.size());
        assertEquals("up", quarks.get(0).getName());
        assertEquals(1, quarks.get(0).getSpin());
    }
}
