package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Entity;
import org.chodavarapu.datamill.json.JsonObject;
import rx.Observable;

/**
 * @author Israel Colomer (israelcolomer@gmail.com)
 */
public class EmptyEntity implements Entity {
    @Override
    public Observable<byte[]> asBytes() {
        return Observable.empty();
    }

    @Override
    public Observable<JsonObject> asJson() {
        return Observable.empty();
    }

    @Override
    public Observable<byte[]> asChunks() {
        return Observable.empty();
    }

    @Override
    public Observable<String> asString() {
        return Observable.empty();
    }
}
