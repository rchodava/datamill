package org.chodavarapu.datamill.examples.starter;

import org.chodavarapu.datamill.http.Server;
import org.chodavarapu.datamill.org.chodavarapu.datamill.json.JsonMappers;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Main {
    private static class User {

    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.addListener(8080,
                r -> {
                    switch (r.method().get()) {
                        case "GET":
                            r.entity().asJson().mapToObject(new User(), (j, u) -> {
                                u.properties().stream().forEach(p -> {
                                    if (p.isSimple()) {
                                        p.set(j.get(p.getName()));
                                    } else {
                                    }
                                });
                            });
                        case "PATCH":
                            r.entity().asJson().map(JsonMappers.JSON_TO_JSON_PATCH_OPERATIONS);
                        case "POST":
                        case "PUT":
                        case "DELETE":
                        default:
                    }

                    return r.method().ifGet(g -> g.respond().ok()).
                            orElse(r.respond().ok());
                });

        server.addListener(440, true, r -> r.respond().ok());
        server.start();
    }
}
