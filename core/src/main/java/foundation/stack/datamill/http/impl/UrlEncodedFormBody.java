package foundation.stack.datamill.http.impl;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class UrlEncodedFormBody extends BytesBody {
    private static List<NameValuePair> toNameValuePairs(Map<String, String> map) {
        if (map != null) {
            ArrayList<NameValuePair> pairs = new ArrayList<>();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }

            return pairs;
        }

        return Collections.emptyList();
    }

    public UrlEncodedFormBody(Map<String, String> parameters, Charset charset) {
        super(URLEncodedUtils.format(toNameValuePairs(parameters), charset).getBytes(charset));
    }
}
