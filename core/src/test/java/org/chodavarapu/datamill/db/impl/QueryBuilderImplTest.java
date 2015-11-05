package org.chodavarapu.datamill.db.impl;

import com.google.common.collect.ImmutableMap;
import org.chodavarapu.datamill.db.Row;
import org.junit.Test;
import rx.Observable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class QueryBuilderImplTest {
    private static class TestQueryBuilderImpl extends QueryBuilderImpl {
        private String lastQuery;
        private Object[] lastParameters;

        public String getLastQuery() {
            return lastQuery;
        }

        public Object[] getLastParameters() {
            return lastParameters;
        }

        @Override
        protected Observable<Row> query(String query) {
            lastQuery = query;
            return null;
        }

        @Override
        protected Observable<Row> query(String query, Object... parameters) {
            lastQuery = query;
            lastParameters = parameters;
            return null;
        }
    }

    @Test
    public void selectQueries() {
        TestQueryBuilderImpl queryBuilder = new TestQueryBuilderImpl();

        queryBuilder.selectAll().from("table_name").all();
        assertEquals("SELECT * FROM table_name", queryBuilder.getLastQuery());

        queryBuilder.selectAll().from("table_name").where().eq("int_column", 2);
        assertEquals("SELECT * FROM table_name WHERE int_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());

        queryBuilder.selectAll().from("table_name").where().eq("boolean_column", true);
        assertEquals("SELECT * FROM table_name WHERE boolean_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { true }, queryBuilder.getLastParameters());

        queryBuilder.selectAll().from("table_name").where().eq("string_column", "value");
        assertEquals("SELECT * FROM table_name WHERE string_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { "value" }, queryBuilder.getLastParameters());

        queryBuilder.select("column_name").from("table_name").all();
        assertEquals("SELECT column_name FROM table_name", queryBuilder.getLastQuery());

        queryBuilder.select("column_name", "second_column").from("table_name").all();
        assertEquals("SELECT column_name, second_column FROM table_name", queryBuilder.getLastQuery());

        queryBuilder.select(Arrays.asList("column_name", "second_column")).from("table_name").all();
        assertEquals("SELECT column_name, second_column FROM table_name", queryBuilder.getLastQuery());

        queryBuilder.select("column_name", "second_column").from("table_name").where().eq("int_column", 2);
        assertEquals("SELECT column_name, second_column FROM table_name WHERE int_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());
    }

    @Test
    public void deleteQueries() {
        TestQueryBuilderImpl queryBuilder = new TestQueryBuilderImpl();

        queryBuilder.deleteFrom("table_name").all();
        assertEquals("DELETE FROM table_name", queryBuilder.getLastQuery());

        queryBuilder.deleteFrom("table_name").where().eq("int_column", 2);
        assertEquals("DELETE FROM table_name WHERE int_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());

        queryBuilder.deleteFrom("table_name").where().eq("boolean_column", true);
        assertEquals("DELETE FROM table_name WHERE boolean_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { true }, queryBuilder.getLastParameters());

        queryBuilder.deleteFrom("table_name").where().eq("string_column", "value");
        assertEquals("DELETE FROM table_name WHERE string_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { "value" }, queryBuilder.getLastParameters());
    }

    @Test
    public void insertQueries() {
        TestQueryBuilderImpl queryBuilder = new TestQueryBuilderImpl();

        queryBuilder.insertInto("table_name").row(r -> r.put("int_column", 2).put("boolean_column", true).build());
        assertEquals("INSERT INTO table_name (int_column, boolean_column) VALUES (?, ?)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2, true }, queryBuilder.getLastParameters());

        queryBuilder.insertInto("table_name").row(r -> r.put("boolean_column", true).put("int_column", 2).build());
        assertEquals("INSERT INTO table_name (boolean_column, int_column) VALUES (?, ?)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { true, 2 }, queryBuilder.getLastParameters());

        queryBuilder.insertInto("table_name").row(r -> r.put("boolean_value", true).put("null_column", null).build());
        assertEquals("INSERT INTO table_name (boolean_value, null_column) VALUES (?, NULL)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { true }, queryBuilder.getLastParameters());

        queryBuilder.insertInto("table_name").row(r -> r.put("string_column", "value").build());
        assertEquals("INSERT INTO table_name (string_column) VALUES (?)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { "value" }, queryBuilder.getLastParameters());

        queryBuilder.insertInto("table_name").values(ImmutableMap.of("int_column", 2, "boolean_column", true));
        assertEquals("INSERT INTO table_name (int_column, boolean_column) VALUES (?, ?)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2, true }, queryBuilder.getLastParameters());

        HashMap<String, Object> values = new LinkedHashMap<>();
        values.put("int_column", 2);
        values.put("boolean_column", null);
        queryBuilder.insertInto("table_name").values(values);
        assertEquals("INSERT INTO table_name (int_column, boolean_column) VALUES (?, NULL)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());

        HashMap<String, Object> row1 = new LinkedHashMap<>();
        row1.put("int_column", 2);
        row1.put("boolean_column", null);
        HashMap<String, Object> row2 = new LinkedHashMap<>();
        row2.put("int_column", null);
        row2.put("string_column", "value");
        queryBuilder.insertInto("table_name").values(row1, row2);
        assertEquals("INSERT INTO table_name (int_column, boolean_column, string_column) VALUES (?, NULL, NULL), (NULL, NULL, ?)",
                queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2, "value" }, queryBuilder.getLastParameters());
    }
}
