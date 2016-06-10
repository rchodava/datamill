package foundation.stack.datamill.reflection;

import javassist.util.proxy.ProxyFactory;
import foundation.stack.datamill.reflection.impl.OutlineImpl;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class OutlineBuilder {
    private static final Objenesis objenesis = new ObjenesisStd();
    private boolean camelCased;

    public OutlineBuilder() {
    }

    public OutlineBuilder defaultCamelCased() {
        camelCased = true;
        return this;
    }

    public OutlineBuilder defaultSnakeCased() {
        camelCased = false;
        return this;
    }

    public <T> Outline<T> build(Class<T> classToOutline) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setSuperclass(classToOutline);
        Class<? extends T> outlineClass = proxyFactory.createClass();
        return new OutlineImpl<>(objenesis.newInstance(outlineClass), camelCased);
    }

    public <T> Bean<T> wrap(T instance) {
        return build((Class<T>) instance.getClass()).wrap(instance);
    }
}
