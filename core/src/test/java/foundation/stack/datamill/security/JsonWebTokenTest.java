package foundation.stack.datamill.security;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class JsonWebTokenTest {
    private static final String SYMMETRIC_TEST_KEY = "{\"keys\":[{\"kty\":\"oct\",\"kid\":\"k1445302661828\",\"k\":\"grWBrDk3Cv8FP-BPI6q654mCFUDKFY_tVsZ3u6MpYkTM2mZRPolzhA9bUNRYYDOEsD-aQ_srxjLg0evm8C81XA\"}]}";

    @Test
    public void testDefaultVerification() {
        String token = new JsonWebToken()
                .setDefaults()
                .setKey(new KeySet(SYMMETRIC_TEST_KEY).getKeys().get(0))
                .setSubject("rchodava@gmail.com").encoded();

        JsonWebToken verified = JsonWebToken.buildVerification()
                .withVerificationKey(new KeySet(SYMMETRIC_TEST_KEY).getKeys().get(0))
                .expectingDefaults()
                .verify(token);

        assertEquals("rchodava@gmail.com", verified.getSubject());
    }

    @Test(expected = SecurityException.class)
    public void testFailingOnMismatchedSubject() {
        String token = new JsonWebToken()
                .setDefaults()
                .setKey(new KeySet(SYMMETRIC_TEST_KEY).getKeys().get(0))
                .setSubject("rchodava@gmail.com").encoded();

        JsonWebToken.buildVerification()
                .withVerificationKey(new KeySet(SYMMETRIC_TEST_KEY).getKeys().get(0))
                .expectingDefaults()
                .expectingSubject("rchodava1@gmail.com")
                .verify(token);
    }

    @Test(expected = SecurityException.class)
    public void testFailingOnMissingSubject() {
        String token = new JsonWebToken()
                .setDefaults()
                .setKey(new KeySet(SYMMETRIC_TEST_KEY).getKeys().get(0))
                .encoded();

        JsonWebToken.buildVerification()
                .withVerificationKey(new KeySet(SYMMETRIC_TEST_KEY).getKeys().get(0))
                .expectingDefaults()
                .verify(token);
    }
}
