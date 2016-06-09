package foundation.stack.datamill.http;

import rx.functions.Func1;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface PostProcessedRoute extends Route {
    Route andFinally(Func1<Response, Response> postProcessor);
}
