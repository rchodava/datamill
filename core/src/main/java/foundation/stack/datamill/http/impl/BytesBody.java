package foundation.stack.datamill.http.impl;

import foundation.stack.datamill.http.Body;
import foundation.stack.datamill.json.JsonObject;
import rx.Observable;

import java.nio.ByteBuffer;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class BytesBody extends AbstractBody implements Body {
    private byte[] bytes;

    public BytesBody(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public Observable<ByteBuffer> asBufferChunks() {
        return asChunks().map(bytes -> ByteBuffer.wrap(bytes));
    }

    @Override
    public Observable<byte[]> asBytes() {
        return Observable.just(bytes);
    }

    @Override
    public Observable<byte[]> asChunks() {
        return asBytes();
    }

    @Override
    public Observable<JsonObject> asJson() {
        return asString().map(json -> new JsonObject(json));
    }

    @Override
    public Observable<String> asString() {
        return asBytes().map(bytes -> new String(bytes));
    }
}
