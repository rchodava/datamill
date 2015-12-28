package org.chodavarapu.datamill.db.impl;

import com.google.common.collect.ImmutableMap;
import org.chodavarapu.datamill.db.Row;
import org.chodavarapu.datamill.db.UpdateBuilder;
import org.chodavarapu.datamill.db.UpdateQueryExecution;
import org.junit.Test;
import rx.Observable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.Assert.*;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class QueryBuilderImplTest {
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
        protected Observable<Row> query(String query) {
            lastQuery = query;
            lastWasUpdate = false;
            return null;
        }

        @Override
        protected Observable<Row> query(String query, Object... parameters) {
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
            return null;
        }
    }

    @Test
    public void selectQueries() {
        TestQueryBuilderImpl queryBuilder = new TestQueryBuilderImpl();

        queryBuilder.selectAll().from("table_name").all();
        assertEquals("SELECT * FROM table_name", queryBuilder.getLastQuery());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.selectAll().from("table_name").where().eq("int_column", 2);
        assertEquals("SELECT * FROM table_name WHERE int_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.selectAll().from("table_name").where().eq("table_name", "int_column", 2);
        assertEquals("SELECT * FROM table_name WHERE table_name.int_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.selectAll().from("table_name").where().eq("boolean_column", true);
        assertEquals("SELECT * FROM table_name WHERE boolean_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { true }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.selectAll().from("table_name").where().eq("string_column", "value");
        assertEquals("SELECT * FROM table_name WHERE string_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { "value" }, queryBuilder.getLastParameters());
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

        queryBuilder.select("column_name", "second_column").from("table_name").where().eq("int_column", 2);
        assertEquals("SELECT column_name, second_column FROM table_name WHERE int_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select("column_name", "second_column").from("table_name").where().eq("table_name", "int_column", 2);
        assertEquals("SELECT column_name, second_column FROM table_name WHERE table_name.int_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select(Arrays.asList("column_name", "second_column")).from("table_name")
                .leftJoin("second_table").onEq("second_column", "third_column").all();
        assertEquals("SELECT column_name, second_column FROM table_name LEFT JOIN second_table ON second_column = third_column", queryBuilder.getLastQuery());
        assertFalse(queryBuilder.getLastWasUpdate());

        queryBuilder.select(Arrays.asList("column_name", "second_column")).from("table_name")
                .leftJoin("second_table").onEq("second_column", "third_column").where().eq("column_name", 2);
        assertEquals("SELECT column_name, second_column FROM table_name LEFT JOIN second_table ON second_column = third_column WHERE column_name = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());
        assertFalse(queryBuilder.getLastWasUpdate());
    }

    @Test
    public void deleteQueries() {
        TestQueryBuilderImpl queryBuilder = new TestQueryBuilderImpl();

        queryBuilder.deleteFrom("table_name").all();
        assertEquals("DELETE FROM table_name", queryBuilder.getLastQuery());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.deleteFrom("table_name").where().eq("int_column", 2);
        assertEquals("DELETE FROM table_name WHERE int_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.deleteFrom("table_name").where().eq("boolean_column", true);
        assertEquals("DELETE FROM table_name WHERE boolean_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { true }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.deleteFrom("table_name").where().eq("string_column", "value");
        assertEquals("DELETE FROM table_name WHERE string_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { "value" }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());
    }

    @Test
    public void insertQueries() {
        TestQueryBuilderImpl queryBuilder = new TestQueryBuilderImpl();

        queryBuilder.insertInto("table_name").row(r -> r.put("int_column", 2).put("boolean_column", true).build());
        assertEquals("INSERT INTO table_name (int_column, boolean_column) VALUES (?, ?)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2, true }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.insertInto("table_name").row(r -> r.put("boolean_column", true).put("int_column", 2).build());
        assertEquals("INSERT INTO table_name (boolean_column, int_column) VALUES (?, ?)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { true, 2 }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.insertInto("table_name").row(r -> r.put("boolean_value", true).put("null_column", null).build());
        assertEquals("INSERT INTO table_name (boolean_value, null_column) VALUES (?, NULL)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { true }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.insertInto("table_name").row(r -> r.put("string_column", "value").build());
        assertEquals("INSERT INTO table_name (string_column) VALUES (?)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { "value" }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.insertInto("table_name").values(ImmutableMap.of("int_column", 2, "boolean_column", true));
        assertEquals("INSERT INTO table_name (int_column, boolean_column) VALUES (?, ?)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2, true }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        HashMap<String, Object> values = new LinkedHashMap<>();
        values.put("int_column", 2);
        values.put("boolean_column", null);
        queryBuilder.insertInto("table_name").values(values);
        assertEquals("INSERT INTO table_name (int_column, boolean_column) VALUES (?, NULL)", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 2 }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

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
        assertTrue(queryBuilder.getLastWasUpdate());
    }

    @Test
    public void updateQueries() {
        TestQueryBuilderImpl queryBuilder = new TestQueryBuilderImpl();

        queryBuilder.update("table_name").set(r -> r.put("int_column", 2).put("boolean_column", true).build()).all();
        assertEquals("UPDATE table_name SET int_column = ?, boolean_column = ?", queryBuilder.getLastQuery());
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

        queryBuilder.update("table_name").set(ImmutableMap.of("int_column", 1)).where().eq("int_column", 2);
        assertEquals("UPDATE table_name SET int_column = ? WHERE int_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 1, 2 }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.update("table_name").set(ImmutableMap.of("int_column", 1)).where().eq("boolean_column", true);
        assertEquals("UPDATE table_name SET int_column = ? WHERE boolean_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 1, true }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());

        queryBuilder.update("table_name").set(ImmutableMap.of("int_column", 1)).where().eq("string_column", "value");
        assertEquals("UPDATE table_name SET int_column = ? WHERE string_column = ?", queryBuilder.getLastQuery());
        assertArrayEquals(new Object[] { 1, "value" }, queryBuilder.getLastParameters());
        assertTrue(queryBuilder.getLastWasUpdate());
    }
}
