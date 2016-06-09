package foundation.stack.datamill.values;

import java.time.LocalDateTime;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Value {
    boolean asBoolean();
    byte asByte();
    byte[] asByteArray();
    char asCharacter();
    double asDouble();
    float asFloat();
    int asInteger();
    LocalDateTime asLocalDateTime();
    Object asObject(Class<?> type);
    long asLong();
    short asShort();
    String asString();

    <T> T map(Function<Value, T> mapper);
}
