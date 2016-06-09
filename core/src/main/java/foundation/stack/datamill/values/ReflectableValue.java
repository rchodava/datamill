package foundation.stack.datamill.values;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface ReflectableValue extends Value {
    boolean isBoolean();
    boolean isByte();
    boolean isCharacter();
    boolean isDouble();
    boolean isFloat();
    boolean isInteger();
    boolean isLong();
    boolean isNumeric();
    boolean isShort();
    boolean isString();
}
