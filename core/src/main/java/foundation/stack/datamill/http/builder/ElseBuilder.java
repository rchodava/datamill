package foundation.stack.datamill.http.builder;

import foundation.stack.datamill.http.PostProcessedRoute;
import foundation.stack.datamill.http.Response;
import foundation.stack.datamill.http.Route;
import foundation.stack.datamill.http.ServerRequest;
import foundation.stack.datamill.reflection.Method;
import foundation.stack.datamill.reflection.Bean;
import rx.Observable;

import java.util.function.BiFunction;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface ElseBuilder {
    ElseBuilder elseIfMethodMatches(foundation.stack.datamill.http.Method method, Route route);
    ElseBuilder elseIfUriMatches(String pattern, Route route);
    ElseBuilder elseIfMethodAndUriMatch(foundation.stack.datamill.http.Method method, String pattern, Route route);
    ElseBuilder elseIfMatchesBeanMethod(Bean<?> bean);
    ElseBuilder elseIfMatchesBeanMethod(
            Bean<?> bean,
            BiFunction<ServerRequest, Method, Observable<Response>> route);
    PostProcessedRoute orElse(Route route);
    PostProcessedRoute orElse(Observable<Response> response);
    PostProcessedRoute orElse(Response response);
}
