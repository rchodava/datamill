package foundation.stack.datamill.db;

import foundation.stack.datamill.LimitBuilder;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface SelectLimitBuilder<R> extends LimitBuilder<R> {
    R limit(int offset, int count);
}
