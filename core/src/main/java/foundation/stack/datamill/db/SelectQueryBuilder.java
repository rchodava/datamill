package foundation.stack.datamill.db;

import foundation.stack.datamill.reflection.Member;
import foundation.stack.datamill.reflection.Outline;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface SelectQueryBuilder {
    SelectBuilder selectAllIn(Outline<?> outline);

    SelectBuilder select(String column);

    SelectBuilder select(Member member);

    SelectBuilder select(String... columns);

    SelectBuilder select(Member... members);

    SelectBuilder select(Iterable<String> columns);

    SelectBuilder selectQualified(String table, String column);

    SelectBuilder selectQualified(String table, String... columns);

    SelectBuilder selectQualified(String table, Iterable<String> columns);
}
