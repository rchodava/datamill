package org.chodavarapu.datamill.db;

import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Connection {
    <T> T doWithinTransaction(Function<Connection, T> block);
    void query(String sql);
}
