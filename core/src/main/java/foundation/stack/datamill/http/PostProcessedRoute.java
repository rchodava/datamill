package foundation.stack.datamill.http;

import rx.functions.Func2;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface PostProcessedRoute extends Route {
    Route andFinally(Func2<Request, Response, Response> postProcessor);
}
