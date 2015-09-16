package org.chodavarapu.datamill.examples.starter;

import org.chodavarapu.datamill.db.Connection;
import org.chodavarapu.datamill.http.Method;
import org.chodavarapu.datamill.http.Server;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Server server = new Server(rb ->
                rb.ifUriMatches("/posts", r -> r.respond().ok())
                        .elseIfUriMatches("/users", r -> r.respond().ok())
                        .elseIfMethodMatches(Method.POST, r -> r.respond().ok())
                        .elseIfMethodMatches(Method.PUT, r -> r.respond().ok())
                        .elseIfMethodMatches(Method.GET, r -> r.respond().ok())
                        .elseIfMethodMatches(Method.DELETE, r -> r.respond().ok())
                        .orElse(r -> r.respond().notFound()));

        Connection connection = null;
        connection.select("name", "price").from("plan").all();
        server.listen(8080);
    }
}
