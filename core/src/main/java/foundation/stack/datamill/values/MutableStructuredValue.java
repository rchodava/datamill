package foundation.stack.datamill.values;

import foundation.stack.datamill.reflection.Member;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface MutableStructuredValue extends StructuredValue {
    <T> MutableStructuredValue put(String name, T value);

    default <T> MutableStructuredValue put(Member member, T value) {
        return put(member.name(), value);
    }
}
