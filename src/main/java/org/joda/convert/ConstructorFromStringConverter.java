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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * Conversion from a string using a fromString constructor.
 * <p>
 * The fromString constructor must take a single {@code String} parameter.
 * <p>
 * ConstructorFromStringConverter is thread-safe and immutable.
 * 
 * @param <T>  the type of the converter
 */
final class ConstructorFromStringConverter<T> implements TypedFromStringConverter<T> {

    /** Conversion from a string. */
    private final Constructor<T> fromString;

    /**
     * Creates an instance using a method and a constructor.
     * 
     * @param cls  the class this converts for, not null
     * @param fromString  the fromString method, not null
     * @throws RuntimeException (or subclass) if the method signatures are invalid
     */
    ConstructorFromStringConverter(Class<T> cls, Constructor<T> fromString) {
        if (cls.isInterface() || Modifier.isAbstract(cls.getModifiers()) || cls.isLocalClass() || cls.isMemberClass()) {
            throw new IllegalArgumentException("FromString constructor must be on an instantiable class: " + fromString);
        }
        if (fromString.getDeclaringClass() != cls) {
            throw new IllegalStateException("FromString constructor must be defined on specified class: " + fromString);
        }
        this.fromString = fromString;
    }

    //-----------------------------------------------------------------------
    @Override
    public T convertFromString(Class<? extends T> cls, String str) {
        try {
            return fromString.newInstance(str);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException("Constructor is not accessible: " + fromString);
        } catch (InstantiationException ex) {
            throw new IllegalStateException("Constructor is not valid: " + fromString);
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
        return fromString.getDeclaringClass();
    }

}
