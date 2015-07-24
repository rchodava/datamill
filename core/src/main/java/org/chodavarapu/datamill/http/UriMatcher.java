package org.chodavarapu.datamill.http;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface UriMatcher<R> {
    String get();

    R ifMatches(String path, Function<Map<String, String>, R> handler);
}
