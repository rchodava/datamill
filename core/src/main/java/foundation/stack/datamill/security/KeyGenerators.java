package foundation.stack.datamill.security;

import org.jose4j.jwk.*;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.ByteUtil;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface KeyGenerators {
    class Symmetric {
        private Symmetric() {
        }

        public static void main(String[] arguments) throws Exception {
            byte[] bytes = ByteUtil.randomBytes(ByteUtil.byteLength(512));
            OctetSequenceJsonWebKey key = new OctetSequenceJsonWebKey(new HmacKey(bytes));
            key.setKeyId("k" + System.currentTimeMillis());
            System.out.println(new JsonWebKeySet(key).toJson(JsonWebKey.OutputControlLevel.INCLUDE_SYMMETRIC));
        }
    }

    class RSA {
        private RSA() {
        }

        public static void main(String[] arguments) throws Exception {
            RsaJsonWebKey key = RsaJwkGenerator.generateJwk(2048);
            key.setKeyId("k" + System.currentTimeMillis());

            System.out.println("Public & Private:");
            System.out.println(new JsonWebKeySet(key).toJson(JsonWebKey.OutputControlLevel.INCLUDE_PRIVATE));

            System.out.println();

            System.out.println("Only Public:");
            System.out.println(new JsonWebKeySet(key).toJson(JsonWebKey.OutputControlLevel.PUBLIC_ONLY));
        }
    }
}
