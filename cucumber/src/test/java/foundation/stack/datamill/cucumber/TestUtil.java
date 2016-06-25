package foundation.stack.datamill.cucumber;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author Israel Colomer (israelcolomer@gmail.com)
 */
public class TestUtil {

    private TestUtil() {
    }

    public static Integer findRandomPort(int defaultPort)  {
        try {
            try (ServerSocket socket = new ServerSocket(0)) {
                return socket.getLocalPort();
            }
        } catch (IOException e) {}

        return defaultPort;
    }
}
