package foundation.stack.datamill.http.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
import foundation.stack.datamill.http.Body;
import foundation.stack.datamill.json.JsonObject;
import org.json.JSONArray;
import rx.Observable;
import rx.exceptions.Exceptions;

import java.io.IOException;
import java.util.List;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public abstract class AbstractBody implements Body {
    private static final ObjectMapper jsonMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .disable(SerializationFeature.WRITE_NULL_MAP_VALUES);

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

    @Override
    public <T> Observable<T> fromJson(Class<T> clazz) {
        return asString().map(json -> {
            try {
                return jsonMapper.readValue(json, clazz);
            } catch (IOException e) {
                throw Exceptions.propagate(e);
            }
        });
    }
}
