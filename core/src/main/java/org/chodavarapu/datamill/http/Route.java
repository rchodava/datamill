package org.chodavarapu.datamill.http;

import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Route extends Function<Request, Response> {
}
