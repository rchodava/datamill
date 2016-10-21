package foundation.stack.datamill.cucumber;

import java.security.SecureRandom;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class RandomGenerator {
    private static final SecureRandom random = new SecureRandom();
    private static final char[] CHARACTERS = new char[]{
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    private RandomGenerator() {
    }

    public static String generateRandomAlphanumeric(int length) {
        StringBuilder name = new StringBuilder("n");
        for (int i = 0; i < length - 2; i++) {
            name.append(CHARACTERS[random.nextInt(36)]);
        }
        name.append('z');
        return name.toString();
    }
}
