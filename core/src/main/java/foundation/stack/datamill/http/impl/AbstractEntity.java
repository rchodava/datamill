package foundation.stack.datamill.http.impl;

import com.google.common.collect.Lists;
import foundation.stack.datamill.http.Entity;
import foundation.stack.datamill.json.JsonObject;
import org.json.JSONArray;
import rx.Observable;

import java.util.List;

/**
 * @author Israel Colomer (israelcolomer@gmail.com)
 */
public abstract  class AbstractEntity implements Entity {

    @Override
    public Observable<JsonObject> asJsonFromArray() {
        return asString().flatMap(json -> {
            JSONArray jsonArray = new JSONArray(json);
            List<JsonObject> jsonObjects = Lists.newArrayListWithCapacity(jsonArray.length());
            for(int i = 0; i < jsonArray.length(); i++) {
                jsonObjects.add(new JsonObject(jsonArray.get(i).toString()));
            }
            return Observable.from(jsonObjects);
        });
    }

}
