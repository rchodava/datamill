package org.chodavarapu.datamill.http.builder;

import org.chodavarapu.datamill.http.Method;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.Route;
import rx.Observable;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface ElseBuilder {
    ElseBuilder elseIfMethodMatches(Method method, Route route);
    ElseBuilder elseIfUriMatches(String pattern, Route route);
    ElseBuilder elseIfMethodAndUriMatch(Method method, String pattern, Route route);
    Route orElse(Route route);
    Route orElse(Observable<Response> response);
    Route orElse(Response response);
}
