package org.chodavarapu.datamill.security;

import org.jose4j.jwk.*;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface KeyGenerators {
    class Symmetric {
        public static void main(String[] arguments) throws Exception {
            OctetSequenceJsonWebKey key = OctJwkGenerator.generateJwk(512);
            key.setKeyId("k" + System.currentTimeMillis());
            System.out.println(new JsonWebKeySet(key).toJson(JsonWebKey.OutputControlLevel.INCLUDE_SYMMETRIC));
        }
    }

    class RSA {
        public static void main(String[] arguments) throws Exception {
            RsaJsonWebKey key = RsaJwkGenerator.generateJwk(2048);
            key.setKeyId("k" + System.currentTimeMillis());
            System.out.println(new JsonWebKeySet(key).toJson(JsonWebKey.OutputControlLevel.INCLUDE_PRIVATE));
        }
    }
}
