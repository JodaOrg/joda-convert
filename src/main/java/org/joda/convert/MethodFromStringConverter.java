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
import java.lang.reflect.Modifier;

/**
 * Conversion from a string using a static method.
 * <p>
 * The fromString method must meet the following signature:<br />
 * {@code static T anyName(String)} on any class.
 * <p>
 * MethodFromStringConverter is thread-safe and immutable.
 * 
 * @param <T>  the type of the converter
 */
final class MethodFromStringConverter<T> implements TypedFromStringConverter<T> {

    /** Conversion from a string. */
    private final Method fromString;
    /** Effective type. */
    private final Class<?> effectiveType;

    /**
     * Creates an instance using two methods.
     * 
     * @param cls  the class this converts for, not null
     * @param fromString  the fromString method, not null
     * @throws RuntimeException (or subclass) if the method signatures are invalid
     */
    MethodFromStringConverter(Class<T> cls, Method fromString, Class<?> effectiveType) {
        if (Modifier.isStatic(fromString.getModifiers()) == false) {
            throw new IllegalStateException("FromString method must be static: " + fromString);
        }
        if (fromString.getParameterTypes().length != 1) {
            throw new IllegalStateException("FromString method must have one parameter: " + fromString);
        }
        Class<?> param = fromString.getParameterTypes()[0];
        if (param != String.class && param != CharSequence.class) {
            throw new IllegalStateException("FromString method must take a String or CharSequence: " + fromString);
        }
        if (fromString.getReturnType().isAssignableFrom(cls) == false && cls.isAssignableFrom(fromString.getReturnType()) == false) {
            throw new IllegalStateException("FromString method must return specified class or a supertype: " + fromString);
        }
        this.fromString = fromString;
        this.effectiveType = effectiveType;
    }

    //-----------------------------------------------------------------------
    @Override
    public T convertFromString(Class<? extends T> cls, String str) {
        try {
            return cls.cast(fromString.invoke(null, str));
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException("Method is not accessible: " + fromString);
        } catch (InvocationTargetException ex) {
            if (ex.getCause() instanceof RuntimeException) {
                throw (RuntimeException) ex.getCause();
            }
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }

    //-------------------------------------------------------------------------
    @Override
    public Class<?> getEffectiveType() {
        return effectiveType;
    }

}
