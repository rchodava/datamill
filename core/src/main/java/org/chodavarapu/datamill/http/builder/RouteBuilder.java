package org.chodavarapu.datamill.http.builder;

import org.chodavarapu.datamill.http.Method;
import org.chodavarapu.datamill.http.Request;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.Route;
import org.chodavarapu.datamill.reflection.Bean;
import rx.Observable;

import java.util.function.BiFunction;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface RouteBuilder {
    ElseBuilder ifUriMatches(String pattern, Route route);
    ElseBuilder ifMethodMatches(Method method, Route route);
    ElseBuilder ifMethodAndUriMatch(Method method, String pattern, Route route);
    ElseBuilder ifMatchesBeanMethod(Bean bean, BiFunction<Request, Method, Observable<Response>> route);
}
