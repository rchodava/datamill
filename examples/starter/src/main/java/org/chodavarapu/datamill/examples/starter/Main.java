package org.chodavarapu.datamill.examples.starter;

import org.chodavarapu.datamill.http.Server;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Server server = new Server(rb ->
                rb.ifUriMatches("/posts").and(sb -> sb.ifGet().then(r -> r.respond().ok()).orElse(r -> r.respond().ok()))
                        .elseIfUriMatches("/users").and(sb ->
                                sb.ifGet().then(r -> r.respond().ok())
                                        .elseIfPost().then(r -> r.respond().ok())
                                        .elseIfPut().then(r -> r.respond().ok())
                                        .elseIfPatch().then(r -> r.respond().ok())
                                        .elseIfDelete().then(r -> r.respond().ok())
                                        .orElse(r -> r.respond().ok())
                ).orElse(r -> r.respond().notFound()));

        server.listen(8080);
    }
}
