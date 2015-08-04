package org.chodavarapu.datamill.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import org.chodavarapu.datamill.http.builder.RouteBuilder;
import org.chodavarapu.datamill.http.impl.RequestImpl;
import org.chodavarapu.datamill.http.impl.RouteBuilderImpl;

import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Server extends AbstractVerticle {
    private HttpServer server;

    private final Function<RouteBuilder, Route> routeConstructor;

    public Server(Function<RouteBuilder, Route> routeConstructor) {
        this.routeConstructor = routeConstructor;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Route route = routeConstructor.apply(new RouteBuilderImpl());

        server = vertx.createHttpServer();
        server.requestHandler(r -> {
            route.apply(new RequestImpl(r));
        });

        startFuture.complete();
    }

    public Server listen(String host, int port, boolean secure) {
        server.listen(port, host);
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
