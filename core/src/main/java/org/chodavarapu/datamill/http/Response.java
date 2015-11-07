package org.chodavarapu.datamill.http;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Response {
    Entity entity();
    Status status();
}
