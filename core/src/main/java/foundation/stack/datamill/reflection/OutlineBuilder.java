package foundation.stack.datamill.reflection;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import javassist.util.proxy.ProxyFactory;
import foundation.stack.datamill.reflection.impl.OutlineImpl;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.concurrent.ExecutionException;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class OutlineBuilder {
    private static final Objenesis objenesis = new ObjenesisStd();

    public static final OutlineBuilder DEFAULT = new OutlineBuilder();
    public static final OutlineBuilder CAMEL_CASED = new OutlineBuilder().defaultCamelCased();
    public static final OutlineBuilder SNAKE_CASED = new OutlineBuilder().defaultSnakeCased();

    private static <T> Outline<T> buildOutline(Class<T> classToOutline, boolean camelCased) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setSuperclass(classToOutline);
        Class<? extends T> outlineClass = proxyFactory.createClass();
        return new OutlineImpl<>(objenesis.newInstance(outlineClass), camelCased);
    }

    private final LoadingCache<Class<?>, Outline<?>> camelCasedOutlineCache;
    private final LoadingCache<Class<?>, Outline<?>> snakeCasedOutlineCache;

    private boolean camelCased;

    public OutlineBuilder() {
        this(false,
                CacheBuilder.newBuilder().build(new OutlineCacheLoader(true)),
                CacheBuilder.newBuilder().build(new OutlineCacheLoader(false)));
    }

    private OutlineBuilder(
            boolean camelCased,
            LoadingCache<Class<?>, Outline<?>> camelCasedOutlineCache,
            LoadingCache<Class<?>, Outline<?>> snakeCasedOutlineCache) {
        this.camelCased = camelCased;
        this.camelCasedOutlineCache = camelCasedOutlineCache;
        this.snakeCasedOutlineCache = snakeCasedOutlineCache;
    }

    public OutlineBuilder defaultCamelCased() {
        return CAMEL_CASED;
    }

    public OutlineBuilder defaultSnakeCased() {
        return SNAKE_CASED;
    }

    public <T> Outline<T> build(Class<T> classToOutline) {
        try {
            return camelCased ?
                    (Outline<T>) camelCasedOutlineCache.get(classToOutline) :
                    (Outline<T>) snakeCasedOutlineCache.get(classToOutline);
        } catch (ExecutionException e) {
            throw new ReflectionException(e.getCause());
        }
    }

    public <T> Bean<T> wrap(T instance) {
        return build((Class<T>) instance.getClass()).wrap(instance);
    }

    private static class OutlineCacheLoader extends CacheLoader<Class<?>, Outline<?>> {
        private final boolean camelCased;

        public OutlineCacheLoader(boolean camelCased) {
            this.camelCased = camelCased;
        }

        @Override
        public Outline<?> load(Class<?> key) throws Exception {
            return buildOutline(key, camelCased);
        }
    }
}
