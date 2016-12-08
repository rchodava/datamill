package foundation.stack.datamill.db.impl;

import foundation.stack.datamill.values.Times;
import foundation.stack.datamill.reflection.Outline;
import foundation.stack.datamill.reflection.OutlineBuilder;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class RowImplTest {
    private static class TestBean {
        public int getPropertyName() {
            return -1;
        }
    }

    @Test
    public void memberNameBasedValueRetrieval() throws Exception {
        Outline<TestBean> outline = OutlineBuilder.DEFAULT.build(TestBean.class);

        ResultSet resultSet = mock(ResultSet.class);

        RowImpl row = new RowImpl(resultSet);

        when(resultSet.getByte("test_beans.property_name")).thenReturn((byte) 26);
        assertEquals(26, row.get(outline.member(m -> m.getPropertyName())).asByte());

        when(resultSet.getBytes("test_beans.property_name")).thenReturn("test".getBytes());
        assertArrayEquals("test".getBytes(), row.get(outline.member(m -> m.getPropertyName())).asByteArray());

        when(resultSet.getInt("test_beans.property_name")).thenReturn((int) 'a');
        assertEquals('a', row.get(outline.member(m -> m.getPropertyName())).asCharacter());

        when(resultSet.getDouble("test_beans.property_name")).thenReturn(1.23);
        assertEquals(1.23, row.get(outline.member(m -> m.getPropertyName())).asDouble(), 0.001);

        LocalDateTime now = LocalDateTime.now();
        when(resultSet.getTimestamp("test_beans.property_name")).thenReturn(new Timestamp(Times.toEpochMillis(now)));
        assertEquals(now, row.get(outline.member(m -> m.getPropertyName())).asLocalDateTime());

        when(resultSet.getLong("test_beans.property_name")).thenReturn(156l);
        assertEquals(156l, row.get(outline.member(m -> m.getPropertyName())).asLong());

        when(resultSet.getInt("test_beans.property_name")).thenReturn(178);
        assertEquals(178, row.get(outline.member(m -> m.getPropertyName())).asInteger());

        when(resultSet.getFloat("test_beans.property_name")).thenReturn(8.012f);
        assertEquals(8.012f, row.get(outline.member(m -> m.getPropertyName())).asFloat(), 0.001);

        when(resultSet.getBoolean("test_beans.property_name")).thenReturn(true);
        assertEquals(true, row.get(outline.member(m -> m.getPropertyName())).asBoolean());

        when(resultSet.getShort("test_beans.property_name")).thenReturn((short) 17);
        assertEquals((short) 17, row.get(outline.member(m -> m.getPropertyName())).asShort());

        when(resultSet.getString("test_beans.property_name")).thenReturn("test");
        assertEquals("test", row.get(outline.member(m -> m.getPropertyName())).asString());
    }

    @Test
    public void indexedValueRetrieval() throws Exception {
        ResultSet resultSet = mock(ResultSet.class);

        RowImpl row = new RowImpl(resultSet);

        when(resultSet.getByte(13)).thenReturn((byte) 26);
        assertEquals(26, row.get(13).asByte());

        when(resultSet.getBytes(21)).thenReturn("test".getBytes());
        assertArrayEquals("test".getBytes(), row.get(21).asByteArray());

        when(resultSet.getInt(14)).thenReturn((int) 'a');
        assertEquals('a', row.get(14).asCharacter());

        when(resultSet.getDouble(15)).thenReturn(1.23);
        assertEquals(1.23, row.get(15).asDouble(), 0.001);

        LocalDateTime now = LocalDateTime.now();
        when(resultSet.getTimestamp(15)).thenReturn(new Timestamp(Times.toEpochMillis(now)));
        assertEquals(now, row.get(15).asLocalDateTime());

        when(resultSet.getLong(16)).thenReturn(156l);
        assertEquals(156l, row.get(16).asLong());

        when(resultSet.getInt(17)).thenReturn(178);
        assertEquals(178, row.get(17).asInteger());

        when(resultSet.getFloat(18)).thenReturn(8.012f);
        assertEquals(8.012f, row.get(18).asFloat(), 0.001);

        when(resultSet.getBoolean(19)).thenReturn(true);
        assertEquals(true, row.get(19).asBoolean());

        when(resultSet.getShort(20)).thenReturn((short) 17);
        assertEquals((short) 17, row.get(20).asShort());

        when(resultSet.getString(21)).thenReturn("test");
        assertEquals("test", row.get(21).asString());
    }

    @Test
    public void labeledValueRetrieval() throws Exception {
        ResultSet resultSet = mock(ResultSet.class);

        RowImpl row = new RowImpl(resultSet);

        when(resultSet.getByte("label13")).thenReturn((byte) 26);
        assertEquals(26, row.get("label13").asByte());

        when(resultSet.getBytes("label21")).thenReturn("test".getBytes());
        assertArrayEquals("test".getBytes(), row.get("label21").asByteArray());

        when(resultSet.getInt("label14")).thenReturn((int) 'a');
        assertEquals('a', row.get("label14").asCharacter());

        when(resultSet.getDouble("label15")).thenReturn(1.23);
        assertEquals(1.23, row.get("label15").asDouble(), 0.001);

        LocalDateTime now = LocalDateTime.now();
        when(resultSet.getTimestamp("label15")).thenReturn(new Timestamp(Times.toEpochMillis(now)));
        assertEquals(now, row.get("label15").asLocalDateTime());

        when(resultSet.getLong("label16")).thenReturn(156l);
        assertEquals(156l, row.get("label16").asLong());

        when(resultSet.getInt("label17")).thenReturn(178);
        assertEquals(178, row.get("label17").asInteger());

        when(resultSet.getFloat("label18")).thenReturn(8.012f);
        assertEquals(8.012f, row.get("label18").asFloat(), 0.001);

        when(resultSet.getBoolean("label19")).thenReturn(true);
        assertEquals(true, row.get("label19").asBoolean());

        when(resultSet.getShort("label20")).thenReturn((short) 17);
        assertEquals((short) 17, row.get("label20").asShort());

        when(resultSet.getString("label21")).thenReturn("test");
        assertEquals("test", row.get("label21").asString());
    }

    @Test
    public void qualifiedLabeledValueRetrieval() throws Exception {
        ResultSet resultSet = mock(ResultSet.class);

        RowImpl row = new RowImpl(resultSet);

        when(resultSet.getByte("table.label13")).thenReturn((byte) 26);
        assertEquals(26, row.get("table", "label13").asByte());

        when(resultSet.getBytes("table.label21")).thenReturn("test".getBytes());
        assertArrayEquals("test".getBytes(), row.get("table", "label21").asByteArray());

        when(resultSet.getInt("table.label14")).thenReturn((int) 'a');
        assertEquals('a', row.get("table", "label14").asCharacter());

        when(resultSet.getDouble("table.label15")).thenReturn(1.23);
        assertEquals(1.23, row.get("table", "label15").asDouble(), 0.001);

        LocalDateTime now = LocalDateTime.now();
        when(resultSet.getTimestamp("table.label15")).thenReturn(new Timestamp(Times.toEpochMillis(now)));
        assertEquals(now, row.get("table", "label15").asLocalDateTime());

        when(resultSet.getLong("table.label16")).thenReturn(156l);
        assertEquals(156l, row.get("table", "label16").asLong());

        when(resultSet.getInt("table.label17")).thenReturn(178);
        assertEquals(178, row.get("table", "label17").asInteger());

        when(resultSet.getFloat("table.label18")).thenReturn(8.012f);
        assertEquals(8.012f, row.get("table", "label18").asFloat(), 0.001);

        when(resultSet.getBoolean("table.label19")).thenReturn(true);
        assertEquals(true, row.get("table", "label19").asBoolean());

        when(resultSet.getShort("table.label20")).thenReturn((short) 17);
        assertEquals((short) 17, row.get("table", "label20").asShort());

        when(resultSet.getString("table.label21")).thenReturn("test");
        assertEquals("test", row.get("table", "label21").asString());
    }
}
