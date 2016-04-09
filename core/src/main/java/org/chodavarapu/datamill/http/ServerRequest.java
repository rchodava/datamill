package org.chodavarapu.datamill.http;

import com.google.common.collect.Multimap;
import org.chodavarapu.datamill.values.Value;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface ServerRequest extends Request {
    Value firstTrailingHeader(String header);
    Value firstTrailingHeader(RequestHeader header);
    ResponseBuilder respond();
    Multimap<String, String> trailingHeaders();
}
