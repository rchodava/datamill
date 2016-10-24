package foundation.stack.datamill;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Israel Colomer (israelcolomer@gmail.com)
 */
public class ProcessRunnerTest {

    @Test
    public void runProcessAndWait_ReturnsExpectedResults_OnSuccessfulCommandExecution() throws IOException {
        boolean runningOnWindows = runningOnWindows();
        String[] command = runningOnWindows ? new String[] {"ping", "localhost"} : new String[] {"ping", "-c", "1", "localhost"};

        ProcessRunner.ExecutionResult executionResult = ProcessRunner.runProcessAndWait(null, command);
        assertEquals(executionResult.getExitCode(), 0);
        assertTrue(executionResult.getErrorOutput().isEmpty());
        if (runningOnWindows) {
            assertTrue(executionResult.getStandardOutput().contains("Reply from"));
        }
        else {
            assertEquals(executionResult.getStandardOutput().get(0), "PING localhost (127.0.0.1): 56 data bytes");
        }
    }

    @Test
    public void runProcessAndWait_NoOutput_OnSuccessfulCommandExecution() throws IOException {
        boolean runningOnWindows = runningOnWindows();
        String[] command = runningOnWindows ? new String[] {"ping", "localhost"} : new String[] {"ping", "-c", "1", "localhost"};

        ProcessRunner.ExecutionResult executionResult = ProcessRunner.runProcessAndWait(null, false, command);
        assertEquals(executionResult.getExitCode(), 0);
        assertNull(executionResult.getErrorOutput());
        assertNull(executionResult.getStandardOutput());
    }

    @Test
    public void runProcessAndWait_ReturnsExpectedResults_OnFailingCommandExecution() throws IOException {
        Path userHome = Paths.get(System.getProperty("user.home"));
        Path doesNotExist = Paths.get(userHome.toString(), "doesNotExist");
        boolean runningOnWindows = runningOnWindows();
        String[] command = runningOnWindows ? new String[] {"dir", "/x", doesNotExist.toString()} : new String[] {"ls", "-la", doesNotExist.toString()};
        ProcessRunner.ExecutionResult executionResult = ProcessRunner.runProcessAndWait(null, command);
        assertEquals(executionResult.getExitCode(), 1);
        assertTrue(executionResult.getStandardOutput().isEmpty());
        if (runningOnWindows) {
            assertTrue(executionResult.getErrorOutput().contains("File Not Found"));

        }
        else {
            assertEquals(executionResult.getErrorOutput().get(0), "ls: " + doesNotExist.toString() + ": No such file or directory");
        }
    }

    @Test
    public void runProcess_PerformsAsExpected_OnSuccessfulCommandExecution() throws IOException {
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), File.separator, "test");
        ProcessRunner.runProcess(null, "mkdir", tempDir.toString());

        boolean runningOnWindows = runningOnWindows();
        String[] command = runningOnWindows ? new String[] {"dir", "/x", tempDir.toString()} : new String[] {"ls", "-la", tempDir.toString()};

        ProcessRunner.ExecutionResult executionResult = ProcessRunner.runProcessAndWait(null, command);
        assertEquals(executionResult.getExitCode(), 0);
        assertTrue(executionResult.getErrorOutput().isEmpty());
        if (runningOnWindows) {
            assertTrue(executionResult.getStandardOutput().contains("0 File(s)"));
        }
        else {
            assertEquals(executionResult.getStandardOutput().get(0), "total 0");
        }
    }

    private static boolean runningOnWindows() {
        return System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH).contains("win");
    }
}
