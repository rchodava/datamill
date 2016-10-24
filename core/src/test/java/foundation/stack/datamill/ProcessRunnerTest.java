package foundation.stack.datamill;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Israel Colomer (israelcolomer@gmail.com)
 */
public class ProcessRunnerTest {

    @Test
    public void runProcessAndWait_ReturnsExpectedResults_OnSuccessfulCommandExecution() throws IOException {
        ProcessRunner.ExecutionResult executionResult = ProcessRunner.runProcessAndWait(null, "echo", "hello");
        assertEquals(executionResult.getExitCode(), 0);
        assertTrue(executionResult.getErrorOutput().isEmpty());
        assertEquals(executionResult.getStandardOutput().get(0), "hello");
    }

    @Test
    public void runProcessAndWait_ReturnsExpectedResults_OnFailingCommandExecution() throws IOException {
        Path userHome = Paths.get(System.getProperty("user.home"));
        Path doesNotExist = Paths.get(userHome.toString(), "doesNotExist");
        ProcessRunner.ExecutionResult executionResult = ProcessRunner.runProcessAndWait(null, "ls", "-la", doesNotExist.toString());
        assertEquals(executionResult.getExitCode(), 1);
        assertTrue(executionResult.getStandardOutput().isEmpty());
        assertEquals(executionResult.getErrorOutput().get(0), "ls: " + doesNotExist.toString() + ": No such file or directory");
    }

    @Test
    public void runProcess_PerformsAsExpected_OnSuccessfulCommandExecution() throws IOException {
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "/test");
        ProcessRunner.runProcess(null, "mkdir", tempDir.toString());
        ProcessRunner.ExecutionResult executionResult = ProcessRunner.runProcessAndWait(null, "ls", "-la", tempDir.toString());
        assertEquals(executionResult.getExitCode(), 0);
        assertTrue(executionResult.getErrorOutput().isEmpty());
        assertEquals(executionResult.getStandardOutput().size(), 3);
        assertEquals(executionResult.getStandardOutput().get(0), "total 0");
    }
}
