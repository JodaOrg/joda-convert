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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Conversion to and from a string using reflection.
 * <p>
 * The toString method must meet the following signature:<br />
 * {@code String anyName()} on Class T.
 * <p>
 * ReflectionStringConverter is abstract, but all known implementations are thread-safe and immutable.
 * 
 * @param <T>  the type of the converter
 */
final class ReflectionStringConverter<T> implements TypedStringConverter<T> {

    /** The converted class. */
    private final Class<T> cls;
    /** Conversion to a string. */
    private final Method toString;
    /** Conversion from a string, package-scoped for testing. */
    final TypedFromStringConverter<T> fromString;

    /**
     * Creates an instance using two methods.
     * 
     * @param cls  the class this converts for, null creates a from-string converter
     * @param toString  the toString method, not null
     * @param fromString  the fromString converter, not null
     * @throws RuntimeException (or subclass) if the method signatures are invalid
     */
    ReflectionStringConverter(Class<T> cls, Method toString, TypedFromStringConverter<T> fromString) {
        if (toString.getParameterTypes().length != 0) {
            throw new IllegalStateException("ToString method must have no parameters: " + toString);
        }
        if (toString.getReturnType() != String.class) {
            throw new IllegalStateException("ToString method must return a String: " + toString);
        }
        this.cls = cls;
        this.toString = toString;
        this.fromString = fromString;
    }

    //-----------------------------------------------------------------------
    @Override
    public String convertToString(T object) {
        try {
            return (String) toString.invoke(object);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException("Method is not accessible: " + toString);
        } catch (InvocationTargetException ex) {
            if (ex.getCause() instanceof RuntimeException) {
                throw (RuntimeException) ex.getCause();
            }
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public T convertFromString(Class<? extends T> cls, String str) {
        return fromString.convertFromString(cls, str);
    }

    @Override
    public Class<?> getEffectiveType() {
        return fromString.getEffectiveType();
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
        return "RefectionStringConverter[" + cls.getSimpleName() + "]";
    }

}
