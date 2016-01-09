package org.chodavarapu.datamill.db;

import org.chodavarapu.datamill.reflection.Member;
import org.chodavarapu.datamill.reflection.Outline;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface QueryBuilder {
    WhereBuilder<UpdateQueryExecution> deleteFrom(String table);
    WhereBuilder<UpdateQueryExecution> deleteFrom(Outline<?> outline);
    InsertBuilder insertInto(String table);
    InsertBuilder insertInto(Outline<?> outline);
    SelectBuilder selectAll();
    SelectBuilder selectAllIn(Outline<?> outline);
    SelectBuilder select(String column);
    SelectBuilder select(Member member);
    SelectBuilder select(String... columns);
    SelectBuilder select(Member... members);
    SelectBuilder select(Iterable<String> columns);
    SelectBuilder selectQualified(String table, String column);
    SelectBuilder selectQualified(String table, String... columns);
    SelectBuilder selectQualified(String table, Iterable<String> columns);
    UpdateBuilder update(String table);
    UpdateBuilder update(Outline<?> outline);
}
