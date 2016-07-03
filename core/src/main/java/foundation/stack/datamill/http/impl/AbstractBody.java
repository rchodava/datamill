package foundation.stack.datamill.http.impl;

import com.google.common.collect.Lists;
import foundation.stack.datamill.http.Body;
import foundation.stack.datamill.json.JsonObject;
import org.json.JSONArray;
import rx.Observable;

import java.util.List;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public abstract class AbstractBody implements Body {
    @Override
    public Observable<JsonObject> asJsonArray() {
        return asString().flatMap(json -> {
            JSONArray array = new JSONArray(json);
            List<JsonObject> jsonObjects = Lists.newArrayListWithCapacity(array.length());
            for(int i = 0; i < array.length(); i++) {
                jsonObjects.add(new JsonObject(array.get(i).toString()));
            }
            return Observable.from(jsonObjects);
        });
    }
}
