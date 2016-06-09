package foundation.stack.datamill.security;

import org.jose4j.jwk.*;
import org.jose4j.lang.JoseException;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class KeySet {
    private static class JsonKeyImpl implements JsonKey {
        protected final JsonWebKey key;

        public JsonKeyImpl(JsonWebKey key) {
            this.key = key;
        }

        @Override
        public KeyType getType() {
            switch (key.getKeyType()) {
                case RsaJsonWebKey.KEY_TYPE: return KeyType.RSA;
                case OctetSequenceJsonWebKey.KEY_TYPE: return KeyType.SYMMETRIC;
                default: return KeyType.UNSUPPORTED;
            }
        }

        @Override
        public String getId() {
            return key.getKeyId();
        }

        @Override
        public Key getKey() {
            return key.getKey();
        }
    }

    private static class JsonKeyPairImpl extends JsonKeyImpl implements JsonKeyPair {
        public JsonKeyPairImpl(JsonWebKey key) {
            super(key);

            if (!(key instanceof PublicJsonWebKey)) {
                throw new IllegalArgumentException("Specified key does not consist of a key pair!");
            }
        }

        @Override
        public Key getPrivateKey() {
            return ((PublicJsonWebKey) key).getPrivateKey();
        }
    }
    
    private final List<JsonKey> keys = new ArrayList<>();

    public KeySet(String keySetJson) {
        try {
            JsonWebKeySet keySet = new JsonWebKeySet(keySetJson);
            for (JsonWebKey key : keySet.getJsonWebKeys()) {
                if (key instanceof PublicJsonWebKey) {
                    keys.add(new JsonKeyPairImpl(key));
                } else {
                    keys.add(new JsonKeyImpl(key));
                }
            }
        } catch (JoseException e) {
            throw new SecurityException(e);
        }
    }

    public List<JsonKey> getKeys() {
        return keys;
    }
}
