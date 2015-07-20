package org.chodavarapu.datamill.http;

import org.chodavarapu.datamill.http.impl.RequestImpl;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Server {
    private final org.eclipse.jetty.server.Server embeddedServer;
    private final Map<Connector, Function<Request, Response>> listeners = new HashMap<Connector, Function<Request, Response>>();

    public Server() {
        embeddedServer = new org.eclipse.jetty.server.Server();
        embeddedServer.setHandler(new AbstractHandler() {
            public void handle(String target, org.eclipse.jetty.server.Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                Server.this.handle(baseRequest, request, response);
            }
        });
    }

    public Server addListener(String host, int port, boolean secure, Function<Request, Response> listener) {
        ServerConnector connector = new ServerConnector(embeddedServer);
        connector.setHost(host);
        connector.setPort(port);

        embeddedServer.addConnector(connector);
        listeners.put(connector, listener);

        return this;
    }

    public Server addListener(String host, int port, Function<Request, Response> listener) {
        return addListener(host, port, false, listener);
    }

    public Server addListener(int port, Function<Request, Response> listener) {
        return addListener("localhost", port, listener);
    }

    public Server addListener(int port, boolean secure, Function<Request, Response> listener) {
        return addListener("localhost", port, secure, listener);
    }

    private void handle(org.eclipse.jetty.server.Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        HttpChannel channel = baseRequest.getHttpChannel();
        if (channel != null) {
            Function<Request, Response> listener = listeners.get(channel.getConnector());
            if (listener != null) {
                Response generatedResponse = listener.apply(new RequestImpl(request));
                if (generatedResponse instanceof Consumer) {
                    ((Consumer) generatedResponse).accept(response);
                }

                baseRequest.setHandled(true);
            }
        }
    }

    public void start() throws Exception {
        embeddedServer.start();
        embeddedServer.join();
    }
}
