package foundation.stack.datamill.http;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class StatusTest {
    @Test
    public void fromResponseCode() {
        assertEquals(Status.OK, Status.valueOf(200));
        assertEquals(Status.NO_CONTENT, Status.valueOf(204));
        assertEquals(Status.NOT_FOUND, Status.valueOf(404));
        assertEquals(Status.INTERNAL_SERVER_ERROR, Status.valueOf(500));
    }
}
