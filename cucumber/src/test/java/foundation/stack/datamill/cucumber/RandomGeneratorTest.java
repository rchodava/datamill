package foundation.stack.datamill.cucumber;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class RandomGeneratorTest {
    @Test
    public void generate() {
        assertEquals(255, RandomGenerator.generateRandomAlphanumeric(255).length());
        assertTrue(Pattern.matches("^[a-z0-9_]+$", RandomGenerator.generateRandomAlphanumeric(255)));
    }
}
