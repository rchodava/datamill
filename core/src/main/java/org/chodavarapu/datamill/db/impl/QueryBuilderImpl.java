package org.chodavarapu.datamill.db.impl;

import com.google.common.base.Joiner;
import org.chodavarapu.datamill.db.QueryBuilder;
import org.chodavarapu.datamill.db.Row;
import org.chodavarapu.datamill.db.SelectBuilder;
import org.chodavarapu.datamill.db.WhereBuilder;
import rx.Observable;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class QueryBuilderImpl implements QueryBuilder {
    private static final String SQL_SELECT = "SELECT ";

    private static class SelectQuery implements SelectBuilder, WhereBuilder {
        private final StringBuilder query = new StringBuilder();

        public SelectQuery() {
            query.append(SQL_SELECT);
            query.append('*');
        }

        public SelectQuery(Iterator<String> columns) {
            query.append(SQL_SELECT);
            query.append(Joiner.on(',').join(columns));
            query.append(' ');
        }

        @Override
        public Observable<Row> all() {
            return null;
        }

        @Override
        public WhereBuilder from(String table) {
            return this;
        }
    }

    @Override
    public SelectBuilder select(String column) {
        return select(Arrays.asList(column));
    }

    @Override
    public SelectBuilder select(String... columns) {
        return select(Arrays.asList(columns));
    }

    @Override
    public SelectBuilder select(Iterable<String> columns) {
        return new SelectQuery();
    }

    @Override
    public SelectBuilder selectAll() {
        return new SelectQuery();
    }
}
