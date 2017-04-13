package foundation.stack.datamill.configuration;

import foundation.stack.datamill.configuration.impl.*;
import rx.functions.Func1;

import java.io.IOException;

/**
 * Starting points for creating {@link PropertySourceChain}s.
 *
 * @see PropertySourceChain
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Properties {
    /** @see PropertySourceChain#orConstantsClass(Class) */
    public static <T> PropertySourceChain fromConstantsClass(Class<T> constantsClass) {
        return fromSource(new ConstantsClassSource<>(constantsClass));
    }

    /** @see PropertySourceChain#orFile(String) */
    public static PropertySourceChain fromFile(String path) {
        try {
            return fromSource(new FileSource(path));
        } catch (IOException e) {
            return fromSource(EmptySource.INSTANCE);
        }
    }

    /** @see PropertySourceChain#orSource(PropertySource) */
    public static PropertySourceChain fromSource(PropertySource source) {
        return new PropertySourceChainImpl(source);
    }

    /** @see PropertySourceChain#orEnvironment() */
    public static PropertySourceChain fromEnvironment() {
        return fromSource(EnvironmentPropertiesSource.IDENTITY);
    }

    /** @see PropertySourceChain#orEnvironment(Func1) */
    public static PropertySourceChain fromEnvironment(Func1<String, String> transformer) {
        return fromSource(new EnvironmentPropertiesSource(transformer));
    }

    /** @see PropertySourceChain#orSystem() */
    public static PropertySourceChain fromSystem() {
        return fromSource(SystemPropertiesSource.IDENTITY);
    }

    /** @see PropertySourceChain#orSystem(Func1) */
    public static PropertySourceChain fromSystem(Func1<String, String> transformer) {
        return fromSource(new SystemPropertiesSource(transformer));
    }
}
