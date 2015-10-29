package org.chodavarapu.datamill.db.impl;

import org.chodavarapu.datamill.db.RowBuilder;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class RowBuilderImplTest {
    @Test
    public void mapWithValues() {
        RowBuilder builder = new RowBuilderImpl();
        builder.put("booleanVal", true)
                .put("stringVal", "string")
                .put("integerVal", 1)
                .put("byteVal", (byte) 1)
                .put("longVal", 1L);

        Map<String, ?> values = builder.build();
        assertEquals(true, values.get("booleanVal"));
        assertEquals("string", values.get("stringVal"));
        assertEquals(1, values.get("integerVal"));
        assertEquals((byte) 1, values.get("byteVal"));
        assertEquals(1L, values.get("longVal"));
    }
}
