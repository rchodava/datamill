package org.chodavarapu.datamill.security;

import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwk.OctetSequenceJsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.lang.JoseException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class KeySet {
    private static class KeyImpl implements Key {
        private final JsonWebKey key;

        public KeyImpl(JsonWebKey key) {
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
    }
    
    private final List<Key> keys = new ArrayList<>();

    public KeySet(String keySetJson) {
        try {
            JsonWebKeySet keySet = new JsonWebKeySet(keySetJson);
            for (JsonWebKey key : keySet.getJsonWebKeys()) {
                keys.add(new KeyImpl(key));
            }
        } catch (JoseException e) {
            throw new SecurityException(e);
        }
    }

    public List<Key> getKeys() {
        return keys;
    }
}
