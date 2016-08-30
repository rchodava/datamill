package foundation.stack.datamill.db.impl;

import foundation.stack.datamill.db.InsertSuffixBuilder;
import foundation.stack.datamill.db.RowBuilder;
import foundation.stack.datamill.db.UpdateQueryExecution;
import rx.Observable;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
class EmptyUpdateSuffixBuilder implements InsertSuffixBuilder {
    @Override
    public Observable<Integer> count() {
        return Observable.just(0);
    }

    @Override
    public Observable<Long> getIds() {
        return Observable.empty();
    }

    @Override
    public UpdateQueryExecution onDuplicateKeyUpdate(Function<RowBuilder, Map<String, ?>> rowConstructor) {
        return this;
    }

    @Override
    public UpdateQueryExecution onDuplicateKeyUpdate(Map<String, ?> values) {
        return this;
    }
}
