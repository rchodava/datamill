package org.chodavarapu.datamill.http;

import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Response {
    Entity entity();
    Map<String, String> headers();
    Status status();
}
