package org.chodavarapu.datamill.cucumber;

import com.google.common.io.Files;
import cucumber.api.java.After;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class CommandLineSteps {
    private static final Logger logger = LoggerFactory.getLogger(CommandLineSteps.class);
    static final String TEMPORARY_DIRECTORY = "$$temporaryDirectory";

    private final PlaceholderResolver placeholderResolver;
    private final PropertyStore propertyStore;

    public CommandLineSteps(PropertyStore propertyStore, PlaceholderResolver placeholderResolver) {
        this.propertyStore = propertyStore;
        this.placeholderResolver = placeholderResolver;
    }

    @When("^" + Phrases.SUBJECT + " executes \"(.+)\" from a temporary directory")
    public void executeCommand(String command) {
        String resolvedCommand = placeholderResolver.resolve(command);
        try {
            File temporaryDirectory = Files.createTempDir();
            propertyStore.put(TEMPORARY_DIRECTORY, temporaryDirectory);
            int result = Runtime.getRuntime().exec(resolvedCommand, null, temporaryDirectory).waitFor();
            if (result != 0) {
                fail("Received result code " + result + " after executing " + resolvedCommand);
            }
        } catch (InterruptedException | IOException e) {
            fail("Error while executing " + resolvedCommand);
        }
    }

    @Then("^the temporary directory should have the files:$")
    public void verifyTemporaryDirectoryHasFiles(List<String> names) throws Exception {
        File temporaryDirectory = (File) propertyStore.get(TEMPORARY_DIRECTORY);
        if (temporaryDirectory != null && temporaryDirectory.isDirectory()) {
            for (String name : names) {
                String resolved = placeholderResolver.resolve(name);
                File resolvedFile = new File(temporaryDirectory, resolved);

                assertTrue("Expected file " + resolvedFile.getAbsolutePath() + " does not exist!",
                        resolvedFile.exists());
            }
        } else {
            if (names.size() > 0) {
                fail("A temporary directory was not created to verify the existence of any files!");
            }
        }
    }

    private static void delete(File folder) throws IOException {
        java.nio.file.Files.walkFileTree(Paths.get(folder.getPath()), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                java.nio.file.Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                java.nio.file.Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @After
    public void cleanUp() throws IOException {
        File temporaryDirectory = (File) propertyStore.get(TEMPORARY_DIRECTORY);
        if (temporaryDirectory != null && temporaryDirectory.isDirectory()) {
            logger.debug("Cleaning up temporary directory {}", temporaryDirectory);
            delete(temporaryDirectory);
        }
    }
}
