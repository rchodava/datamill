package foundation.stack.datamill;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface LimitBuilder<R> {
    R all();
    R limit(int count);
}
