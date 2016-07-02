package foundation.stack.datamill.http;

import com.google.common.collect.Multimap;

import java.util.Optional;
import java.util.function.Function;

/**
 * Represents a HTTP response. Responses should generally be built using a {@link ResponseBuilder} obtained by calling
 * the {@link ServerRequest#respond(Function)} method.
 *
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Response {
    Optional<Body> body();
    Multimap<String, String> headers();
    Status status();
}
