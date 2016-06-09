package foundation.stack.datamill.security.impl;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class BCryptTest {
    @Test
    public void basicEncryption() {
        String hashed = BCrypt.hashpw("password", BCrypt.gensalt());
        assertTrue(BCrypt.checkpw("password", hashed));
        assertFalse(BCrypt.checkpw("password1", hashed));
    }
}
