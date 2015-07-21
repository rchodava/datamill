package org.chodavarapu.datamill.http;

import org.chodavarapu.datamill.org.chodavarapu.datamill.json.JsonElement;

import java.io.InputStream;
import java.io.Reader;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface RequestEntity {
    JsonElement asJson();
    Reader asReader();
    InputStream asStream();
    String asString();
}
