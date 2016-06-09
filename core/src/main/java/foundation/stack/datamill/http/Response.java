package foundation.stack.datamill.http;

import com.google.common.collect.Multimap;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Response {
    Entity entity();
    Multimap<String, String> headers();
    Status status();
}
