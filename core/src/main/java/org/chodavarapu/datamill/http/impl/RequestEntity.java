package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Entity;
import org.chodavarapu.datamill.http.HttpException;
import org.chodavarapu.datamill.json.JsonObject;
import rx.Observable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class RequestEntity implements Entity {
    private final Observable<byte[]> chunks;
    private final Charset charset;

    public RequestEntity(Observable<byte[]> chunks, Charset charset) {
        this.chunks = chunks;
        this.charset = charset;
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
        return chunks;
    }

    @Override
    public Observable<JsonObject> asJson() {
        return asString().map(string -> new JsonObject(string));
    }

    @Override
    public Observable<String> asString() {
        return asBytes().map(bytes -> new String(bytes, charset));
    }
}
