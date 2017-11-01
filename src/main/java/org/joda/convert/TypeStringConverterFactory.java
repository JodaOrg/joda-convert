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

import java.lang.reflect.Type;

/**
 * Factory for {@code StringConverter} looking up types.
 * <p>
 * This class is immutable and thread-safe.
 * <p>
 * This parses the string format of Type.
 * <p>
 * This is achieved thanks to some code copied from Guava.
 * (A Guava dependency is painful when building a Java 6 library for Java 9)
 * <p>
 * This parser is incomplete, but handles common cases.
 * It does not handle union types or multi-dimensional arrays.
 */
final class TypeStringConverterFactory
        implements StringConverterFactory {

    /**
     * Singleton instance.
     */
    static final TypeStringConverterFactory INSTANCE = new TypeStringConverterFactory();

    /**
     * Restricted constructor.
     */
    private TypeStringConverterFactory() {
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
        if (Type.class.isAssignableFrom(cls) && cls != Class.class) {
            return new TypeStringConverter(cls);
        }
        return null;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    //-----------------------------------------------------------------------
    static final class TypeStringConverter implements TypedStringConverter<Type> {

        private final Class<?> effectiveType;

        TypeStringConverter(Class<?> effectiveType) {
            this.effectiveType = effectiveType;
        }

        @Override
        public String convertToString(Type type) {
            try {
                return Types.toString(type);
            } catch (Exception ex) {
                return type.toString();
            }
        }

        @Override
        public Type convertFromString(Class<? extends Type> cls, String str) {
            return TypeUtils.parse(str);
        }

        @Override
        public Class<?> getEffectiveType() {
            return effectiveType;
        }
    }

}
