package org.chodavarapu.datamill.reflection;

import javassist.util.proxy.ProxyFactory;
import org.chodavarapu.datamill.reflection.impl.OutlineImpl;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class OutlineBuilder<T> {
    private static final Objenesis objenesis = new ObjenesisStd();
    private final Class<T> outlinedClass;
    private boolean camelCased;

    public OutlineBuilder(Class<T> outlinedClass) {
        this.outlinedClass = outlinedClass;
    }

    public OutlineBuilder<T> defaultCamelCased() {
        camelCased = true;
        return this;
    }

    public OutlineBuilder<T> defaultSnakeCased() {
        camelCased = false;
        return this;
    }

    public Outline<T> build() {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setSuperclass(outlinedClass);
        Class<? extends T> outlineClass = proxyFactory.createClass();
        return new OutlineImpl<>(objenesis.newInstance(outlineClass), camelCased);
    }
}
