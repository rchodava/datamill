package org.chodavarapu.datamill.cucumber;

import com.google.common.io.Files;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class CommandLineStepsTest {
    @Test
    public void executeAndVerifyFiles() throws Exception {
        PropertyStore store = new PropertyStore();
        store.put("fileName", "test.txt");

        PlaceholderResolver resolver = new PlaceholderResolver(store);

        CommandLineSteps steps = new CommandLineSteps(store, resolver);

        steps.executeCommand("echo \"Hello\"");
        steps.executeCommandExpectingFailure("ping 256.256.256.256");

        File temporaryDirectory = (File) store.get(CommandLineSteps.TEMPORARY_DIRECTORY);
        Files.write("Hello", new File(temporaryDirectory, "test.txt"), Charset.defaultCharset());

        steps.verifyTemporaryDirectoryHasFiles(Arrays.asList("test.txt"));
        steps.verifyTemporaryDirectoryHasFiles(Arrays.asList("{fileName}"));

        steps.cleanUp();

        assertFalse(new File(temporaryDirectory, "test.txt").exists());
        assertFalse(temporaryDirectory.exists());
    }
}
