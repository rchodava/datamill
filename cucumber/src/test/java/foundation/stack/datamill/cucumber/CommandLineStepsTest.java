package foundation.stack.datamill.cucumber;

import com.google.common.io.Files;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class CommandLineStepsTest {
    @Test
    public void commandLineSteps() throws Exception {
        PropertyStore store = new PropertyStore();
        store.put("fileName", "test.txt");

        PlaceholderResolver resolver = new PlaceholderResolver(store);

        CommandLineSteps steps = new CommandLineSteps(store, resolver);

        steps.executeCommand("echo \"Hello\"");
        steps.executeCommandExpectingFailure("mkdir");

        File temporaryDirectory = (File) store.get(CommandLineSteps.TEMPORARY_DIRECTORY);
        Files.write("Hello", new File(temporaryDirectory, "test.txt"), Charset.defaultCharset());

        steps.verifyTemporaryDirectoryHasFiles(Collections.singletonList("test.txt"));
        steps.verifyTemporaryDirectoryHasFiles(Collections.singletonList("{fileName}"));

        Files.write("Hello", new File(temporaryDirectory, "test2.txt"), Charset.defaultCharset());
        steps.delete("test2.txt");
        assertFalse(new File(temporaryDirectory, "test2.txt").exists());

        steps.createFile("test3.txt", "Hello");
        steps.appendFile("test3.txt", "World");
        assertEquals("HelloWorld", Files.readFirstLine(new File(temporaryDirectory, "test3.txt"), Charset.defaultCharset()));

        steps.move("test3.txt", "test4.txt");
        assertFalse(new File(temporaryDirectory, "test3.txt").exists());
        assertTrue(new File(temporaryDirectory, "test4.txt").exists());

        steps.cleanUp();

        assertFalse(new File(temporaryDirectory, "test.txt").exists());
        assertFalse(new File(temporaryDirectory, "test4.txt").exists());
        assertFalse(temporaryDirectory.exists());
    }
}
