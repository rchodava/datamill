package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Entity;
import org.chodavarapu.datamill.http.HttpException;
import org.chodavarapu.datamill.json.JsonObject;
import rx.Observable;
import rx.observables.StringObservable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class InputStreamEntity implements Entity {
    private final InputStream inputStream;

    public InputStreamEntity(InputStream inputStream) {
        this.inputStream = inputStream;
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
    public Observable<JsonObject> asJson() {
        return asString().map(string -> new JsonObject(string));
    }

    @Override
    public Observable<byte[]> asChunks() {
        return StringObservable.from(inputStream);
    }

    @Override
    public Observable<String> asString() {
        return asBytes().map(bytes -> new String(bytes));
    }
}
