package org.chodavarapu.datamill.http.impl;

import io.vertx.core.http.HttpServerRequest;
import org.chodavarapu.datamill.http.RequestEntity;
import org.chodavarapu.datamill.http.HttpException;
import org.chodavarapu.datamill.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.observables.AbstractOnSubscribe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class RequestEntityImpl implements RequestEntity {
    private static final Logger logger = LoggerFactory.getLogger(RequestEntityImpl.class);

    private final HttpServerRequest request;

    public RequestEntityImpl(HttpServerRequest request) {
        this.request = request;
    }

    @Override
    public byte[] asBytes() {
        return asChunks().<ByteArrayOutputStream>collect(
                () -> new ByteArrayOutputStream(),
                (stream, chunk) -> {
                    try {
                        stream.write(chunk);
                    } catch (IOException e) {
                        throw new HttpException(e);
                    }
                }).toBlocking().last().toByteArray();
    }

    @Override
    public Observable<byte[]> asChunks() {
        return AbstractOnSubscribe.<byte[], Void>create(
                s -> {
                    request.handler(buffer -> s.onNext(buffer.getBytes()));
                    request.exceptionHandler(exception -> s.onError(exception));
                    request.endHandler(v -> s.onCompleted());
                },
                s -> null,
                v -> {
                    try {
                        request.handler(null);
                        request.exceptionHandler(null);
                        request.endHandler(null);
                    } catch (IllegalStateException e) {
                        logger.error("Error on terminating request data handling!", e);
                    }
                }).toObservable();
    }

    @Override
    public JsonObject asJson() {
        return new JsonObject(asString());
    }

    @Override
    public String asString() {
        return new String(asBytes());
    }
}
