/*
 *  Copyright 2010-present Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.joda.convert;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Factory for {@code StringConverter} looking up annotations.
 * <p>
 * This class is immutable and thread-safe.
 * 
 * @since 1.5
 */
final class AnnotationStringConverterFactory implements StringConverterFactory {

    /**
     * Singleton instance.
     */
    static final AnnotationStringConverterFactory INSTANCE = new AnnotationStringConverterFactory();

    /**
     * Restricted constructor.
     */
    private AnnotationStringConverterFactory() {
    }

    //-----------------------------------------------------------------------
    /**
     * Finds a converter by type.
     * 
     * @param cls  the type to lookup, not null
     * @return the converter, null if not found
     * @throws RuntimeException (or subclass) if source code is invalid
     */
    @Override
    public StringConverter<?> findConverter(Class<?> cls) {
        return findAnnotatedConverter(cls);  // capture generics
    }

    /**
     * Finds a converter searching annotated.
     * 
     * @param <T>  the type of the converter
     * @param cls  the class to find a method for, not null
     * @return the converter, not null
     * @throws RuntimeException if none found
     */
    private <T> StringConverter<T> findAnnotatedConverter(Class<T> cls) {
        var toString = findToStringMethod(cls);  // checks superclasses
        if (toString == null) {
            return null;
        }
        TypedFromStringConverter<T> fromString = findAnnotatedFromStringConverter(cls);
        if (fromString == null) {
            throw new IllegalStateException("Class annotated with @ToString but not with @FromString: " + cls.getName());
        }
        return new ReflectionStringConverter<>(cls, toString, fromString);
    }

    /**
     * Finds a from-string converter by type.
     * 
     * @param <T>  the type of the converter
     * @param cls  the type to lookup, not null
     * @return the converter, null if not found
     * @throws RuntimeException (or subclass) if source code is invalid
     */
    <T> TypedFromStringConverter<T> findFromStringConverter(Class<T> cls) {
        return findAnnotatedFromStringConverter(cls);  // capture generics
    }

    /**
     * Finds a from-string converter.
     * 
     * @param <T>  the type of the converter
     * @param cls  the class to find a method for, not null
     * @return the converter, null if not found
     * @throws RuntimeException if none found
     */
    private <T> TypedFromStringConverter<T> findAnnotatedFromStringConverter(Class<T> cls) {
        var con = findFromStringConstructor(cls);
        var mth = findFromStringMethod(cls, con == null);  // optionally checks superclasses
        if (con != null && mth != null) {
            throw new IllegalStateException("Both method and constructor are annotated with @FromString: " + cls.getName());
        }
        return (con != null ? con : mth);
    }

    //-------------------------------------------------------------------------
    /**
     * Finds the conversion method.
     * 
     * @param cls  the class to find a method for, not null
     * @return the method to call, null means use {@code toString}
     * @throws RuntimeException if invalid
     */
    private Method findToStringMethod(Class<?> cls) {
        Method matched = null;
        // find in superclass hierarchy
        Class<?> loopCls = cls;
        while (loopCls != null && matched == null) {
            var methods = loopCls.getDeclaredMethods();
            for (var method : methods) {
                if (!method.isBridge() && !method.isSynthetic()) {
                    var toString = method.getAnnotation(ToString.class);
                    if (toString != null) {
                        if (matched != null) {
                            throw new IllegalStateException("Two methods are annotated with @ToString: " + cls.getName());
                        }
                        matched = method;
                    }
                }
            }
            loopCls = loopCls.getSuperclass();
        }
        // find in immediate parent interfaces
        if (matched == null) {
            for (var loopIfc : eliminateEnumSubclass(cls).getInterfaces()) {
                var methods = loopIfc.getDeclaredMethods();
                for (var method : methods) {
                    if (!method.isBridge() && !method.isSynthetic()) {
                        var toString = method.getAnnotation(ToString.class);
                        if (toString != null) {
                            if (matched != null) {
                                throw new IllegalStateException("Two methods are annotated with @ToString on interfaces: " + cls.getName());
                            }
                            matched = method;
                        }
                    }
                }
            }
        }
        return matched;
    }

    //-------------------------------------------------------------------------
    /**
     * Finds the conversion method.
     * 
     * @param <T>  the type of the converter
     * @param cls  the class to find a method for, not null
     * @return the method to call, null means none found
     * @throws RuntimeException if invalid
     */
    private <T> TypedFromStringConverter<T> findFromStringConstructor(Class<T> cls) {
        Constructor<T> con;
        try {
            con = cls.getDeclaredConstructor(String.class);
        } catch (NoSuchMethodException ex) {
            try {
                con = cls.getDeclaredConstructor(CharSequence.class);
            } catch (NoSuchMethodException ex2) {
                return null;
            }
        }
        var fromString = con.getAnnotation(FromString.class);
        if (fromString == null) {
            return null;
        }
        return new ConstructorFromStringConverter<>(cls, con);
    }

    /**
     * Finds the conversion method.
     * 
     * @param cls  the class to find a method for, not null
     * @param searchSuperclasses  whether to search superclasses
     * @return the method to call, null means not found
     * @throws RuntimeException if invalid
     */
    private <T> TypedFromStringConverter<T> findFromStringMethod(Class<T> cls, boolean searchSuperclasses) {
        // find in superclass hierarchy
        Class<?> loopCls = cls;
        while (loopCls != null) {
            var fromString = findFromString(loopCls);
            if (fromString != null) {
                return new MethodFromStringConverter<>(cls, fromString, loopCls);
            }
            if (searchSuperclasses == false) {
                break;
            }
            loopCls = loopCls.getSuperclass();
        }
        // find in immediate parent interfaces
        TypedFromStringConverter<T> matched = null;
        if (searchSuperclasses) {
            for (var loopIfc : eliminateEnumSubclass(cls).getInterfaces()) {
                var fromString = findFromString(loopIfc);
                if (fromString != null) {
                    if (matched != null) {
                        throw new IllegalStateException("Two different interfaces are annotated with " +
                            "@FromString or @FromStringFactory: " + cls.getName());
                    }
                    matched = new MethodFromStringConverter<>(cls, fromString, loopIfc);
                }
            }
        }
        return matched;
    }

    /**
     * Finds the conversion method.
     * 
     * @param cls  the class to find a method for, not null
     * @param matched  the matched method, may be null
     * @return the method to call, null means not found
     * @throws RuntimeException if invalid
     */
    private Method findFromString(Class<?> cls) {
        // find in declared methods
        var methods = cls.getDeclaredMethods();
        Method matched = null;
        for (var method : methods) {
            if (!method.isBridge() && !method.isSynthetic()) {
                var fromString = method.getAnnotation(FromString.class);
                if (fromString != null) {
                    if (matched != null) {
                        throw new IllegalStateException("Two methods are annotated with @FromString: " + cls.getName());
                    }
                    matched = method;
                }
            }
        }
        // check for factory
        var factory = cls.getAnnotation(FromStringFactory.class);
        if (factory != null) {
            if (matched != null) {
                throw new IllegalStateException("Class annotated with @FromString and @FromStringFactory: " + cls.getName());
            }
            var factoryMethods = factory.factory().getDeclaredMethods();
            for (var method : factoryMethods) {
                if (!method.isBridge() && !method.isSynthetic()) {
                    // handle factory containing multiple FromString for different types
                    if (cls.isAssignableFrom(method.getReturnType())) {
                        var fromString = method.getAnnotation(FromString.class);
                        if (fromString != null) {
                            if (matched != null) {
                                throw new IllegalStateException("Two methods are annotated with @FromString on the factory: " + factory.factory().getName());
                            }
                            matched = method;
                        }
                    }
                }
            }
        }
        return matched;
    }

    // eliminates enum subclass as they are pesky
    private Class<?> eliminateEnumSubclass(Class<?> cls) {
        var sup = cls.getSuperclass();
        if (sup != null && sup.getSuperclass() == Enum.class) {
            return sup;
        }
        return cls;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
