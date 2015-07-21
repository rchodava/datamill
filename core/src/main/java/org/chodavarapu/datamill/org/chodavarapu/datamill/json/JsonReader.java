package org.chodavarapu.datamill.org.chodavarapu.datamill.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import org.chodavarapu.datamill.org.chodavarapu.datamill.json.impl.JsonArrayImpl;
import org.chodavarapu.datamill.org.chodavarapu.datamill.json.impl.JsonElementImpl;
import org.chodavarapu.datamill.org.chodavarapu.datamill.json.impl.JsonObjectImpl;

import java.io.IOException;
import java.io.Reader;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class JsonReader {
    private final JsonFactory factory = new JsonFactory();

    private void parse(JsonParser parser, JsonElement container) throws IOException {
        JsonToken token = parser.nextToken();
        while (token != null) {
            if (token == JsonToken.START_ARRAY) {
                parse(parser, new JsonArrayImpl());
            } else if (token == JsonToken.START_OBJECT) {
                parse(parser, new JsonObjectImpl());
            } else if (token == JsonToken.FIELD_NAME) {
                String fieldName = parser.getCurrentName();
            }

            token = parser.nextToken();
        }
    }

    public JsonElement read(Reader reader) {
        try {
            JsonParser parser = factory.createParser(reader);
//            return new JsonElementImpl(parser);
            return null;
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }
}
