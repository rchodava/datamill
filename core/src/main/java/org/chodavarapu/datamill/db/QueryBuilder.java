package org.chodavarapu.datamill.db;

import org.chodavarapu.datamill.reflection.Outline;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface QueryBuilder extends SelectQueryBuilder {
    WhereBuilder<UpdateQueryExecution> deleteFrom(String table);
    WhereBuilder<UpdateQueryExecution> deleteFrom(Outline<?> outline);
    WhereBuilder<UpdateQueryExecution> deleteFromNamed(String table);
    WhereBuilder<UpdateQueryExecution> deleteFromNamed(Outline<?> outline);
    InsertBuilder insertInto(String table);
    InsertBuilder insertInto(Outline<?> outline);
    SelectBuilder selectAll();
    UpdateBuilder update(String table);
    UpdateBuilder update(Outline<?> outline);
}
