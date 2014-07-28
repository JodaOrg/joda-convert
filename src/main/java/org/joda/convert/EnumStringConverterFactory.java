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

/**
 * Factory for {@code StringConverter} looking up enums.
 * <p>
 * This class is immutable and thread-safe.
 * 
 * @since 1.7
 */
final class EnumStringConverterFactory implements StringConverterFactory {

    /**
     * Singleton instance.
     */
    static final StringConverterFactory INSTANCE = new EnumStringConverterFactory();

    /**
     * Restricted constructor.
     */
    private EnumStringConverterFactory() {
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
        Class<?> sup = cls.getSuperclass();
        if (sup == Enum.class) {
            return new EnumStringConverter(cls);
        } else if (sup != null && sup.getSuperclass() == Enum.class) {
            return new EnumStringConverter(sup);
        }
        return null;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    //-----------------------------------------------------------------------
    final class EnumStringConverter implements TypedStringConverter<Enum<?>> {
        
        private final Class<?> effectiveType;
        
        EnumStringConverter(Class<?> effectiveType) {
            this.effectiveType = effectiveType;
        }

        @Override
        public String convertToString(Enum<?> en) {
            return en.name();  // avoid toString() as that can be overridden
        }
        @Override
        @SuppressWarnings({ "unchecked", "rawtypes" })
        public Enum<?> convertFromString(Class<? extends Enum<?>> cls, String str) {
            return RenameHandler.INSTANCE.lookupEnum((Class) cls, str);
        }
        @Override
        public Class<?> getEffectiveType() {
            return effectiveType;
        }
    }

}
