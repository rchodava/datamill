package org.chodavarapu.datamill.db;

import org.chodavarapu.datamill.values.Value;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Row {
    int size();
    Value column(int index);
    Value column(String name);
}
