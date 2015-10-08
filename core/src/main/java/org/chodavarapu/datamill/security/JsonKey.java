package org.chodavarapu.datamill.security;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface JsonKey {
    String getId();
    KeyType getType();
    java.security.Key getKey();
}
