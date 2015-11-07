package org.chodavarapu.datamill.http;

import org.chodavarapu.datamill.json.JsonObject;
import rx.Observable;
/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Entity {
    Observable<byte[]> asBytes();
    Observable<JsonObject> asJson();
    Observable<byte[]> asChunks();
    Observable<String> asString();
}
