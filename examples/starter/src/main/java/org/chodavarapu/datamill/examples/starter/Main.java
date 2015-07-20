package org.chodavarapu.datamill.examples.starter;

import org.chodavarapu.datamill.http.Server;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.addListener(8080,
                r -> {
                    switch (r.method().get()) {
                        case GET:
                        case POST:
                        case PUT:
                        case DELETE:
                        default:
                    }

                    return r.method().ifGet(g -> g.respond().ok()).
                            orElse(r.respond().ok());
                });

        server.addListener(440, true, r -> r.respond().ok());
        server.start();
    }
}
