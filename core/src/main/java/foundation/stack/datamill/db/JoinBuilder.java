package foundation.stack.datamill.db;

import foundation.stack.datamill.reflection.Member;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface JoinBuilder<R> {
    SelectWhereBuilder<R> onEq(String column1, String column2);
    SelectWhereBuilder<R> onEq(String table1, String column1, String table2, String column2);
    SelectWhereBuilder<R> onEq(Member member1, Member member2);
    SelectWhereBuilder<R> onEq(String table1, String column1, Member member2);
}
