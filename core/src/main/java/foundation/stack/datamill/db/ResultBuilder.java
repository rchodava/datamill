package foundation.stack.datamill.db;

import rx.Observable;
import rx.functions.Func1;

import java.util.List;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface ResultBuilder {
    <T> Observable<List<T>> getAs(Func1<Row, T> transformer);
    <T> Observable<T> firstAs(Func1<Row, T> transformer);
    Observable<Row> stream();
}
