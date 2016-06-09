package foundation.stack.datamill.http.impl;

import foundation.stack.datamill.http.Entity;
import foundation.stack.datamill.json.JsonObject;
import rx.Observable;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class BytesEntity implements Entity {
    private byte[] bytes;

    public BytesEntity(byte[] bytes) {
        this.bytes = bytes;
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
