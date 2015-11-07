package org.chodavarapu.datamill.http.impl;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.rx.java.RxHelper;
import org.chodavarapu.datamill.http.Entity;
import org.chodavarapu.datamill.http.HttpException;
import org.chodavarapu.datamill.json.JsonObject;
import rx.Observable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class RequestEntity implements Entity {
    private final HttpServerRequest request;

    public RequestEntity(HttpServerRequest request) {
        this.request = request;
    }

    @Override
    public Observable<byte[]> asBytes() {
        return asChunks().collect(
                () -> new ByteArrayOutputStream(),
                (stream, chunk) -> {
                    try {
                        stream.write(chunk);
                    } catch (IOException e) {
                        throw new HttpException(e);
                    }
                }).map(os -> os.toByteArray());
    }

    @Override
    public Observable<byte[]> asChunks() {
        return RxHelper.toObservable(request).map(b -> b.getBytes());
    }

    @Override
    public Observable<JsonObject> asJson() {
        return asString().map(string -> new JsonObject(string));
    }

    @Override
    public Observable<String> asString() {
        return asBytes().map(bytes -> new String(bytes));
    }
}
