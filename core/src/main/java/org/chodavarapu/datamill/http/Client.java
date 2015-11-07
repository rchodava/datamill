package org.chodavarapu.datamill.http;

import com.google.common.base.Joiner;
import org.chodavarapu.datamill.http.impl.InputStreamEntity;
import org.chodavarapu.datamill.http.impl.RequestBuilderImpl;
import org.chodavarapu.datamill.http.impl.ResponseImpl;
import org.chodavarapu.datamill.http.impl.ValueEntity;
import org.chodavarapu.datamill.values.Value;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.util.async.Async;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Client {
    public Observable<Response> request(Function<RequestBuilder, Request> builder) {
        Request request = builder.apply(new RequestBuilderImpl());
        return request(request.method(), request.headers(), request.uri(), request.entity());
    }

    public Observable<Response> request(Method method, Map<String, String> headers, String uri, Value entity) {
        return request(method, headers, uri, new ValueEntity(entity));
    }

    protected URLConnection createConnection(String uri) throws IOException {
        return new URL(uri).openConnection();
    }

    public Observable<Response> request(Method method, Map<String, String> headers, String uri, Entity entity) {
        return Async.fromCallable(() -> {
            URLConnection urlConnection = createConnection(uri);
            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;

            httpConnection.setRequestMethod(method.toString());

            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    httpConnection.addRequestProperty(header.getKey(), header.getValue());
                }
            }

            if (entity != null) {
                writeEntityOutOverConnection(entity, httpConnection);
            }

            int responseCode = httpConnection.getResponseCode();
            InputStream inputStream = httpConnection.getInputStream();

            Map<String, List<String>> responseHeaders = httpConnection.getHeaderFields();
            Map<String, String> combinedHeaders = new HashMap<>();
            for (Map.Entry<String, List<String>> header : responseHeaders.entrySet()) {
                if (header.getValue().size() > 1) {
                    combinedHeaders.put(header.getKey(), Joiner.on(',').join(header.getValue()));
                } else {
                    combinedHeaders.put(header.getKey(), header.getValue().get(0));
                }
            }

            return new ResponseImpl(Status.valueOf(responseCode), combinedHeaders, new InputStreamEntity(inputStream));
        }, Schedulers.io());
    }

    private void writeEntityOutOverConnection(Entity entity, HttpURLConnection httpConnection) throws IOException {
        httpConnection.setDoOutput(true);
        OutputStream outputStream = httpConnection.getOutputStream();
        entity.asChunks().observeOn(Schedulers.io())
                .doOnNext(bytes -> {
                    try {
                        outputStream.write(bytes);
                    } catch (IOException e) {
                        throw new HttpException("Error writing entity!", e);
                    }
                })
                .doOnCompleted(() -> {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        throw new HttpException("Error while closing stream!", e);
                    }
                })
                .doOnError(e -> {
                    try {
                        outputStream.close();
                    } catch (IOException closing) {
                        throw new HttpException("Error while closing stream due to an original exception!", e);
                    }

                    throw new HttpException(e);
                })
                .subscribe();
    }

    public Observable<Response> get(String uri) {
        return get(uri, null);
    }

    public Observable<Response> get(String uri, Map<String, String> headers) {
        return request(Method.GET, headers, uri, (Entity) null);
    }


    public Observable<Response> get(Function<RequestBuilder, Request> builder) {
        return request(requestBuilder -> builder.apply(requestBuilder.method(Method.GET)));
    }

    public Observable<Response> post(String uri, Entity entity) {
        return post(uri, null, entity);
    }

    public Observable<Response> post(String uri, Value entity) {
        return post(uri, null, entity);
    }

    public Observable<Response> post(String uri, Map<String, String> headers, Entity entity) {
        return request(Method.POST, headers, uri, entity);
    }

    public Observable<Response> post(String uri, Map<String, String> headers, Value entity) {
        return request(Method.POST, headers, uri, entity);
    }

    public Observable<Response> post(Function<RequestBuilder, Request> builder) {
        return request(requestBuilder -> builder.apply(requestBuilder.method(Method.POST)));
    }

    public Observable<Response> put(String uri, Entity entity) {
        return put(uri, null, entity);
    }

    public Observable<Response> put(String uri, Value entity) {
        return put(uri, null, entity);
    }

    public Observable<Response> put(String uri, Map<String, String> headers, Entity entity) {
        return request(Method.PUT, headers, uri, entity);
    }

    public Observable<Response> put(String uri, Map<String, String> headers, Value entity) {
        return request(Method.PUT, headers, uri, entity);
    }

    public Observable<Response> put(Function<RequestBuilder, Request> builder) {
        return request(requestBuilder -> builder.apply(requestBuilder.method(Method.PUT)));
    }
}
