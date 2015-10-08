package org.chodavarapu.datamill.security;

import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class JsonWebToken {
    private JsonKey key;
    private final JwtClaims claims = new JwtClaims();

    public JsonWebToken() {
        claims.setExpirationTimeMinutesInTheFuture(10);
        claims.setGeneratedJwtId();
        claims.setIssuedAtToNow();
        claims.setNotBeforeMinutesInThePast(2);
        claims.setSubject("subject");
    }

    public JsonWebToken setKey(JsonKey key) {
        this.key = key;
        return this;
    }

    public String encoded() {
        JsonWebSignature signature = new JsonWebSignature();
        signature.setKeyIdHeaderValue(key.getId());

        switch (key.getType()) {
            case SYMMETRIC:
                signature.setKey(key.getKey());
                signature.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
                break;
            case RSA:
                signature.setKey(((JsonKeyPair) key).getPrivateKey());
                signature.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
                break;
        }

        try {
            return signature.getCompactSerialization();
        } catch (JoseException e) {
            throw new SecurityException(e);
        }
    }
}
