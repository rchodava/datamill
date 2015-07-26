package org.chodavarapu.datamill.examples.starter;

import org.chodavarapu.datamill.http.Server;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.addListener(8080, r ->
                r.uri().ifMatches("/posts").and(r.method().isGet()).then(__ -> r.respond().ok())
                        .elseIfUriMatches("/users").then(__ -> {
                    return r.method().ifGet().then(___ -> r.respond().ok())
                            .elseIfPost().then(___ -> r.respond().ok())
                            .elseIfPut().then(___ -> r.respond().ok())
                            .elseIfPatch().then(___ -> r.respond().ok())
                            .elseIfDelete().then(___ -> r.respond().ok())
                            .orElse(___ -> r.respond().ok());
                }).orElse(r.respond().notFound()));

        server.start();
    }
}
