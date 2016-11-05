package foundation.stack.datamill;

import com.google.common.base.Joiner;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

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

    public static ExecutionBuilder run(String... command) {
        return new ExecutionBuilder(new ProcessBuilder().command(command));
    }

    private static ListenableFuture<List<String>> readLinesFromStream(
            InputStream inputStream, boolean errorStream, Marker logMarker, boolean bufferOutput, ProcessOutputListener listener)
            throws InterruptedException {
        BufferedReader processOutput = new BufferedReader(new InputStreamReader(inputStream));
        List<String> output = new CopyOnWriteArrayList<>();
        return processStreamProcessors.submit(() -> {
            try {
                String line;
                do {
                    line = processOutput.readLine();
                    if (line != null) {
                        if (bufferOutput) {
                            output.add(line);
                        }

                        if (listener != null) {
                            listener.output(line, errorStream);
                        }

                        logger.debug(logMarker, line);
                    }
                } while (line != null && !Thread.interrupted());
            } catch (IOException e) {
            }
            return output;
        });
    }

    public static class ExecutionBuilder {
        private final ProcessBuilder builder;
        private boolean bufferOutput;
        private ProcessOutputListener outputListener;
        private Marker logMarker;

        public ExecutionBuilder(ProcessBuilder builder) {
            this.builder = builder;
        }

        public ExecutionBuilder workingDirectory(File workingDirectory) {
            builder.directory(workingDirectory);
            return this;
        }

        public ExecutionBuilder bufferOutput(boolean bufferOutput) {
            this.bufferOutput = bufferOutput;
            return this;
        }

        public ExecutionBuilder logMarker(Marker logMarker) {
            this.logMarker = logMarker;
            return this;
        }

        public ExecutionBuilder outputListener(ProcessOutputListener outputListener) {
            this.outputListener = outputListener;
            return this;
        }

        public ExecutionResult runAndWait() throws IOException {
            logger.debug(logMarker, "{}", Joiner.on(' ').join(builder.command()));

            Process process = builder.start();

            try {
                ListenableFuture<List<String>> standardOutputFuture, standardErrorFuture;

                standardOutputFuture = readLinesFromStream(process.getInputStream(), false, logMarker, bufferOutput, outputListener);
                standardErrorFuture = readLinesFromStream(process.getErrorStream(), true, logMarker, bufferOutput, outputListener);

                int exitCode = process.waitFor();

                if (bufferOutput) {
                    List<List<String>> results = Futures.allAsList(standardOutputFuture, standardErrorFuture)
                            .get(1, TimeUnit.SECONDS);
                    if (exitCode != 0 && results.size() == 2) {
                        logger.debug(logMarker, "Exit code was {} with standard error: {}", exitCode, results.get(1));
                    }

                    return new ExecutionResult(
                            exitCode,
                            results.size() > 0 ? results.get(0) : null,
                            results.size() > 1 ? results.get(1) : null);
                }

                return new ExecutionResult(exitCode);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new IOException(e);
            }
        }

        public void run() throws IOException {
            logger.debug(logMarker, "{}", Joiner.on(' ').join(builder.command()));

            Process process = builder.start();

            try {
                readLinesFromStream(process.getInputStream(), false, logMarker, bufferOutput, outputListener);
                readLinesFromStream(process.getErrorStream(), true, logMarker, bufferOutput, outputListener);
            } catch (InterruptedException e) {
                throw new IOException(e);
            }
        }
    }

    public static class ExecutionResult {
        private final int exitCode;
        private final List<String> bufferedStandardOutput;
        private final List<String> bufferedStandardError;

        public ExecutionResult(int exitCode, List<String> bufferedStandardOutput, List<String> bufferedStandardError) {
            this.exitCode = exitCode;
            this.bufferedStandardOutput = bufferedStandardOutput;
            this.bufferedStandardError = bufferedStandardError;
        }

        public ExecutionResult(int exitCode) {
            this.exitCode = exitCode;
            this.bufferedStandardOutput = null;
            this.bufferedStandardError = null;
        }

        public int getExitCode() {
            return exitCode;
        }

        public List<String> getBufferedStandardOutput() {
            return bufferedStandardOutput;
        }

        public List<String> getBufferedStandardError() {
            return bufferedStandardError;
        }
    }
}
