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
        String[] command = new String[] {"java", "-version"};
        ProcessRunner.ExecutionResult executionResult = ProcessRunner.runProcessAndWait(null, command);
        assertEquals(executionResult.getExitCode(), 0);
        // It is counter intuitive, but jdk uses standard error instead of standard output for showing version
        assertEquals(3, executionResult.getStandardError().size());
        assertTrue(executionResult.getStandardError().get(0).startsWith("java version "));
        assertTrue(executionResult.getStandardOutput().isEmpty());
    }

    @Test
    public void runProcessAndWait_NoOutput_OnSuccessfulCommandExecution() throws IOException {
        String[] command = new String[] {"java", "-version"};
        ProcessRunner.ExecutionResult executionResult = ProcessRunner.runProcessAndWait(null, false, command);
        assertEquals(executionResult.getExitCode(), 0);
        assertNull(executionResult.getStandardError());
        assertNull(executionResult.getStandardOutput());
    }

    @Test
    public void runProcessAndWait_ReturnsExpectedResults_OnFailingCommandExecution() throws IOException {
        Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir"));
        String[] command = new String[] {"git", "status"};
        ProcessRunner.ExecutionResult executionResult = ProcessRunner.runProcessAndWait(tmpDir.toFile(), command);
        assertTrue(executionResult.getExitCode() != 0);
        assertTrue(executionResult.getStandardOutput().isEmpty());
        assertEquals("fatal: Not a git repository (or any of the parent directories): .git", executionResult.getStandardError().get(0));
    }

    @Test
    public void runProcess_PerformsAsExpected_OnSuccessfulCommandExecution() throws IOException {
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), File.separator, "test");
        ProcessRunner.runProcess(null, "mkdir", tempDir.toString());

        boolean runningOnWindows = runningOnWindows();
        String[] command = runningOnWindows ? new String[] {"dir", tempDir.toString()} : new String[] {"ls", "-la", tempDir.toString()};

        ProcessRunner.ExecutionResult executionResult = ProcessRunner.runProcessAndWait(null, command);
        assertEquals(executionResult.getExitCode(), 0);
        assertTrue(executionResult.getStandardError().isEmpty());
        if (runningOnWindows) {
            assertTrue(executionResult.getStandardOutput().isEmpty());
        }
        else {
            assertEquals("total 0", executionResult.getStandardOutput().get(0));
        }
    }

    private static boolean runningOnWindows() {
        return System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH).contains("win");
    }
}
