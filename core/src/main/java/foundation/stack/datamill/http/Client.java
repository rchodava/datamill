package foundation.stack.datamill.http;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import foundation.stack.datamill.http.impl.InputStreamBody;
import foundation.stack.datamill.http.impl.ValueBody;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import foundation.stack.datamill.http.impl.EmptyBody;
import foundation.stack.datamill.http.impl.RequestBuilderImpl;
import foundation.stack.datamill.http.impl.ResponseImpl;
import foundation.stack.datamill.http.impl.TemplateBasedUriBuilder;
import foundation.stack.datamill.values.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private final TemplateBasedUriBuilder templateBasedUriBuilder = new TemplateBasedUriBuilder();

    public Observable<Response> request(Function<RequestBuilder, Request> builder) {
        Request request = builder.apply(new RequestBuilderImpl());
        return request(request.method(), request.headers(), request.uri(), request.uriParameters(),
                request.queryParameters(), request.options(), request.body());
    }

    public Observable<Response> request(Method method, Map<String, String> headers, String uri, Value entity) {
        return request(method, headers, uri, new ValueBody(entity));
    }

    public Observable<Response> request(Method method, Map<String, String> headers, String uri, Body body) {
        return request(method, headers != null ? Multimaps.forMap(headers) : null, uri, null, null, null, body);
    }

    public Observable<Response> request(
            Method method,
            Multimap<String, String> headers,
            String uri,
            Map<String, String> uriParameters,
            Multimap<String, String> queryParameters,
            Map<String, ?> options,
            Body body) {

        if (uriParameters != null && !uriParameters.isEmpty()) {
            uri = templateBasedUriBuilder.build(uri, uriParameters);
        }

        URI parsedURI;
        try {
            URIBuilder uriBuilder = new URIBuilder(uri);
            parsedURI = appendQueryParameters(uriBuilder, queryParameters);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Could not build URI for " + uri);
        }

        final URI targetURI = parsedURI;

        return Observable.fromCallable(() -> {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpUriRequest request = buildHttpRequest(method, targetURI);
            setRequestOptions(request, options);
            setRequestHeaders(request, headers);
            printRequestIfDebugging(method, targetURI, headers);

            CloseableHttpResponse httpResponse;
            if (body != null) {
                httpResponse = doWithEntity(body, httpClient, request);
            } else {
                httpResponse = doExecute(httpClient, request);
            }

            CloseableHttpResponse finalResponse = httpResponse;

            Map<String, String> combinedHeaders = populateResponseHeaders(finalResponse);
            int responseCode = finalResponse.getStatusLine().getStatusCode();
            return new ResponseImpl(Status.valueOf(responseCode), combinedHeaders,
                    buildResponseEntity(finalResponse, httpClient));
        });
    }

    private Body buildResponseEntity(CloseableHttpResponse finalResponse, CloseableHttpClient httpClient) throws IOException {
        if (finalResponse != null && finalResponse.getEntity() != null) {
            return new InputStreamBody(finalResponse.getEntity().getContent(), () -> {
                try {
                    finalResponse.close();
                } catch (IOException e) {
                    logger.debug("Error while closing response stream!", e);
                }

                if (httpClient != null) {
                    try {
                        httpClient.close();
                    } catch (IOException e) {
                        logger.debug("Error while closing client!", e);
                    }
                }
            });
        }
        else {
            return new EmptyBody();
        }
    }

    private CloseableHttpResponse doWithEntity(Body body, CloseableHttpClient httpClient, HttpUriRequest request) throws IOException {
        if (!(request instanceof HttpEntityEnclosingRequestBase)) {
            throw new IllegalArgumentException("Expecting to write an body for a request type that does not support it!");
        }

        PipedOutputStream pipedOutputStream = buildPipedOutputStream();
        PipedInputStream pipedInputStream = buildPipedInputStream();

        pipedInputStream.connect(pipedOutputStream);

        BasicHttpEntity httpEntity = new BasicHttpEntity();
        httpEntity.setContent(pipedInputStream);
        ((HttpEntityEnclosingRequestBase) request).setEntity(httpEntity);

        writeEntityOutOverConnection(body, pipedOutputStream);

        return doExecute(httpClient, request);
    }


    protected CloseableHttpResponse doExecute(CloseableHttpClient httpClient, HttpUriRequest request) throws IOException {
        return httpClient.execute(request);
    }

    private void printRequestIfDebugging(Method method, URI composedUri, Multimap<String, String> headers) {
        if (logger.isDebugEnabled()) {
            logger.debug("Making HTTP request {} {}", method.name(), composedUri);
            if (headers != null && logger.isDebugEnabled()) {
                logger.debug("  HTTP request headers:");
                for (Map.Entry<String, String> header : headers.entries()) {
                    logger.debug("    {}: {}", header.getKey(), header.getValue());
                }
            }
        }
    }

    private Map<String, String> populateResponseHeaders(CloseableHttpResponse httpResponse) {
        Map<String, String> combinedHeaders = new HashMap<>();

        for (Header header : httpResponse.getAllHeaders()) {
            combinedHeaders.put(header.getName(), header.getValue());
        }

        return combinedHeaders;
    }

    private void setRequestHeaders(HttpUriRequest request, Multimap<String, String> headers) {
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entries()) {
                request.addHeader(header.getKey().toLowerCase(), header.getValue());
            }
        }
    }

    private void setRequestOptions(HttpUriRequest request, Map<String, ?> options) {
        if (options != null && !options.isEmpty()) {
            Object connectTimeout = options.get(Request.OPTION_CONNECT_TIMEOUT);
            if (connectTimeout instanceof Integer) {
                RequestConfig requestConfig = RequestConfig.custom()
                        .setConnectTimeout((int) connectTimeout)
                        .build();
                ((HttpRequestBase) request).setConfig(requestConfig);
            }
        }
    }

    protected PipedOutputStream buildPipedOutputStream() {
        return new PipedOutputStream();
    }

    protected PipedInputStream buildPipedInputStream() {
        return new PipedInputStream();
    }

    protected HttpUriRequest buildHttpRequest(Method method, URI uri) {
        switch (method) {
            case OPTIONS:
                return new HttpOptions(uri);
            case GET:
                return new HttpGet(uri);
            case HEAD:
                return new HttpHead(uri);
            case POST:
                return new HttpPost(uri);
            case PUT:
                return new HttpPut(uri);
            case DELETE:
                return new HttpDelete(uri);
            case TRACE:
                return new HttpTrace(uri);
            case PATCH:
                return new HttpPatch(uri);
            default:
                throw new IllegalArgumentException("Method " + method + " is not implemented!");
        }
    }

    private URI appendQueryParameters(URIBuilder uriBuilder, Multimap<String, String> queryParameters) throws URISyntaxException {
        if (queryParameters != null && !queryParameters.isEmpty()) {
            queryParameters.entries().stream().forEach(entry -> {
                try {
                    uriBuilder.setParameter(URLEncoder.encode(entry.getKey(), "UTF-8"), entry.getValue());
                } catch (UnsupportedEncodingException e) {
                }
            });
        }
        return uriBuilder.build();
    }

    private void writeEntityOutOverConnection(Body body, PipedOutputStream pipedOutputStream) throws IOException {
        body.asChunks().observeOn(Schedulers.io())
                .doOnNext(bytes -> {
                    try {
                        pipedOutputStream.write(bytes);
                    } catch (IOException e) {
                        throw new HttpException("Error writing body!", e);
                    }
                })
                .doOnCompleted(() -> {
                    try {
                        pipedOutputStream.close();
                    } catch (IOException e) {
                        throw new HttpException("Error while closing stream!", e);
                    }
                })
                .doOnError(e -> {
                    try {
                        pipedOutputStream.close();
                    } catch (IOException closing) {
                        throw new HttpException("Error while closing stream due to an original exception!", e);
                    }

                    throw new HttpException(e);
                })
                .subscribe();
    }

    public Observable<Response> delete(String uri) {
        return delete(uri, null);
    }

    public Observable<Response> delete(String uri, Map<String, String> headers) {
        return request(Method.DELETE, headers, uri, (Body) null);
    }


    public Observable<Response> delete(Function<RequestBuilder, Request> builder) {
        return request(requestBuilder -> builder.apply(requestBuilder.method(Method.DELETE)));
    }

    public Observable<Response> get(String uri) {
        return get(uri, null);
    }

    public Observable<Response> get(String uri, Map<String, String> headers) {
        return request(Method.GET, headers, uri, (Body) null);
    }


    public Observable<Response> get(Function<RequestBuilder, Request> builder) {
        return request(requestBuilder -> builder.apply(requestBuilder.method(Method.GET)));
    }

    public Observable<Response> patch(String uri, Body body) {
        return patch(uri, null, body);
    }

    public Observable<Response> patch(String uri, Value entity) {
        return patch(uri, null, entity);
    }

    public Observable<Response> patch(String uri, Map<String, String> headers, Body body) {
        return request(Method.PATCH, headers, uri, body);
    }

    public Observable<Response> patch(String uri, Map<String, String> headers, Value entity) {
        return request(Method.PATCH, headers, uri, entity);
    }

    public Observable<Response> patch(Function<RequestBuilder, Request> builder) {
        return request(requestBuilder -> builder.apply(requestBuilder.method(Method.PATCH)));
    }

    public Observable<Response> post(String uri, Body body) {
        return post(uri, null, body);
    }

    public Observable<Response> post(String uri, Value entity) {
        return post(uri, null, entity);
    }

    public Observable<Response> post(String uri, Map<String, String> headers, Body body) {
        return request(Method.POST, headers, uri, body);
    }

    public Observable<Response> post(String uri, Map<String, String> headers, Value entity) {
        return request(Method.POST, headers, uri, entity);
    }

    public Observable<Response> post(Function<RequestBuilder, Request> builder) {
        return request(requestBuilder -> builder.apply(requestBuilder.method(Method.POST)));
    }

    public Observable<Response> put(String uri, Body body) {
        return put(uri, null, body);
    }

    public Observable<Response> put(String uri, Value entity) {
        return put(uri, null, entity);
    }

    public Observable<Response> put(String uri, Map<String, String> headers, Body body) {
        return request(Method.PUT, headers, uri, body);
    }

    public Observable<Response> put(String uri, Map<String, String> headers, Value entity) {
        return request(Method.PUT, headers, uri, entity);
    }

    public Observable<Response> put(Function<RequestBuilder, Request> builder) {
        return request(requestBuilder -> builder.apply(requestBuilder.method(Method.PUT)));
    }
}
