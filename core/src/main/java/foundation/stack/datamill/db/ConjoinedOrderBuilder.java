package foundation.stack.datamill.db;

import foundation.stack.datamill.reflection.Member;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface ConjoinedOrderBuilder<R> {
    R all();
    R limit(int count);
    R limit(int offset, int count);
    OrderBuilder<R> andOrderBy(String fragment);
    OrderBuilder<R> andOrderBy(Member member);
}
