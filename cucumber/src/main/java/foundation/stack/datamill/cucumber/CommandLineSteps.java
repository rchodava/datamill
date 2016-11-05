package foundation.stack.datamill.cucumber;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import cucumber.api.java.After;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import foundation.stack.datamill.ProcessRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class CommandLineSteps {
    private static final Logger logger = LoggerFactory.getLogger(CommandLineSteps.class);
    static final String TEMPORARY_DIRECTORY = "$$temporaryDirectory";
    static final String COMMAND_RESULT = "$$commandResult";

    private final PlaceholderResolver placeholderResolver;
    private final PropertyStore propertyStore;

    public CommandLineSteps(PropertyStore propertyStore, PlaceholderResolver placeholderResolver) {
        this.propertyStore = propertyStore;
        this.placeholderResolver = placeholderResolver;
    }

    private File getOrCreateTemporaryDirectory() {
        File temporaryDirectory = (File) propertyStore.get(TEMPORARY_DIRECTORY);
        if (temporaryDirectory == null || !temporaryDirectory.isDirectory()) {
            temporaryDirectory = Files.createTempDir();
            logger.debug("Adding temporary directory to store: {}", temporaryDirectory);
            propertyStore.put(TEMPORARY_DIRECTORY, temporaryDirectory);
        }

        return temporaryDirectory;
    }

    @When("^" + Phrases.SUBJECT + " executes \"([^\"]+)\", it should fail$")
    public void executeCommandExpectingFailure(String command) {
        executeCommandExpectingFailureFromRelativeLocation(command, null);
    }

    @When("^" + Phrases.SUBJECT + " executes \"([^\"]+)\" from \"(.+)\", it should fail$")
    public void executeCommandExpectingFailureFromRelativeLocation(String command, String relativePath) {
        String resolvedCommand = placeholderResolver.resolve(command);
        String resolvedRelativePath = placeholderResolver.resolve(relativePath);

        try {
            File temporaryDirectory = getOrCreateTemporaryDirectory();
            File workingDirectory = resolvedRelativePath != null ?
                    new File(temporaryDirectory, resolvedRelativePath) : temporaryDirectory;
            int result = Runtime.getRuntime().exec(resolvedCommand, null, workingDirectory).waitFor();
            if (result == 0) {
                fail("Expected " + resolvedCommand + " to fail but got a zero result!");
            }
        } catch (InterruptedException | IOException e) {
            fail("Error while executing " + resolvedCommand);
        }
    }

    @When("^" + Phrases.SUBJECT + " moves \"(.+)\" to \"(.+)\" in the temporary directory$")
    public void move(String from, String to) throws IOException {
        File temporaryDirectory = (File) propertyStore.get(TEMPORARY_DIRECTORY);
        if (temporaryDirectory == null || !temporaryDirectory.isDirectory()) {
            fail("A temporary directory was not created to move files!");
        }

        String resolvedFrom = placeholderResolver.resolve(from);
        String resolvedTo = placeholderResolver.resolve(to);

        File fromFile = new File(temporaryDirectory, resolvedFrom);
        File toFile = new File(temporaryDirectory, resolvedTo);

        if (fromFile.exists()) {
            Files.move(fromFile, toFile);
        } else {
            fail("Failed to move non-existent file " + fromFile.getAbsolutePath());
        }
    }

    @When("^" + Phrases.SUBJECT + " creates \"(.+)\" in (?:a|the) temporary directory with content:$")
    public void createFile(String file, String content) throws IOException {
        doCreateFile(file, placeholderResolver.resolve(content));
    }

    @When("^" + Phrases.SUBJECT + " creates \"(.+)\" in (?:a|the) temporary directory with content as is:$")
    public void createFileWithResolvedContent(String file, String content) throws IOException {
        doCreateFile(file, content);
    }

    public void doCreateFile(String file, String content) throws IOException {
        File temporaryDirectory = getOrCreateTemporaryDirectory();

        String resolvedFile = placeholderResolver.resolve(file);

        File fileWithinDirectory = new File(temporaryDirectory, resolvedFile);
        Files.write(content, fileWithinDirectory, Charset.defaultCharset());
    }

    @When("^" + Phrases.SUBJECT + " appends \"(.+)\" in the temporary directory with content:$")
    public void appendFile(String file, String content) throws IOException {
        File temporaryDirectory = (File) propertyStore.get(TEMPORARY_DIRECTORY);
        if (temporaryDirectory == null || !temporaryDirectory.isDirectory()) {
            fail("A temporary directory was not created!");
        }

        String resolvedFile = placeholderResolver.resolve(file);
        String resolvedContent = placeholderResolver.resolve(content);

        File fileWithinDirectory = new File(temporaryDirectory, resolvedFile);
        Files.append(resolvedContent, fileWithinDirectory, Charset.defaultCharset());
    }

    @When("^" + Phrases.SUBJECT + " deletes \"(.+)\" from the temporary directory$")
    public void delete(String file) throws IOException {
        File temporaryDirectory = (File) propertyStore.get(TEMPORARY_DIRECTORY);
        if (temporaryDirectory == null || !temporaryDirectory.isDirectory()) {
            fail("A temporary directory was not created to delete files from!");
        }

        String resolvedFile = placeholderResolver.resolve(file);
        File target = new File(temporaryDirectory, resolvedFile);

        if (target.isFile()) {
            if (!target.delete()) {
                fail("Failed to delete file " + target.getAbsolutePath());
            }
        } else {
            delete(target);
            if (target.isDirectory()) {
                fail("Failed to delete folder " + target.getAbsolutePath());
            }
        }
    }

    @When("^" + Phrases.SUBJECT + " executes \"(.+)\" from \"(.+)\" relative to (?:a|the) temporary directory$")
    public void executeCommandFromRelativeLocation(String command, String relativePath) {
        String resolvedCommand = placeholderResolver.resolve(command);
        String resolvedRelativePath = placeholderResolver.resolve(relativePath);

        try {
            File temporaryDirectory = getOrCreateTemporaryDirectory();
            File workingDirectory = resolvedRelativePath != null ? new File(temporaryDirectory, resolvedRelativePath) : temporaryDirectory;
            ProcessRunner.ExecutionResult executionResult = ProcessRunner.run(resolvedCommand.split(" "))
                    .workingDirectory(workingDirectory).runAndWait();
            if (executionResult.getExitCode() != 0) {
                logger.error("Error while executing {} with message {}", resolvedCommand, executionResult.getBufferedStandardError());
                fail("Received result code " + executionResult.getExitCode() + " after executing " + resolvedCommand);
            }
            if (executionResult.getBufferedStandardOutput() != null && !executionResult.getBufferedStandardOutput().isEmpty()) {
                String commandResult = Joiner.on(System.getProperty("line.separator")).join(executionResult.getBufferedStandardOutput());
                logger.debug("Command execution of [{}] returned following results: {}", resolvedCommand, commandResult);
                propertyStore.put(COMMAND_RESULT, commandResult);
            }
        } catch (IOException e) {
            logger.error("Error while executing {}", resolvedCommand, e);
            fail("Error while executing " + resolvedCommand);
        }
    }

    @When("^" + Phrases.SUBJECT + " executes \"(.+)\" from (?:a|the) temporary directory$")
    public void executeCommand(String command) {
        executeCommandFromRelativeLocation(command, null);
    }

    @Then("^the temporary directory should have the files:$")
    public void verifyTemporaryDirectoryHasFiles(List<String> names) throws Exception {
        File temporaryDirectory = (File) propertyStore.get(TEMPORARY_DIRECTORY);
        doVerifyDirectoryHasFiles(temporaryDirectory, names);
    }

    @Then("^the temporary directory for \"(.+)\" should have the files:$")
    public void verifySpecificTemporaryDirectoryHasFiles(String subDirectory, List<String> names) throws Exception {
        File temporaryDirectory = Paths.get(((File) propertyStore.get(TEMPORARY_DIRECTORY)).getAbsolutePath(), subDirectory).toFile();
        doVerifyDirectoryHasFiles(temporaryDirectory, names);
    }

    private void doVerifyDirectoryHasFiles(File temporaryDirectory, List<String> names) {
        if (temporaryDirectory != null && temporaryDirectory.isDirectory()) {
            for (String name : names) {
                String resolved = placeholderResolver.resolve(name);
                File resolvedFile = new File(temporaryDirectory, resolved);

                assertTrue("Expected file " + resolvedFile.getAbsolutePath() + " does not exist!",
                        resolvedFile.exists());
            }
        } else {
            if (!names.isEmpty()) {
                fail("A temporary directory was not created to verify the existence of any files!");
            }
        }
    }

    @Then("^the command output should not contain \"(.+)\"$")
    public void verifyCommandExecutionResultDoesNotContain(String expectedCommandExecutionResult) throws Exception {
        String resolvedExpectedCommandExecutionResult = placeholderResolver.resolve(expectedCommandExecutionResult);
        String actualCommandExecutionResult = (String) propertyStore.get(COMMAND_RESULT);
        assertFalse(actualCommandExecutionResult.contains(resolvedExpectedCommandExecutionResult));
    }

    @Then("^the command output should contain \"(.+)\"$")
    public void verifyCommandExecutionResultContains(String expectedCommandExecutionResult) throws Exception {
        String resolvedExpectedCommandExecutionResult = placeholderResolver.resolve(expectedCommandExecutionResult);
        String actualCommandExecutionResult = (String) propertyStore.get(COMMAND_RESULT);
        assertTrue(actualCommandExecutionResult.contains(resolvedExpectedCommandExecutionResult));
    }

    @Then("^the command output should be \"(.+)\"$")
    public void verifyCommandExecutionResultIs(String expectedCommandExecutionResult) throws Exception {
        String resolvedExpectedCommandExecutionResult = placeholderResolver.resolve(expectedCommandExecutionResult);
        String actualCommandExecutionResult = (String) propertyStore.get(COMMAND_RESULT);
        assertEquals(actualCommandExecutionResult, resolvedExpectedCommandExecutionResult);
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
