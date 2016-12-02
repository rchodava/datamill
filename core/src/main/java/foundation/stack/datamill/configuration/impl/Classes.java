package foundation.stack.datamill.configuration.impl;

/*
 * Portions of this code were copied from the Apache commons-lang project, notably the source of class
 * org.apache.commons.lang3.ClassUtils. The code is under the following terms:
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Classes {
    private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<>();

    static {
        primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
        primitiveWrapperMap.put(Byte.TYPE, Byte.class);
        primitiveWrapperMap.put(Character.TYPE, Character.class);
        primitiveWrapperMap.put(Short.TYPE, Short.class);
        primitiveWrapperMap.put(Integer.TYPE, Integer.class);
        primitiveWrapperMap.put(Long.TYPE, Long.class);
        primitiveWrapperMap.put(Double.TYPE, Double.class);
        primitiveWrapperMap.put(Float.TYPE, Float.class);
        primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
    }

    private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap = new HashMap<>();

    static {
        for (final Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperMap.entrySet()) {
            final Class<?> primitiveClass = entry.getKey();
            final Class<?> wrapperClass = entry.getValue();
            if (!primitiveClass.equals(wrapperClass)) {
                wrapperPrimitiveMap.put(wrapperClass, primitiveClass);
            }
        }
    }

    public static Class<?> primitiveToWrapper(final Class<?> clazz) {
        Class<?> convertedClass = clazz;
        if (clazz != null && clazz.isPrimitive()) {
            convertedClass = primitiveWrapperMap.get(clazz);
        }

        return convertedClass;
    }

    private static Class<?> wrapperToPrimitive(final Class<?> clazz) {
        return wrapperPrimitiveMap.get(clazz);
    }

    public static boolean isAssignable(Class<?> clazz, final Class<?> toClass) {
        if (toClass == null) {
            return false;
        }

        if (clazz == null) {
            return !toClass.isPrimitive();
        }

        if (clazz.isPrimitive() && !toClass.isPrimitive()) {
            clazz = primitiveToWrapper(clazz);
            if (clazz == null) {
                return false;
            }
        }
        if (toClass.isPrimitive() && !clazz.isPrimitive()) {
            clazz = wrapperToPrimitive(clazz);
            if (clazz == null) {
                return false;
            }
        }

        if (clazz.equals(toClass)) {
            return true;
        }
        if (clazz.isPrimitive()) {
            if (!toClass.isPrimitive()) {
                return false;
            }
            if (Integer.TYPE.equals(clazz)) {
                return Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            if (Long.TYPE.equals(clazz)) {
                return Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            if (Boolean.TYPE.equals(clazz)) {
                return false;
            }
            if (Double.TYPE.equals(clazz)) {
                return false;
            }
            if (Float.TYPE.equals(clazz)) {
                return Double.TYPE.equals(toClass);
            }
            if (Character.TYPE.equals(clazz)) {
                return Integer.TYPE.equals(toClass)
                        || Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            if (Short.TYPE.equals(clazz)) {
                return Integer.TYPE.equals(toClass)
                        || Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            if (Byte.TYPE.equals(clazz)) {
                return Short.TYPE.equals(toClass)
                        || Integer.TYPE.equals(toClass)
                        || Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            // should never get here
            return false;
        }

        return toClass.isAssignableFrom(clazz);
    }

    public static boolean isPrimitiveWrapper(Class<?> type) {
        return type == Boolean.class || type == Byte.class || type == Short.class ||
                type == Integer.class || type == Long.class || type == Float.class ||
                type == Double.class || type == Void.class || type == Character.class;
    }
}
