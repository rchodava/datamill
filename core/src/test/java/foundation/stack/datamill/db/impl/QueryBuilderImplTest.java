package foundation.stack.datamill.db.impl;

import com.google.common.collect.ImmutableMap;
import foundation.stack.datamill.db.ResultBuilder;
import foundation.stack.datamill.db.UpdateQueryExecution;
import foundation.stack.datamill.reflection.Outline;
import foundation.stack.datamill.reflection.OutlineBuilder;
import org.junit.Test;
import rx.Observable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.Assert.*;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class QueryBuilderImplTest {
    private static final UpdateQueryExecution EMPTY_UPDATE_QUERY_EXECUTION = new UpdateQueryExecution() {
        @Override
        public Observable<Integer> count() {
            return null;
        }

        @Override
        public Observable<Long> getIds() {
            return null;
        }
    };

    private static class QueryTestBean {
        public String getName() {
            return "";
        }

        public String getId() {
            return "";
        }
    }

    private static class InsertQueryBean {
        private String id;
        private String name;

        public InsertQueryBean(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    private static class TestQueryBuilderImpl extends QueryBuilderImpl {
        private String lastQuery;
        private Object[] lastParameters;
        private boolean lastWasUpdate;

        public String getLastQuery() {
            return lastQuery;
        }

        public Object[] getLastParameters() {
            return lastParameters;
        }

        public boolean getLastWasUpdate() {
            return lastWasUpdate;
        }

        @Override
        protected ResultBuilder query(String query) {
            lastQuery = query;
            lastWasUpdate = false;
            lastParameters = new Object[0];
            return null;
        }

        @Override
        protected ResultBuilder query(String query, Object... parameters) {
            lastQuery = query;
            lastParameters = parameters;
            lastWasUpdate = false;
            return null;
        }

        @Override
        public UpdateQueryExecution update(String query, Object... parameters) {
            lastQuery = query;
            lastParameters = parameters;
            lastWasUpdate = true;
            return EMPTY_UPDATE_QUERY_EXECUTION;
        }
    }

    @Test
    public void selectQueries() {
        Outline<QueryTestBean> outline = OutlineBuilder.DEFAULT.build(QueryTestBean.class);
        TestQueryBuilderImpl queryBuilder = new TestQueryBuilderImpl();

        queryBuilder.selectAll().from("table_name").all();
        assertEquals("SELECT * FROM table_name", queryBuilder.getLastQuery());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.selectAll().from("table_name").limit(1);
        assertEquals("SELECT * FROM table_name LIMIT 1", queryBuilder.getLastQuery());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.selectAll().from("table_name").limit(10, 20);
        assertEquals("SELECT * FROM table_name LIMIT 10, 20", queryBuilder.getLastQuery());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.selectAll().from("table_name").orderBy("column").asc().limit(1);
        assertEquals("SELECT * FROM table_name ORDER BY column ASC LIMIT 1", queryBuilder.getLastQuery());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.selectAll().from("table_name").orderBy("column").asc().andOrderBy("column2").desc().limit(1);
        assertEquals("SELECT * FROM table_name ORDER BY column ASC, column2 DESC LIMIT 1", queryBuilder.getLastQuery());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.selectAll().from(outline).all();
        assertEquals("SELECT * FROM query_test_beans", queryBuilder.getLastQuery());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.selectAll().from("table_name").where(c -> c.eq("int_column", 2)).all();
        assertEquals("SELECT * FROM table_name WHERE int_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.selectAll().from("table_name").where(c -> c.eq("int_column", 2)).orderBy("int_column").asc().all();
        assertEquals("SELECT * FROM table_name WHERE int_column = ? ORDER BY int_column ASC", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.selectAll().from("table_name").where(c -> c.eq("int_column", 2)).limit(1);
        assertEquals("SELECT * FROM table_name WHERE int_column = ? LIMIT 1", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.selectAll().from("table_name").where(c -> c.eq("int_column", 2)).limit(10, 20);
        assertEquals("SELECT * FROM table_name WHERE int_column = ? LIMIT 10, 20", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.selectAll().from("table_name").where(c -> c.eq("int_column", 2)).orderBy("int_column").desc().limit(10, 20);
        assertEquals("SELECT * FROM table_name WHERE int_column = ? ORDER BY int_column DESC LIMIT 10, 20", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.selectAll().from("table_name").where(c -> c.eq(outline.member(m -> m.getId()), 2)).all();
        assertEquals("SELECT * FROM table_name WHERE query_test_beans.id = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.selectAll().from("table_name").where(c -> c.eq("table_name", "int_column", 2)).all();
        assertEquals("SELECT * FROM table_name WHERE table_name.int_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.selectAll().from("table_name").where(c -> c.eq("boolean_column", true)).all();
        assertEquals("SELECT * FROM table_name WHERE boolean_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { true }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.selectAll().from("table_name").where(c -> c.eq("string_column", "value")).all();
        assertEquals("SELECT * FROM table_name WHERE string_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { "value" }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.selectAllIn(outline).from("table_name").all();
        assertEquals("SELECT query_test_beans.name, query_test_beans.id FROM table_name", queryBuilder.getLastQuery());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select(outline.member(m -> m.getName())).from("table_name").all();
        assertEquals("SELECT query_test_beans.name FROM table_name", queryBuilder.getLastQuery());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select("column_name").from("table_name").all();
        assertEquals("SELECT column_name FROM table_name", queryBuilder.getLastQuery());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.selectQualified("table_name", "column_name").from("table_name").all();
        assertEquals("SELECT table_name.column_name FROM table_name", queryBuilder.getLastQuery());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select("column_name", "second_column").from("table_name").all();
        assertEquals("SELECT column_name, second_column FROM table_name", queryBuilder.getLastQuery());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.selectQualified("table_name", "column_name", "second_column").from("table_name").all();
        assertEquals("SELECT table_name.column_name, table_name.second_column FROM table_name", queryBuilder.getLastQuery());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select(Arrays.asList("column_name", "second_column")).from("table_name").all();
        assertEquals("SELECT column_name, second_column FROM table_name", queryBuilder.getLastQuery());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.selectQualified("table_name", Arrays.asList("column_name", "second_column")).from("table_name").all();
        assertEquals("SELECT table_name.column_name, table_name.second_column FROM table_name", queryBuilder.getLastQuery());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select("column_name", "second_column").from("table_name").where(c -> c.eq("int_column", 2)).all();
        assertEquals("SELECT column_name, second_column FROM table_name WHERE int_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select("column_name", "second_column").from("table_name").where(c -> c.eq("int_column", 2).and().eq("boolean_column", true)).all();
        assertEquals("SELECT column_name, second_column FROM table_name WHERE int_column = ? AND boolean_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2, true }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select("column_name", "second_column").from("table_name").where(c -> c.eq("int_column", 2).or().eq("boolean_column", true)).all();
        assertEquals("SELECT column_name, second_column FROM table_name WHERE int_column = ? OR boolean_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2, true }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select("column_name", "second_column").from("table_name").where(c -> c.eq("int_column", 2).and().eq("boolean_column", null)).all();
        assertEquals("SELECT column_name, second_column FROM table_name WHERE int_column = ? AND boolean_column = NULL", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select("column_name", "second_column")
                .from("table_name")
                .where(c -> c.eq("int_column", 2)
                        .or().eq("boolean_column", true)
                        .and().eq("boolean_column2", null))
                .all();
        assertEquals("SELECT column_name, second_column FROM table_name WHERE int_column = ? OR boolean_column = ? AND boolean_column2 = NULL", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2, true }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select("column_name", "second_column")
                .from("table_name")
                .where(c -> c.and(l -> l.eq("int_column", 2), r -> r.eq("boolean_column", true)))
                .all();
        assertEquals("SELECT column_name, second_column FROM table_name WHERE (int_column = ?) AND (boolean_column = ?)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2, true }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select("column_name", "second_column")
                .from("table_name")
                .where(c -> c.or(l -> l.eq("int_column", 2), r -> r.eq("boolean_column", true)))
                .all();
        assertEquals("SELECT column_name, second_column FROM table_name WHERE (int_column = ?) OR (boolean_column = ?)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2, true }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select("column_name", "second_column")
                .from("table_name")
                .where(c -> c.or(l -> l.gt("int_column", 2).and().lt("int_column2", 5), r -> r.eq("boolean_column", true)))
                .all();
        assertEquals("SELECT column_name, second_column FROM table_name WHERE (int_column > ? AND int_column2 < ?) OR (boolean_column = ?)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2, 5, true }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select("column_name", "second_column")
                .from("table_name")
                .where(c -> c.or(
                        l -> l.gt("table_name", "int_column", 2).and().lt("table_name", "int_column2", 5),
                        r -> r.eq("boolean_column", true)))
                .all();
        assertEquals("SELECT column_name, second_column FROM table_name WHERE (table_name.int_column > ? AND table_name.int_column2 < ?) OR (boolean_column = ?)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2, 5, true }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select("column_name", "second_column").from("table_name").where(c -> c.eq("int_column", null)).all();
        assertEquals("SELECT column_name, second_column FROM table_name WHERE int_column = NULL", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select("column_name", "second_column").from("table_name").where(c -> c.is("int_column", null)).all();
        assertEquals("SELECT column_name, second_column FROM table_name WHERE int_column IS NULL", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select("column_name", "second_column").from("table_name").where(c -> c.eq("table_name", "int_column", 2)).all();
        assertEquals("SELECT column_name, second_column FROM table_name WHERE table_name.int_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select("column_name", "second_column").from("table_name").where(c -> c.in("table_name", "int_column", Arrays.asList(1, 2))).all();
        assertEquals("SELECT column_name, second_column FROM table_name WHERE table_name.int_column IN (?,?)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 1, 2 }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select("column_name", "second_column").from("table_name").where(c -> c.in("table_name", "int_column", Collections.singletonList(1))).all();
        assertEquals("SELECT column_name, second_column FROM table_name WHERE table_name.int_column IN (?)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 1 }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select(Arrays.asList("column_name", "second_column")).from("table_name")
                .leftJoin("second_table").onEq("second_column", "third_column").all();
        assertEquals("SELECT column_name, second_column FROM table_name LEFT JOIN second_table ON second_column = third_column", queryBuilder.getLastQuery());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select(Arrays.asList("column_name", "second_column")).from("table_name")
                .leftJoin(outline).onEq("second_column", "third_column").all();
        assertEquals("SELECT column_name, second_column FROM table_name LEFT JOIN query_test_beans ON second_column = third_column", queryBuilder.getLastQuery());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select(Arrays.asList("column_name", "second_column")).from("table_name")
                .leftJoin("second_table").onEq("second_column", "third_column").where(c -> c.eq("column_name", 2)).all();
        assertEquals("SELECT column_name, second_column FROM table_name LEFT JOIN second_table ON second_column = third_column WHERE column_name = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select(Arrays.asList("column_name", "second_column")).from("table_name")
                .leftJoin("second_table").onEq("second_table", "second_column", outline.member(m -> m.getName())).all();
        assertEquals("SELECT column_name, second_column FROM table_name LEFT JOIN second_table ON second_table.second_column = query_test_beans.name", queryBuilder.getLastQuery());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select(Arrays.asList("column_name", "second_column")).from("table_name")
                .leftJoin("second_table").onEq(outline.member(m -> m.getId()), outline.member(m -> m.getName())).all();
        assertEquals("SELECT column_name, second_column FROM table_name LEFT JOIN second_table ON query_test_beans.id = query_test_beans.name", queryBuilder.getLastQuery());
        assertFalse(queryBuilder.getLastWasUpdate());
    }

    @Test
    public void deleteQueries() {
        TestQueryBuilderImpl queryBuilder = new TestQueryBuilderImpl();

        queryBuilder.deleteFrom("table_name").all();
        assertEquals("DELETE FROM table_name", queryBuilder.getLastQuery());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.deleteFrom("table_name").limit(1);
        assertEquals("DELETE FROM table_name LIMIT 1", queryBuilder.getLastQuery());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.deleteFrom("table_name").where(c ->c.eq("int_column", 2)).all();
        assertEquals("DELETE FROM table_name WHERE int_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[]{2}, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.deleteFrom("table_name").where(c ->c.eq("int_column", 2)).limit(1);
        assertEquals("DELETE FROM table_name WHERE int_column = ? LIMIT 1", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[]{2}, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.deleteFrom("table_name").where(c -> c.eq("boolean_column", true)).all();
        assertEquals("DELETE FROM table_name WHERE boolean_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { true }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.deleteFrom("table_name").where(c -> c.eq("string_column", "value")).all();
        assertEquals("DELETE FROM table_name WHERE string_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { "value" }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.deleteFromNamed("table_name")
                .where(c -> c.eq("string_column", "value")).all();
        assertEquals("DELETE table_name FROM table_name WHERE string_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { "value" }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.deleteFrom("table_name").where(c -> c.in("string_column", Arrays.asList(1, 2))).all();
        assertEquals("DELETE FROM table_name WHERE string_column IN (?,?)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 1, 2 }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.deleteFrom("table_name").where(c -> c.in("string_column", Collections.singletonList(1))).all();
        assertEquals("DELETE FROM table_name WHERE string_column IN (?)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 1 }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.deleteFrom("table_name")
                .leftJoin("second_table").onEq("second_column", "third_column")
                .where(c -> c.eq("string_column", "value")).all();
        assertEquals("DELETE FROM table_name LEFT JOIN second_table ON second_column = third_column WHERE string_column = ?",
                queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { "value" }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());
    }

    @Test
    public void insertQueries() {
        TestQueryBuilderImpl queryBuilder = new TestQueryBuilderImpl();

        queryBuilder.insertInto("table_name").row(r -> r.put("int_column", 2).put("boolean_column", true).build()).count();
        assertEquals("INSERT INTO table_name (int_column, boolean_column) VALUES (?, ?)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2, true }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.insertInto("table_name").row(r -> r.put("boolean_column", true).put("int_column", 2).build()).count();
        assertEquals("INSERT INTO table_name (boolean_column, int_column) VALUES (?, ?)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { true, 2 }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.insertInto("table_name").row(r -> r.put("boolean_value", true).put("null_column", null).build()).count();
        assertEquals("INSERT INTO table_name (boolean_value, null_column) VALUES (?, NULL)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { true }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.insertInto("table_name").row(r -> r.put("string_column", "value").build()).count();
        assertEquals("INSERT INTO table_name (string_column) VALUES (?)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { "value" }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.insertInto("table_name").values(
                Arrays.asList(new InsertQueryBean("id1", "name1"), new InsertQueryBean("id2", "name2")),
                (r, b) -> r.put("id", b.getId()).put("name", b.getName()).build()).count();
        assertEquals("INSERT INTO table_name (id, name) VALUES (?, ?), (?, ?)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { "id1", "name1", "id2", "name2" }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.insertInto("table_name").values(ImmutableMap.of("int_column", 2, "boolean_column", true)).count();
        assertEquals("INSERT INTO table_name (int_column, boolean_column) VALUES (?, ?)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2, true }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        HashMap<String, Object> values = new LinkedHashMap<>();
        values.put("int_column", 2);
        values.put("boolean_column", null);
        queryBuilder.insertInto("table_name").values(values).count();
        assertEquals("INSERT INTO table_name (int_column, boolean_column) VALUES (?, NULL)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        HashMap<String, Object> row1 = new LinkedHashMap<>();
        row1.put("int_column", 2);
        row1.put("boolean_column", null);
        HashMap<String, Object> row2 = new LinkedHashMap<>();
        row2.put("int_column", null);
        row2.put("string_column", "value");
        queryBuilder.insertInto("table_name").values(row1, row2).count();
        assertEquals("INSERT INTO table_name (int_column, boolean_column, string_column) VALUES (?, NULL, NULL), (NULL, NULL, ?)",
                queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2, "value" }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.insertInto("table_name").row(r -> r.put("boolean_column", true).put("int_column", 2).build())
                .onDuplicateKeyUpdate(r -> r.put("boolean_column", false).build()).count();
        assertEquals("INSERT INTO table_name (boolean_column, int_column) VALUES (?, ?) ON DUPLICATE KEY UPDATE boolean_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { true, 2, false }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.insertInto("table_name").values(
                Arrays.asList(new InsertQueryBean("id1", "name1"), new InsertQueryBean("id2", "name2")),
                (r, b) -> r.put("id", b.getId()).put("name", b.getName()).build())
                .onDuplicateKeyUpdate(ImmutableMap.of("name", 12))
                .count();
        assertEquals("INSERT INTO table_name (id, name) VALUES (?, ?), (?, ?) ON DUPLICATE KEY UPDATE name = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { "id1", "name1", "id2", "name2", 12 }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());
    }

    @Test
    public void updateQueries() {
        TestQueryBuilderImpl queryBuilder = new TestQueryBuilderImpl();

        queryBuilder.update("table_name").set(r -> r.put("int_column", 2).put("boolean_column", true).build()).all();
        assertEquals("UPDATE table_name SET int_column = ?, boolean_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2, true }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.update("table_name").set(r -> r.put("int_column", 2).put("boolean_column", true).build()).limit(1);
        assertEquals("UPDATE table_name SET int_column = ?, boolean_column = ? LIMIT 1", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2, true }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.update("table_name").set(r -> r.put("boolean_column", true).put("int_column", 2).build()).all();
        assertEquals("UPDATE table_name SET boolean_column = ?, int_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { true, 2 }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.update("table_name").set(r -> r.put("boolean_column", true).put("null_column", null).build()).all();
        assertEquals("UPDATE table_name SET boolean_column = ?, null_column = NULL", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { true }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.update("table_name").set(ImmutableMap.of("int_column", 2, "boolean_column", true)).all();
        assertEquals("UPDATE table_name SET int_column = ?, boolean_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2, true }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        HashMap<String, Object> values = new LinkedHashMap<>();
        values.put("int_column", 2);
        values.put("boolean_column", null);
        queryBuilder.update("table_name").set(values).all();
        assertEquals("UPDATE table_name SET int_column = ?, boolean_column = NULL", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.update("table_name").set(ImmutableMap.of("int_column", 1)).where(c -> c.eq("int_column", 2)).all();
        assertEquals("UPDATE table_name SET int_column = ? WHERE int_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 1, 2 }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.update("table_name").set(ImmutableMap.of("int_column", 1)).where(c -> c.eq("int_column", 2)).limit(1);
        assertEquals("UPDATE table_name SET int_column = ? WHERE int_column = ? LIMIT 1", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 1, 2 }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.update("table_name").set(ImmutableMap.of("int_column", 1)).where(c -> c.in("int_column", Arrays.asList(2, 3))).all();
        assertEquals("UPDATE table_name SET int_column = ? WHERE int_column IN (?,?)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 1, 2, 3 }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.update("table_name").set(ImmutableMap.of("int_column", 1)).where(c -> c.in("int_column", Collections.singletonList(2))).all();
        assertEquals("UPDATE table_name SET int_column = ? WHERE int_column IN (?)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 1, 2 }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.update("table_name").set(ImmutableMap.of("int_column", 1)).where(c -> c.eq("boolean_column", true)).all();
        assertEquals("UPDATE table_name SET int_column = ? WHERE boolean_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 1, true }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.update("table_name").set(ImmutableMap.of("int_column", 1)).where(c -> c.eq("string_column", "value")).all();
        assertEquals("UPDATE table_name SET int_column = ? WHERE string_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 1, "value" }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());
    }
}
