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
    static final StringConverterFactory INSTANCE = new AnnotationStringConverterFactory();

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
    private <T> StringConverter<T> findAnnotatedConverter(final Class<T> cls) {
        Method toString = findToStringMethod(cls);  // checks superclasses
        if (toString == null) {
            return null;
        }
        MethodConstructorStringConverter<T> con = findFromStringConstructor(cls, toString);
        MethodsStringConverter<T> mth = findFromStringMethod(cls, toString, con == null);  // optionally checks superclasses
        if (con == null && mth == null) {
            throw new IllegalStateException("Class annotated with @ToString but not with @FromString: " + cls.getName());
        }
        if (con != null && mth != null) {
            throw new IllegalStateException("Both method and constructor are annotated with @FromString: " + cls.getName());
        }
        return (con != null ? con : mth);
    }

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
            Method[] methods = loopCls.getDeclaredMethods();
            for (Method method : methods) {
                if (!method.isBridge() && !method.isSynthetic()) {
                    ToString toString = method.getAnnotation(ToString.class);
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
            for (Class<?> loopIfc : eliminateEnumSubclass(cls).getInterfaces()) {
                Method[] methods = loopIfc.getDeclaredMethods();
                for (Method method : methods) {
                    if (!method.isBridge() && !method.isSynthetic()) {
                        ToString toString = method.getAnnotation(ToString.class);
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

    /**
     * Finds the conversion method.
     * 
     * @param <T>  the type of the converter
     * @param cls  the class to find a method for, not null
     * @param toString  the toString method, not null
     * @return the method to call, null means none found
     * @throws RuntimeException if invalid
     */
    private <T> MethodConstructorStringConverter<T> findFromStringConstructor(Class<T> cls, Method toString) {
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
        FromString fromString = con.getAnnotation(FromString.class);
        if (fromString == null) {
            return null;
        }
        return new MethodConstructorStringConverter<T>(cls, toString, con);
    }

    /**
     * Finds the conversion method.
     * 
     * @param cls  the class to find a method for, not null
     * @param toString  the toString method, not null
     * @param searchSuperclasses  whether to search superclasses
     * @return the method to call, null means not found
     * @throws RuntimeException if invalid
     */
    private <T> MethodsStringConverter<T> findFromStringMethod(Class<T> cls, Method toString, boolean searchSuperclasses) {
        // find in superclass hierarchy
        Class<?> loopCls = cls;
        while (loopCls != null) {
            Method fromString = findFromString(loopCls);
            if (fromString != null) {
                return new MethodsStringConverter<T>(cls, toString, fromString, loopCls);
            }
            if (searchSuperclasses == false) {
                break;
            }
            loopCls = loopCls.getSuperclass();
        }
        // find in immediate parent interfaces
        MethodsStringConverter<T> matched = null;
        if (searchSuperclasses) {
            for (Class<?> loopIfc : eliminateEnumSubclass(cls).getInterfaces()) {
                Method fromString = findFromString(loopIfc);
                if (fromString != null) {
                    if (matched != null) {
                        throw new IllegalStateException("Two different interfaces are annotated with " +
                            "@FromString or @FromStringFactory: " + cls.getName());
                    }
                    matched = new MethodsStringConverter<T>(cls, toString, fromString, loopIfc);
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
        Method[] methods = cls.getDeclaredMethods();
        Method matched = null;
        for (Method method : methods) {
            if (!method.isBridge() && !method.isSynthetic()) {
                FromString fromString = method.getAnnotation(FromString.class);
                if (fromString != null) {
                    if (matched != null) {
                        throw new IllegalStateException("Two methods are annotated with @FromString: " + cls.getName());
                    }
                    matched = method;
                }
            }
        }
        // check for factory
        FromStringFactory factory = cls.getAnnotation(FromStringFactory.class);
        if (factory != null) {
            if (matched != null) {
                throw new IllegalStateException("Class annotated with @FromString and @FromStringFactory: " + cls.getName());
            }
            Method[] factoryMethods = factory.factory().getDeclaredMethods();
            for (Method method : factoryMethods) {
                if (!method.isBridge() && !method.isSynthetic()) {
                    // handle factory containing multiple FromString for different types
                    if (cls.isAssignableFrom(method.getReturnType())) {
                        FromString fromString = method.getAnnotation(FromString.class);
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
        Class<?> sup = cls.getSuperclass();
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
