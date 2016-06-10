package foundation.stack.datamill.db.impl;

import foundation.stack.datamill.reflection.Member;
import foundation.stack.datamill.values.Value;
import foundation.stack.datamill.db.DatabaseException;
import foundation.stack.datamill.db.Row;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class RowImpl implements Row {
    private final ResultSet resultSet;

    public RowImpl(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public Value column(int index) {
        return new IndexedColumnValue(index);
    }

    @Override
    public Value column(String name) {
        return new LabeledColumnValue(name);
    }

    @Override
    public Value column(String table, String name) {
        return new LabeledColumnValue(table + "." + name);
    }

    @Override
    public Value column(Member member) {
        return column(member.outline().pluralName(), member.name());
    }

    @Override
    public int size() {
        try {
            return resultSet.getMetaData().getColumnCount();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @FunctionalInterface
    private interface ResultSetValueRetriever<K, R> {
        R retrieve(K key) throws SQLException;
    }

    private abstract class KeyedColumnValue<K> implements Value {
        protected final K key;

        public KeyedColumnValue(K key) {
            this.key = key;
        }

        protected <T> T safeRetrieve(ResultSetValueRetriever<K, T> retriever) {
            try {
                return retriever.retrieve(key);
            } catch (SQLException e) {
                throw new DatabaseException(e);
            }
        }

        @Override
        public <T> T map(Function<Value, T> mapper) {
            return mapper.apply(this);
        }
    }

    private class IndexedColumnValue extends KeyedColumnValue<Integer> {
        public IndexedColumnValue(int index) {
            super(index);
        }

        @Override
        public byte asByte() {
            return safeRetrieve(k -> resultSet.getByte(key));
        }

        @Override
        public byte[] asByteArray() {
            return safeRetrieve(k -> resultSet.getBytes(key));
        }

        @Override
        public char asCharacter() {
            return safeRetrieve(k -> (char) resultSet.getInt(key));
        }

        @Override
        public double asDouble() {
            return safeRetrieve(k -> resultSet.getDouble(key));
        }

        @Override
        public LocalDateTime asLocalDateTime() {
            return safeRetrieve(k -> resultSet.getTimestamp(key).toLocalDateTime());
        }

        @Override
        public long asLong() {
            return safeRetrieve(k -> resultSet.getLong(key));
        }

        @Override
        public int asInteger() {
            return safeRetrieve(k -> resultSet.getInt(key));
        }

        @Override
        public float asFloat() {
            return safeRetrieve(k -> resultSet.getFloat(key));
        }

        @Override
        public boolean asBoolean() {
            return safeRetrieve(k -> resultSet.getBoolean(key));
        }

        @Override
        public Object asObject(Class<?> type) {
            return safeRetrieve(k -> resultSet.getObject(key));
        }

        @Override
        public short asShort() {
            return safeRetrieve(k -> resultSet.getShort(key));
        }

        @Override
        public String asString() {
            return safeRetrieve(k -> resultSet.getString(key));
        }
    }

    private class LabeledColumnValue extends KeyedColumnValue<String> {
        public LabeledColumnValue(String label) {
            super(label);
        }

        @Override
        public byte asByte() {
            return safeRetrieve(k -> resultSet.getByte(key));
        }

        @Override
        public byte[] asByteArray() {
            return safeRetrieve(k -> resultSet.getBytes(key));
        }

        @Override
        public char asCharacter() {
            return safeRetrieve(k -> (char) resultSet.getInt(key));
        }

        @Override
        public double asDouble() {
            return safeRetrieve(k -> resultSet.getDouble(key));
        }

        @Override
        public LocalDateTime asLocalDateTime() {
            return safeRetrieve(k -> {
                Timestamp timestap = resultSet.getTimestamp(key);
                if (timestap != null) {
                    return timestap.toLocalDateTime();
                }

                return null;
            });
        }

        @Override
        public long asLong() {
            return safeRetrieve(k -> resultSet.getLong(key));
        }

        @Override
        public int asInteger() {
            return safeRetrieve(k -> resultSet.getInt(key));
        }

        @Override
        public float asFloat() {
            return safeRetrieve(k -> resultSet.getFloat(key));
        }

        @Override
        public boolean asBoolean() {
            return safeRetrieve(k -> resultSet.getBoolean(key));
        }

        @Override
        public Object asObject(Class<?> type) {
            return safeRetrieve(k -> resultSet.getObject(key));
        }

        @Override
        public short asShort() {
            return safeRetrieve(k -> resultSet.getShort(key));
        }

        @Override
        public String asString() {
            return safeRetrieve(k -> resultSet.getString(key));
        }
    }
}
