package foundation.stack.datamill.http.impl;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class TemplateBasedUriBuilderTest {
    @Test
    public void templateBasedUriBuilding() {
        TemplateBasedUriBuilder builder = new TemplateBasedUriBuilder();

        assertEquals("http://localhost:9080/users/0/repositories",
                builder.build("http://{host}:{port}/users/{id}/repositories",
                        ImmutableMap.of("host", "localhost", "port", "9080", "id", "0")));
        assertEquals("https://localhost:9080/users/0/repositories",
                builder.build("{scheme}://{host}:{port}/users/{id}/repositories",
                        ImmutableMap.of("host", "localhost", "port", "9080", "id", "0", "scheme", "https")));
        assertEquals("http://localhost:9080/users/0/repositories",
                builder.build("http://{host}:{port}/users/{id}/{association}",
                        ImmutableMap.of("host", "localhost", "port", "9080", "id", "0", "association", "repositories")));
        assertEquals("http://localhost:9080/users/0/repositories/{unmatched}",
                builder.build("http://{host}:{port}/users/{id}/{association}/{unmatched}",
                        ImmutableMap.of("host", "localhost", "port", "9080", "id", "0", "association", "repositories")));
        assertEquals("http://localhost:9080/users",
                builder.build("{scheme}{host}:{port}{path}",
                        ImmutableMap.of("scheme", "http://", "host", "localhost", "port", "9080", "path", "/users")));
        assertEquals("http://localhost:9080/users",
                builder.build("{scheme}{hostName}:{port}{path1}",
                        ImmutableMap.of("scheme", "http://", "hostName", "localhost", "port", "9080", "path1", "/users")));
    }
}
