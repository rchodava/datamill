package org.chodavarapu.datamill.db.impl;

import org.chodavarapu.datamill.reflection.Outline;
import org.chodavarapu.datamill.reflection.OutlineBuilder;
import org.junit.Test;

import java.sql.ResultSet;

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
        Outline<TestBean> outline = new OutlineBuilder().build(TestBean.class);

        ResultSet resultSet = mock(ResultSet.class);

        RowImpl row = new RowImpl(resultSet);

        when(resultSet.getByte("test_beans.property_name")).thenReturn((byte) 26);
        assertEquals(26, row.column(outline.member(m -> m.getPropertyName())).asByte());

        when(resultSet.getInt("test_beans.property_name")).thenReturn((int) 'a');
        assertEquals('a', row.column(outline.member(m -> m.getPropertyName())).asCharacter());

        when(resultSet.getDouble("test_beans.property_name")).thenReturn(1.23);
        assertEquals(1.23, row.column(outline.member(m -> m.getPropertyName())).asDouble(), 0.001);

        when(resultSet.getLong("test_beans.property_name")).thenReturn(156l);
        assertEquals(156l, row.column(outline.member(m -> m.getPropertyName())).asLong());

        when(resultSet.getInt("test_beans.property_name")).thenReturn(178);
        assertEquals(178, row.column(outline.member(m -> m.getPropertyName())).asInteger());

        when(resultSet.getFloat("test_beans.property_name")).thenReturn(8.012f);
        assertEquals(8.012f, row.column(outline.member(m -> m.getPropertyName())).asFloat(), 0.001);

        when(resultSet.getBoolean("test_beans.property_name")).thenReturn(true);
        assertEquals(true, row.column(outline.member(m -> m.getPropertyName())).asBoolean());

        when(resultSet.getShort("test_beans.property_name")).thenReturn((short) 17);
        assertEquals((short) 17, row.column(outline.member(m -> m.getPropertyName())).asShort());

        when(resultSet.getString("test_beans.property_name")).thenReturn("test");
        assertEquals("test", row.column(outline.member(m -> m.getPropertyName())).asString());
    }

    @Test
    public void indexedValueRetrieval() throws Exception {
        ResultSet resultSet = mock(ResultSet.class);

        RowImpl row = new RowImpl(resultSet);

        when(resultSet.getByte(13)).thenReturn((byte) 26);
        assertEquals(26, row.column(13).asByte());

        when(resultSet.getInt(14)).thenReturn((int) 'a');
        assertEquals('a', row.column(14).asCharacter());

        when(resultSet.getDouble(15)).thenReturn(1.23);
        assertEquals(1.23, row.column(15).asDouble(), 0.001);

        when(resultSet.getLong(16)).thenReturn(156l);
        assertEquals(156l, row.column(16).asLong());

        when(resultSet.getInt(17)).thenReturn(178);
        assertEquals(178, row.column(17).asInteger());

        when(resultSet.getFloat(18)).thenReturn(8.012f);
        assertEquals(8.012f, row.column(18).asFloat(), 0.001);

        when(resultSet.getBoolean(19)).thenReturn(true);
        assertEquals(true, row.column(19).asBoolean());

        when(resultSet.getShort(20)).thenReturn((short) 17);
        assertEquals((short) 17, row.column(20).asShort());

        when(resultSet.getString(21)).thenReturn("test");
        assertEquals("test", row.column(21).asString());
    }

    @Test
    public void labeledValueRetrieval() throws Exception {
        ResultSet resultSet = mock(ResultSet.class);

        RowImpl row = new RowImpl(resultSet);

        when(resultSet.getByte("label13")).thenReturn((byte) 26);
        assertEquals(26, row.column("label13").asByte());

        when(resultSet.getInt("label14")).thenReturn((int) 'a');
        assertEquals('a', row.column("label14").asCharacter());

        when(resultSet.getDouble("label15")).thenReturn(1.23);
        assertEquals(1.23, row.column("label15").asDouble(), 0.001);

        when(resultSet.getLong("label16")).thenReturn(156l);
        assertEquals(156l, row.column("label16").asLong());

        when(resultSet.getInt("label17")).thenReturn(178);
        assertEquals(178, row.column("label17").asInteger());

        when(resultSet.getFloat("label18")).thenReturn(8.012f);
        assertEquals(8.012f, row.column("label18").asFloat(), 0.001);

        when(resultSet.getBoolean("label19")).thenReturn(true);
        assertEquals(true, row.column("label19").asBoolean());

        when(resultSet.getShort("label20")).thenReturn((short) 17);
        assertEquals((short) 17, row.column("label20").asShort());

        when(resultSet.getString("label21")).thenReturn("test");
        assertEquals("test", row.column("label21").asString());
    }

    @Test
    public void qualifiedLabeledValueRetrieval() throws Exception {
        ResultSet resultSet = mock(ResultSet.class);

        RowImpl row = new RowImpl(resultSet);

        when(resultSet.getByte("table.label13")).thenReturn((byte) 26);
        assertEquals(26, row.column("table", "label13").asByte());

        when(resultSet.getInt("table.label14")).thenReturn((int) 'a');
        assertEquals('a', row.column("table", "label14").asCharacter());

        when(resultSet.getDouble("table.label15")).thenReturn(1.23);
        assertEquals(1.23, row.column("table", "label15").asDouble(), 0.001);

        when(resultSet.getLong("table.label16")).thenReturn(156l);
        assertEquals(156l, row.column("table", "label16").asLong());

        when(resultSet.getInt("table.label17")).thenReturn(178);
        assertEquals(178, row.column("table", "label17").asInteger());

        when(resultSet.getFloat("table.label18")).thenReturn(8.012f);
        assertEquals(8.012f, row.column("table", "label18").asFloat(), 0.001);

        when(resultSet.getBoolean("table.label19")).thenReturn(true);
        assertEquals(true, row.column("table", "label19").asBoolean());

        when(resultSet.getShort("table.label20")).thenReturn((short) 17);
        assertEquals((short) 17, row.column("table", "label20").asShort());

        when(resultSet.getString("table.label21")).thenReturn("test");
        assertEquals("test", row.column("table", "label21").asString());
    }
}
