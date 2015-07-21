package org.chodavarapu.datamill.org.chodavarapu.datamill.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import org.chodavarapu.datamill.org.chodavarapu.datamill.json.impl.JsonElementImpl;

import java.io.IOException;
import java.io.Reader;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class JsonReader {
    private final JsonFactory factory = new JsonFactory();

    public JsonElement read(Reader reader) {
        try {
            JsonParser parser = factory.createParser(reader);
            return new JsonElementImpl(parser);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }
}
