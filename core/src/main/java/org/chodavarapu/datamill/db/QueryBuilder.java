package org.chodavarapu.datamill.db;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface QueryBuilder {
    InsertBuilder insertInto(String table);
    SelectBuilder selectAll();
    SelectBuilder select(String column);
    SelectBuilder select(String... columns);
    SelectBuilder select(Iterable<String> columns);
}
