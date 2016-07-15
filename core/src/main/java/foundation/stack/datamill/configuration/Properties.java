package foundation.stack.datamill.configuration;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import foundation.stack.datamill.values.Value;
import rx.functions.Action0;
import rx.functions.Action1;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Properties {
    private static LoadingCache<String, PropertiesFile> files = CacheBuilder.<String, PropertiesFile>newBuilder().build(
            new CacheLoader<String, PropertiesFile>() {
                @Override
                public PropertiesFile load(String path) throws Exception {
                    return loadFile(path);
                }
            });

    private static PropertiesFile loadFile(String path) throws IOException {
        return new PropertiesFile(path);
    }

    private static void withFile(String path, Action1<PropertiesFile> consumer, boolean required) {
        try {
            consumer.call(files.get(path));
        } catch (ExecutionException e) {
            if (required) {
                throw new IllegalArgumentException("Failed to retrieve properties from file " + path, e);
            }
        }
    }

    public static void withRequiredFile(String path, Action1<PropertiesFile> consumer) {
        withFile(path, consumer, true);
    }

    public static void withOptionalFile(String path, Action1<PropertiesFile> consumer) {
        withFile(path, consumer, false);
    }

    public static Optional<Value> fromFile(String path, String name) {
        try {
            return files.get(path).getOptional(name);
        } catch (ExecutionException e) {
            return Optional.empty();
        }
    }

    public static ElseBuilder ifFileExists(String path, Action1<PropertiesFile> consumer) {
        try {
            consumer.call(files.get(path));
            return action -> {
            };
        } catch (ExecutionException e) {
            return action -> action.call();
        }
    }

    public interface ElseBuilder {
        void orElse(Action0 action);
    }
}
