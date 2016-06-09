package foundation.stack.datamill.http.impl;

import foundation.stack.datamill.http.Method;
import foundation.stack.datamill.http.ServerRequest;
import foundation.stack.datamill.http.Response;
import rx.Observable;

import java.util.Set;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Matcher {
    Observable<Response> applyIfMatches(ServerRequest request);
    Set<Method> queryOptions(ServerRequest request);
}
