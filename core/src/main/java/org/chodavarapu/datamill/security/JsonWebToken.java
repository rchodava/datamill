package org.chodavarapu.datamill.security;

import org.jose4j.jwt.JwtClaims;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class JsonWebToken {
    private final JwtClaims claims = new JwtClaims();

    public JsonWebToken() {
        claims.setExpirationTimeMinutesInTheFuture(10);
        claims.setGeneratedJwtId();
        claims.setIssuedAtToNow();
        claims.setNotBeforeMinutesInThePast(2);
        claims.setSubject("subject");
    }
}
