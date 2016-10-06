package foundation.stack.datamill.db;

import foundation.stack.datamill.reflection.Outline;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface SelectBuilder {
    SelectWhereBuilder<ResultBuilder> from(String table);
    SelectWhereBuilder<ResultBuilder> from(Outline<?> outline);
}
