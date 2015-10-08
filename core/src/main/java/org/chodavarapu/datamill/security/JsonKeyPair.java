package org.chodavarapu.datamill.security;

import java.security.Key;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface JsonKeyPair extends JsonKey {
    Key getPrivateKey();
}
