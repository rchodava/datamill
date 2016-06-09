package foundation.stack.datamill.http;

import rx.Observable;

import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Route extends Function<ServerRequest, Observable<Response>> {
}
