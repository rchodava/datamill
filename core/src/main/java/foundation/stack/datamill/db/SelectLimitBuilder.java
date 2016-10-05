package foundation.stack.datamill.db;

import foundation.stack.datamill.reflection.Member;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface SelectLimitBuilder<R> extends LimitBuilder<R> {
    R limit(int offset, int count);
    OrderBuilder<R> orderBy(String fragment);
    OrderBuilder<R> orderBy(Member member);
}
