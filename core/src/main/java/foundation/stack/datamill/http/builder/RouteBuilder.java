package foundation.stack.datamill.http.builder;

import foundation.stack.datamill.http.Method;
import foundation.stack.datamill.http.Response;
import foundation.stack.datamill.http.Route;
import foundation.stack.datamill.http.ServerRequest;
import foundation.stack.datamill.reflection.Bean;
import rx.Observable;

import java.util.function.BiFunction;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface RouteBuilder {
    ElseBuilder ifUriMatches(String pattern, Route route);
    ElseBuilder ifMethodMatches(Method method, Route route);
    ElseBuilder ifMethodAndUriMatch(Method method, String pattern, Route route);
    <T> ElseBuilder ifMatchesBeanMethod(T bean);
    ElseBuilder ifMatchesBeanMethod(Bean<?> bean);
    ElseBuilder ifMatchesBeanMethod(
            Bean<?> bean,
            BiFunction<ServerRequest, foundation.stack.datamill.reflection.Method, Observable<Response>> route);
}
