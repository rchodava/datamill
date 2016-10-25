package foundation.stack.datamill;

import com.google.common.base.Joiner;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ProcessRunner {
    private static final Logger logger = LoggerFactory.getLogger(ProcessRunner.class);

    private static final ListeningExecutorService processStreamProcessors =
            MoreExecutors.listeningDecorator(Executors.newCachedThreadPool(new ThreadFactory() {
                private final ThreadFactory threadFactory = Executors.defaultThreadFactory();

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = threadFactory.newThread(r);
                    if (thread != null) {
                        thread.setDaemon(true);
                    }

                    return thread;
                }
            }));

    public static void runProcess(File workingDirectory, String... command) throws IOException {
        logger.debug("{}", Joiner.on(' ').join(command));

        Process process = new ProcessBuilder().directory(workingDirectory).command(command).start();

        try {
            readLinesFromStream(process.getInputStream());
            readLinesFromStream(process.getErrorStream());
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    public static ExecutionResult runProcessAndWait(File workingDirectory, String... command) throws IOException {
        return runProcessAndWait(workingDirectory, true, command);
    }

    public static ExecutionResult runProcessAndWait(File workingDirectory, boolean returnOutput, String... command) throws IOException {
        logger.debug("{}", Joiner.on(' ').join(command));

        Process process = new ProcessBuilder().directory(workingDirectory).command(command).start();

        try {
            ListenableFuture<List<String>> standardOutputFuture = null, standardErrorFuture = null;
            if (returnOutput) {
                standardOutputFuture = readLinesFromStream(process.getInputStream());
                standardErrorFuture = readLinesFromStream(process.getErrorStream());
            }

            int exitCode = process.waitFor();

            if (returnOutput) {
                List<List<String>> results = Futures.allAsList(standardOutputFuture, standardErrorFuture).get(1, TimeUnit.SECONDS);
                return new ExecutionResult(exitCode, results.size() > 0 ? results.get(0) : null, results.size() > 1 ? results.get(1) : null);
            }
            return new ExecutionResult(exitCode);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new IOException(e);
        }
    }

    private static ListenableFuture<List<String>> readLinesFromStream(InputStream inputStream) throws InterruptedException {
        BufferedReader processOutput = new BufferedReader(new InputStreamReader(inputStream));
        List<String> output = new CopyOnWriteArrayList<>();
        return processStreamProcessors.submit(() -> {
            try {
                String line;
                do {
                    line = processOutput.readLine();
                    if (line != null) {
                        output.add(line);
                        logger.debug(line);
                    }
                } while (line != null && !Thread.interrupted());
            } catch (IOException e) {
            }
            return output;
        });
    }

    public static class ExecutionResult {
        private final int exitCode;
        private final List<String> standardOutput;
        private final List<String> standardError;

        public ExecutionResult(int exitCode, List<String> standardOutput, List<String> standardError) {
            this.exitCode = exitCode;
            this.standardOutput = standardOutput;
            this.standardError = standardError;
        }

        public ExecutionResult(int exitCode) {
            this.exitCode = exitCode;
            this.standardOutput = null;
            this.standardError = null;
        }

        public int getExitCode() {
            return exitCode;
        }

        public List<String> getStandardOutput() {
            return standardOutput;
        }

        public List<String> getStandardError() {
            return standardError;
        }
    }
}

