package foundation.stack.datamill.reflection;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Property<T> extends Member {
    <P> P get(T instance);
    <P> void set(T instance, P value);

    Class<?> type();
    boolean isReadOnly();
    boolean isSimple();
}
