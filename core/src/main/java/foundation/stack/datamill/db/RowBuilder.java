package foundation.stack.datamill.db;

import foundation.stack.datamill.reflection.Member;

import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface RowBuilder {
    Map<String, ?> build();
    <T> RowBuilder put(String name, T value);
    <T> RowBuilder put(Member member, T value);
}
