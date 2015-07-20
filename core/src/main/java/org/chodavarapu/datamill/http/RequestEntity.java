package org.chodavarapu.datamill.http;

import java.io.InputStream;
import java.io.Reader;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface RequestEntity {
    Reader asReader();
    InputStream asStream();
    String asString();
}
