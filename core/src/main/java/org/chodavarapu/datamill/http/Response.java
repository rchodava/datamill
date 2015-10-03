package org.chodavarapu.datamill.http;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Response {
    Object entity();
    Status status();
}
