package org.chodavarapu.datamill.db;

import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface RowBuilder {
    Map<String, ?> build();
    <T> RowBuilder put(String name, T value);
}
