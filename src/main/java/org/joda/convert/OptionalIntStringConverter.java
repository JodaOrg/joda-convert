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

import java.lang.reflect.Method;

/**
 * Convert Java SE 8 {@code OptionalInt}.
 * <p>
 * This class is immutable and thread-safe.
 * 
 * @since 1.8
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
final class OptionalIntStringConverter<T> implements TypedStringConverter<T> {

    /** The converted class. */
    private final Class<T> cls;
    /** Conversion to a string. */
    private final Method methodPresent;
    /** Conversion to a string. */
    private final Method methodGet;
    /** Conversion from a string. */
    private final Method methodOf;
    /** Conversion from a string. */
    private final T empty;

    /**
     * Singleton instance.
     */
    static final TypedStringConverter<?> INSTANCE;
    static {
        OptionalIntStringConverter<?> instance;
        try {
            Class<?> cls = RenameHandler.INSTANCE.loadType("java.util.OptionalInt");
            Method methodPresent = cls.getDeclaredMethod("isPresent");
            Method methodGet = cls.getDeclaredMethod("getAsInt");
            Method methodOf = cls.getDeclaredMethod("of", Integer.TYPE);
            Method methodEmpty = cls.getDeclaredMethod("empty");
            Object empty = methodEmpty.invoke(null);
            instance = new OptionalIntStringConverter(cls, methodPresent, methodGet, methodOf, empty);
        } catch (Exception ex) {
            instance = null;
        }
        INSTANCE = instance;
    }

    /**
     * Restricted constructor.
     * @param cls  the optional double type
     * @param methodPresent  the isPresent() method
     * @param methodGet  the getter method
     * @param methodOf  the factory method
     * @param empty  the empty optional instance
     */
    private OptionalIntStringConverter(
                    Class<T> cls, Method methodPresent, Method methodGet, Method methodOf, T empty) {
        this.cls = cls;
        this.methodPresent = methodPresent;
        this.methodGet = methodGet;
        this.methodOf = methodOf;
        this.empty = empty;
    }

    //-----------------------------------------------------------------------
    @Override
    public String convertToString(T object) {
        try {
            if (methodPresent.invoke(object).equals(Boolean.TRUE)) {
                try {
                    return methodGet.invoke(object).toString();
                } catch (Exception ex) {
                    throw new IllegalArgumentException(ex);
                }
            } else {
                return "";
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    public T convertFromString(Class<? extends T> cls, String str) {
        if (str.isEmpty()) {
            return empty;
        }
        int value = Integer.parseInt(str);
        try {
            return (T) methodOf.invoke(null, value);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    public Class<?> getEffectiveType() {
        return cls;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
