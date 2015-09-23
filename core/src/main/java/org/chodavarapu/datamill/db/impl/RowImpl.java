package org.chodavarapu.datamill.db.impl;

import org.chodavarapu.datamill.Value;
import org.chodavarapu.datamill.db.DatabaseException;
import org.chodavarapu.datamill.db.Row;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class RowImpl implements Row {
    private final ResultSet resultSet;

    private abstract class IndexedColumnValue implements Value {
        private final int index;
        public IndexedColumnValue(int index) {
            this.index = index;
        }

        @Override
        public long asLong() {
            try {
                return resultSet.getLong(index);
            } catch (SQLException e) {
                throw new DatabaseException(e);
            }
        }

        @Override
        public <T> T map(Function<? extends Value, T> mapper) {
            return null;
        }
    }

    public RowImpl(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public Value column(int index) {
        return null;
    }

    @Override
    public Value column(String name) {
        return null;
    }

    @Override
    public int size() {
        try {
            return resultSet.getMetaData().getColumnCount();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
