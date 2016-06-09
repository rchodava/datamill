package foundation.stack.datamill.db;

import foundation.stack.datamill.reflection.Member;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface JoinBuilder<R> {
    WhereBuilder<R> onEq(String column1, String column2);
    WhereBuilder<R> onEq(String table1, String column1, String table2, String column2);
    WhereBuilder<R> onEq(Member member1, Member member2);
    WhereBuilder<R> onEq(String table1, String column1, Member member2);
}
