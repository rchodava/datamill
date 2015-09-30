package org.chodavarapu.datamill.http;

import org.chodavarapu.datamill.json.JsonObject;

import java.io.InputStream;
import java.io.Reader;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface RequestEntity {
    JsonObject asJson();
    Reader asReader();
    InputStream asStream();
    String asString();
}
