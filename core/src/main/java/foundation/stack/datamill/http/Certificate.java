package foundation.stack.datamill.http;

import java.io.InputStream;

/**
 * @author Israel Colomer (israelcolomer@gmail.com)
 */
public interface Certificate {
    InputStream getCertificate();
    InputStream getPrivateKey();
}
