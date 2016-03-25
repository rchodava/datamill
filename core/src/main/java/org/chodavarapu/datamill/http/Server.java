package org.chodavarapu.datamill.http;

import com.github.davidmoten.rx.Obs;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import org.chodavarapu.datamill.http.builder.RouteBuilder;
import org.chodavarapu.datamill.http.impl.ServerRequestImpl;
import org.chodavarapu.datamill.http.impl.RouteBuilderImpl;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Server extends AbstractVerticle {
    private final Function<RouteBuilder, Route> routeConstructor;
    private final BiFunction<ServerRequest, Throwable, Observable<Response>> errorResponseConstructor;
    private HttpServer server;

    public Server(Function<RouteBuilder, Route> routeConstructor) {
        this(routeConstructor, null);
    }

    public Server(
            Function<RouteBuilder, Route> routeConstructor,
            BiFunction<ServerRequest, Throwable, Observable<Response>> errorResponseConstructor) {
        this.routeConstructor = routeConstructor;
        this.errorResponseConstructor = errorResponseConstructor;
    }

    private Observable<byte[]> sendResponse(Response response, HttpServerRequest originalRequest) {
        if (response != null) {
            originalRequest.response().setStatusCode(response.status().getCode());

            if (response.headers() != null) {
                for (Map.Entry<String, String> header : response.headers().entrySet()) {
                    originalRequest.response().headers().add(header.getKey(), header.getValue());
                }
            }

            if (response.entity() == null) {
                originalRequest.response().end();
            } else {
                return response.entity().asBytes()
                        .doOnNext(bytes -> originalRequest.response().end(Buffer.buffer(bytes)))
                        .doOnError(throwable -> originalRequest.response().end());
            }
        }

        return Observable.just(null);
    }

    private void sendGeneralServerError(HttpServerRequest originalRequest) {
        originalRequest.response().setStatusCode(500).end();
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Route route = routeConstructor.apply(new RouteBuilderImpl());

        server = vertx.createHttpServer();
        server.requestHandler(r -> {
            Observable<Response> responseObservable = route.apply(new ServerRequestImpl(r));
            if (responseObservable != null) {
                responseObservable.flatMap(routeResponse -> sendResponse(routeResponse, r))
                        .onErrorResumeNext(t -> {
                            if (errorResponseConstructor != null) {
                                Observable<Response> errorResponseObservable =
                                        errorResponseConstructor.apply(new ServerRequestImpl(r), t);
                                if (errorResponseObservable != null) {
                                    return errorResponseObservable.flatMap(errorResponse -> sendResponse(errorResponse, r))
                                            .doOnError(secondError -> sendGeneralServerError(r))
                                            .map(__ -> null);
                                } else {
                                    sendGeneralServerError(r);
                                }
                            } else {
                                sendGeneralServerError(r);
                            }

                            return Observable.just(null);
                        })
                        .subscribe();
            } else {
                r.response().setStatusCode(404).end();
            }
        });

        startFuture.complete();
    }

    public Server listen(String host, int port, boolean secure) {
        Vertx.vertx(new VertxOptions().setBlockedThreadCheckInterval(1000 * 60 * 60))
                .deployVerticle(this, (verticle) -> {
                    server.listen(port, host);
                });
        return this;
    }

    public Server listen(String host, int port) {
        return listen(host, port, false);
    }

    public Server listen(int port) {
        return listen("localhost", port);
    }

    public Server listen(int port, boolean secure) {
        return listen("localhost", port, secure);
    }
}
