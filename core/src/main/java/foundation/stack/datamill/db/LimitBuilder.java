package foundation.stack.datamill.db;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface LimitBuilder<R> {
    R all();
    R limit(int count);
}
