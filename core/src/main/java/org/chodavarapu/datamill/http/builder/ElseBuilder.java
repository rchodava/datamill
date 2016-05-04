package org.chodavarapu.datamill.http.builder;

import org.chodavarapu.datamill.http.*;
import org.chodavarapu.datamill.reflection.Bean;
import rx.Observable;

import java.util.function.BiFunction;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface ElseBuilder {
    ElseBuilder elseIfMethodMatches(Method method, Route route);
    ElseBuilder elseIfUriMatches(String pattern, Route route);
    ElseBuilder elseIfMethodAndUriMatch(Method method, String pattern, Route route);
    ElseBuilder elseIfMatchesBeanMethod(Bean<?> bean);
    ElseBuilder elseIfMatchesBeanMethod(
            Bean<?> bean,
            BiFunction<ServerRequest, org.chodavarapu.datamill.reflection.Method, Observable<Response>> route);
    PostProcessedRoute orElse(Route route);
    PostProcessedRoute orElse(Observable<Response> response);
    PostProcessedRoute orElse(Response response);
}
