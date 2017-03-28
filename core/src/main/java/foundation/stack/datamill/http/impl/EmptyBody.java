package foundation.stack.datamill.http.impl;

import foundation.stack.datamill.http.Body;
import foundation.stack.datamill.json.JsonObject;
import rx.Observable;

import java.nio.ByteBuffer;

/**
 * @author Israel Colomer (israelcolomer@gmail.com)
 */
public class EmptyBody implements Body {
    @Override
    public Observable<byte[]> asBytes() {
        return Observable.empty();
    }

    @Override
    public Observable<JsonObject> asJson() {
        return Observable.empty();
    }

    @Override
    public Observable<JsonObject> asJsonArray() {
        return Observable.empty();
    }

    @Override
    public Observable<byte[]> asChunks() {
        return Observable.empty();
    }

    @Override
    public Observable<ByteBuffer> asBufferChunks() {
        return Observable.empty();
    }

    @Override
    public Observable<String> asString() {
        return Observable.empty();
    }

    @Override
    public <T> Observable<T> fromJson(Class<T> clazz) {
        return Observable.empty();
    }
}
