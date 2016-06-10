package foundation.stack.datamill.security;

import org.jose4j.jwt.consumer.JwtConsumerBuilder;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class JsonWebTokenVerificationBuilder extends JsonWebToken.AbstractJsonWebTokenVerificationBuilder {
    private static final String BEARER_AUTHENTICATION_SCHEME = "Bearer ";
    private static final int SCHEME_IDENTIFIER_LENGTH = BEARER_AUTHENTICATION_SCHEME.length();

    private JwtConsumerBuilder builder = new JwtConsumerBuilder();

    public JsonWebTokenVerificationBuilder requiringExpirationTime() {
        builder.setRequireExpirationTime();
        return this;
    }

    public JsonWebTokenVerificationBuilder allowingClockSkewInSeconds(int seconds) {
        builder.setAllowedClockSkewInSeconds(seconds);
        return this;
    }

    public JsonWebTokenVerificationBuilder requiringSubject() {
        builder.setRequireSubject();
        return this;
    }

    public JsonWebTokenVerificationBuilder expectingSubject(String subject) {
        builder.setExpectedSubject(subject);
        return this;
    }

    public JsonWebTokenVerificationBuilder expectingIssuer(String issuer) {
        builder.setExpectedIssuer(issuer);
        return this;
    }

    public JsonWebTokenVerificationBuilder expectingAudience(String audience) {
        builder.setExpectedAudience(audience);
        return this;
    }

    public JsonWebTokenVerificationBuilder expectingDefaults() {
        return requiringExpirationTime().allowingClockSkewInSeconds(30).requiringSubject();
    }

    public JsonWebTokenVerificationBuilder withVerificationKey(JsonKey key) {
        if (key instanceof JsonKeyPair) {
            builder.setVerificationKey(((JsonKeyPair) key).getPrivateKey());
        } else {
            builder.setVerificationKey(key.getKey());
        }
        return this;
    }

    public JsonWebToken verify(String jwt) {
        if (jwt != null && jwt.startsWith(BEARER_AUTHENTICATION_SCHEME)) {
            jwt = jwt.substring(SCHEME_IDENTIFIER_LENGTH);
        }

        return super.verify(builder, jwt);
    }
}
