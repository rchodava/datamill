package foundation.stack.datamill.db;

import foundation.stack.datamill.reflection.Outline;
import rx.Observable;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface SelectBuilder {
    SelectWhereBuilder<Observable<Row>> from(String table);
    SelectWhereBuilder<Observable<Row>> from(Outline<?> outline);
}
