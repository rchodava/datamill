package foundation.stack.datamill.http;

import foundation.stack.datamill.json.JsonObject;
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
