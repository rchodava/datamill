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

    public String encoded() {
        JsonWebSignature signature = new JsonWebSignature();

        signature.setPayload(claims.toJson());
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

    public JsonWebToken setDefaults() {
        setGeneratedJwtId();
        setIssuedAtToNow();
        setNotBeforeMinutesInThePast(2);
        setExpirationTimeMinutesInTheFuture(10);
        return this;
    }

    public JsonWebToken setExpirationTimeMinutesInTheFuture(float minutes) {
        claims.setExpirationTimeMinutesInTheFuture(minutes);
        return this;
    }

    public JsonWebToken setGeneratedJwtId() {
        claims.setGeneratedJwtId();
        return this;
    }

    public JsonWebToken setIssuedAtToNow() {
        claims.setIssuedAtToNow();
        return this;
    }

    public JsonWebToken setIssuer(String issuer) {
        claims.setIssuer(issuer);
        return this;
    }

    public JsonWebToken setJwtId(String jwtId) {
        claims.setJwtId(jwtId);
        return this;
    }

    public JsonWebToken setKey(JsonKey key) {
        this.key = key;
        return this;
    }

    public JsonWebToken setNotBeforeMinutesInThePast(float minutes) {
        claims.setNotBeforeMinutesInThePast(minutes);
        return this;
    }

    public JsonWebToken setSubject(String subject) {
        claims.setSubject(subject);
        return this;
    }
}
