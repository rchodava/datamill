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
        ProcessRunner.ExecutionResult executionResult = ProcessRunner.run(command).bufferOutput(true).runAndWait();
        assertEquals(executionResult.getExitCode(), 0);
        // It is counter intuitive, but jdk uses standard error instead of standard output for showing version
        assertEquals(3, executionResult.getBufferedStandardError().size());
        assertTrue(executionResult.getBufferedStandardError().get(0).startsWith("java version "));
        assertTrue(executionResult.getBufferedStandardOutput().isEmpty());
    }

    @Test
    public void runProcessAndWait_NoOutput_OnSuccessfulCommandExecution() throws IOException {
        String[] command = new String[] {"java", "-version"};
        ProcessRunner.ExecutionResult executionResult = ProcessRunner.run(command).runAndWait();
        assertEquals(executionResult.getExitCode(), 0);
        assertNull(executionResult.getBufferedStandardError());
        assertNull(executionResult.getBufferedStandardOutput());
    }

    @Test
    public void runProcessAndWait_ReturnsExpectedResults_OnFailingCommandExecution() throws IOException {
        Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir"));
        String[] command = new String[] {"git", "status"};
        ProcessRunner.ExecutionResult executionResult = ProcessRunner.run(command)
                .bufferOutput(true).workingDirectory(tmpDir.toFile()).runAndWait();
        assertTrue(executionResult.getExitCode() != 0);
        assertTrue(executionResult.getBufferedStandardOutput().isEmpty());
        assertEquals("fatal: Not a git repository (or any of the parent directories): .git", executionResult.getBufferedStandardError().get(0));
    }

    @Test
    public void runProcess_PerformsAsExpected_OnSuccessfulCommandExecution() throws IOException {
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), File.separator, "test");
        ProcessRunner.run("mkdir", tempDir.toString()).run();

        boolean runningOnWindows = runningOnWindows();
        String[] command = runningOnWindows ? new String[] {"dir", tempDir.toString()} : new String[] {"ls", "-la", tempDir.toString()};

        ProcessRunner.ExecutionResult executionResult = ProcessRunner.run(command).bufferOutput(true).runAndWait();
        assertEquals(executionResult.getExitCode(), 0);
        assertTrue(executionResult.getBufferedStandardError().isEmpty());
        if (runningOnWindows) {
            assertTrue(executionResult.getBufferedStandardOutput().isEmpty());
        }
        else {
            assertEquals("total 0", executionResult.getBufferedStandardOutput().get(0));
        }
    }

    @Test
    public void runProcess_NotifiesListenerOfOutput() throws IOException {
        String[] command = new String[] {"java", "-version"};
        StringBuilder output = new StringBuilder();
        ProcessOutputListener listener = (message, error) -> output.append(message);
        ProcessRunner.ExecutionResult executionResult = ProcessRunner.run(command).outputListener(listener).runAndWait();
        assertEquals(executionResult.getExitCode(), 0);
        assertTrue(output.toString().startsWith("java version "));
    }

    private static boolean runningOnWindows() {
        return System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH).contains("win");
    }
}
