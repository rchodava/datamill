package org.chodavarapu.datamill.reflection.impl;

import com.google.common.base.CaseFormat;
import com.google.common.base.Defaults;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import org.chodavarapu.datamill.reflection.Outline;

import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class OutlineImpl<T> implements Outline<T> {
    private class OutlineMethodHandler implements MethodHandler {
        @Override
        public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
            lastInvokedMethod.set(thisMethod.getName());
            return Defaults.defaultValue(thisMethod.getReturnType());
        }
    }

    private final boolean camelCased;
    private final ThreadLocal<String> lastInvokedMethod = new ThreadLocal<>();
    private final T members;

    public OutlineImpl(T members, boolean camelCased) {
        this.members = members;
        ((Proxy) members).setHandler(new OutlineMethodHandler());

        this.camelCased = camelCased;
    }

    @Override
    public String camelCasedName() {
        return typeName();
    }

    @Override
    public String camelCasedName(Consumer<T> propertyInvoker) {
        propertyInvoker.accept(members());
        return camelCasedName((Boolean) null);
    }

    @Override
    public <P> String camelCasedName(P property) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, lastInvokedPropertyName());
    }

    private String lastInvokedPropertyName() {
        String method = lastInvokedMethod.get();
        if (method != null) {
            if (method.startsWith("get") || method.startsWith("set")) {
                return method.substring(3);
            } else if (method.startsWith("is")) {
                return method.substring(2);
            }
        }

        return method;
    }

    @Override
    public T members() {
        return members;
    }

    @Override
    public String name() {
        return camelCased ? camelCasedName() : snakeCasedName();
    }

    @Override
    public String name(Consumer<T> propertyInvoker) {
        return camelCased ? camelCasedName(propertyInvoker) : snakeCasedName(propertyInvoker);
    }

    @Override
    public <P> String name(P property) {
        return camelCased ? camelCasedName(property) : snakeCasedName(property);
    }

    @Override
    public String snakeCasedName() {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, typeName());
    }

    @Override
    public String snakeCasedName(Consumer<T> propertyInvoker) {
        propertyInvoker.accept(members());
        return snakeCasedName((Boolean) null);
    }

    @Override
    public <P> String snakeCasedName(P property) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, lastInvokedPropertyName());
    }

    private String typeName() {
        Class<?> type = members().getClass().getSuperclass();
        if (type != null) {
            return type.getSimpleName();
        }

        return null;
    }
}
