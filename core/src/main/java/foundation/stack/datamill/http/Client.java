package foundation.stack.datamill.http;

import com.google.common.collect.Multimap;
import foundation.stack.datamill.values.Value;
import rx.Observable;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Client {
    Observable<Response> request(Function<RequestBuilder, Request> builder);

    Observable<Response> request(Method method, Map<String, String> headers, String uri, Value entity);

    Observable<Response> request(Method method, Map<String, String> headers, String uri, Body body);

    Observable<Response> request(
            Method method,
            Multimap<String, String> headers,
            String uri,
            Map<String, String> uriParameters,
            Multimap<String, String> queryParameters,
            Map<String, ?> options,
            Body body);

    Observable<Response> delete(String uri);

    Observable<Response> delete(String uri, Map<String, String> headers);

    Observable<Response> delete(Function<RequestBuilder, Request> builder);

    Observable<Response> get(String uri);

    Observable<Response> get(String uri, Map<String, String> headers);

    Observable<Response> get(Function<RequestBuilder, Request> builder);

    Observable<Response> patch(String uri, Body body);

    Observable<Response> patch(String uri, Value entity);

    Observable<Response> patch(String uri, Map<String, String> headers, Body body);

    Observable<Response> patch(String uri, Map<String, String> headers, Value entity);

    Observable<Response> patch(Function<RequestBuilder, Request> builder);

    Observable<Response> post(String uri, Body body);

    Observable<Response> post(String uri, Value entity);

    Observable<Response> post(String uri, Map<String, String> headers, Body body);

    Observable<Response> post(String uri, Map<String, String> headers, Value entity);

    Observable<Response> post(Function<RequestBuilder, Request> builder);

    Observable<Response> put(String uri, Body body);

    Observable<Response> put(String uri, Value entity);

    Observable<Response> put(String uri, Map<String, String> headers, Body body);

    Observable<Response> put(String uri, Map<String, String> headers, Value entity);

    Observable<Response> put(Function<RequestBuilder, Request> builder);
}
