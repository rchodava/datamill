package foundation.stack.datamill.db;

import foundation.stack.datamill.reflection.Outline;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface QueryBuilder extends SelectQueryBuilder {
    WhereBuilder<UpdateQueryExecution, ? extends LimitBuilder<? extends UpdateQueryExecution>> deleteFrom(String table);
    WhereBuilder<UpdateQueryExecution, ? extends LimitBuilder<? extends UpdateQueryExecution>> deleteFrom(
            Outline<?> outline);
    WhereBuilder<UpdateQueryExecution, ? extends LimitBuilder<? extends UpdateQueryExecution>> deleteFromNamed(
            String table);
    WhereBuilder<UpdateQueryExecution, ? extends LimitBuilder<? extends UpdateQueryExecution>> deleteFromNamed(
            Outline<?> outline);
    InsertBuilder insertInto(String table);
    InsertBuilder insertInto(Outline<?> outline);
    SelectBuilder selectAll();
    UpdateBuilder update(String table);
    UpdateBuilder update(Outline<?> outline);
}
