package org.chodavarapu.datamill.http;

import org.chodavarapu.datamill.http.impl.RequestBuilderImpl;
import org.chodavarapu.datamill.http.impl.ResponseImpl;
import org.chodavarapu.datamill.values.Value;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.util.async.Async;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Client {
    public Observable<Response> request(Function<RequestBuilder, Request> builder) {
        Request request = builder.apply(new RequestBuilderImpl());
        return request(request.method(), request.headers(), request.uri(), null);
    }

    public Observable<Response> request(Method method, Map<String, String> headers, String uri, Value entity) {
        return Async.fromCallable(() -> {
            URLConnection urlConnection = new URL(uri).openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;

            httpConnection.setRequestMethod(method.toString());

            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    httpConnection.addRequestProperty(header.getKey(), header.getValue());
                }
            }

            int responseCode = httpConnection.getResponseCode();
            httpConnection.getInputStream();

            return new ResponseImpl(Status.valueOf(responseCode), null);
        }, Schedulers.io());
    }

    public Observable<Response> get(String uri) {
        return get(uri, null);
    }

    public Observable<Response> get(String uri, Map<String, String> headers) {
        return request(Method.GET, headers, uri, null);
    }


    public Observable<Response> get(Function<RequestBuilder, Request> builder) {
        return request(requestBuilder -> builder.apply(requestBuilder.method(Method.GET)));
    }

    public Observable<Response> post(String uri, Value entity) {
        return post(uri, null, entity);
    }

    public Observable<Response> post(String uri, Map<String, String> headers, Value entity) {
        return request(Method.POST, headers, uri, entity);
    }

    public Observable<Response> post(Function<RequestBuilder, Request> builder) {
        return request(requestBuilder -> builder.apply(requestBuilder.method(Method.POST)));
    }

    public Observable<Response> put(String uri, Value entity) {
        return put(uri, null, entity);
    }

    public Observable<Response> put(String uri, Map<String, String> headers, Value entity) {
        return request(Method.PUT, headers, uri, entity);
    }

    public Observable<Response> put(Function<RequestBuilder, Request> builder) {
        return request(requestBuilder -> builder.apply(requestBuilder.method(Method.PUT)));
    }
}
