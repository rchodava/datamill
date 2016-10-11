package foundation.stack.datamill.http.impl;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import rx.Observable;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public final class ServerRequestBuilder {
    private ServerRequestBuilder() {
    }

    public static ServerRequestImpl buildServerRequest(HttpRequest request, Observable<ByteBuffer> bodyStream) {
        Charset messageCharset = HttpUtil.getCharset(request);
        return new ServerRequestImpl(
                request.method().name(),
                buildHeadersMap(request.headers()),
                request.uri(),
                messageCharset,
                new StreamedChunksBody(bodyStream, messageCharset));
    }

    public static Multimap<String, String> buildHeadersMap(HttpHeaders headers) {
        Multimap<String, String> headersMap;

        HttpHeaders requestHeaders = headers;
        if (!requestHeaders.isEmpty()) {
            ImmutableMultimap.Builder<String, String> builder = ImmutableMultimap.builder();

            for (Map.Entry<String, String> header : requestHeaders) {
                String key = header.getKey();
                String value = header.getValue();

                if (key != null && value != null) {
                    builder.put(key.toLowerCase(), value);
                }
            }

            headersMap = builder.build();
        } else {
            headersMap = null;
        }

        return headersMap;
    }
}
