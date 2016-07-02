package foundation.stack.datamill.http;

import com.google.common.collect.Multimap;
import foundation.stack.datamill.values.Value;

import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Request {
    String OPTION_CONNECT_TIMEOUT = "connectTimeout";

    Body body();

    Multimap<String, String> headers();

    Value firstHeader(String header);

    Value firstHeader(RequestHeader header);

    Value firstQueryParameter(String name);

    Method method();

    Map<String, Object> options();

    Multimap<String, String> queryParameters();

    String rawMethod();

    String uri();

    Value uriParameter(String parameter);

    Map<String, String> uriParameters();
}
