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
package org.joda.convert.factory;

import java.util.Arrays;

import org.joda.convert.StringConverter;
import org.joda.convert.StringConverterFactory;
import org.joda.convert.TypedStringConverter;

/**
 * Factory for {@code StringConverter} providing support for numeric object arrays
 * as a comma separated list, using '-' for null.
 * <p>
 * To use, simply register the instance with a {@code StringConvert} instance.
 * <p>
 * This class is immutable and thread-safe.
 * 
 * @since 1.5
 */
public final class NumericObjectArrayStringConverterFactory implements StringConverterFactory {

    /**
     * Singleton instance.
     */
    public static final StringConverterFactory INSTANCE = new NumericObjectArrayStringConverterFactory();

    /**
     * Restricted constructor.
     */
    private NumericObjectArrayStringConverterFactory() {
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
        if (cls.isArray()) {
            if (cls == Long[].class) {
                return LongArrayStringConverter.INSTANCE;
            }
            if (cls == Integer[].class) {
                return IntArrayStringConverter.INSTANCE;
            }
            if (cls == Short[].class) {
                return ShortArrayStringConverter.INSTANCE;
            }
            if (cls == Double[].class) {
                return DoubleArrayStringConverter.INSTANCE;
            }
            if (cls == Float[].class) {
                return FloatArrayStringConverter.INSTANCE;
            }
        }
        return null;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    //-----------------------------------------------------------------------
    enum LongArrayStringConverter implements TypedStringConverter<Long[]> {
        INSTANCE {
            @Override
            public String convertToString(Long[] array) {
                if (array.length == 0) {
                    return "";
                }
                StringBuilder buf = new StringBuilder(array.length * 8);
                buf.append(array[0] != null ? array[0] : "-");
                for (int i = 1; i < array.length; i++) {
                    buf.append(',').append(array[i] != null ? array[i] : "-");
                }
                return buf.toString();
            }
            @Override
            public Long[] convertFromString(Class<? extends Long[]> cls, String str) {
                if (str.length() == 0) {
                    return EMPTY;
                }
                int count = 0;
                int base = 0;
                int sep = str.indexOf(',');
                Long[] array = new Long[str.length() / 2 + 1];
                while (sep >= 0) {
                    String split = str.substring(base, sep);
                    array[count++] = split.equals("-") ? null : Long.valueOf(split);
                    base = sep + 1;
                    sep = str.indexOf(',', sep + 1);
                }
                String split = str.substring(base, str.length());
                array[count++] = split.equals("-") ? null : Long.valueOf(split);
                return Arrays.copyOf(array, count);
            }
            @Override
            public Class<?> getEffectiveType() {
                return Long[].class;
            }
        };
        private static final Long[] EMPTY = new Long[0];
    }

    //-----------------------------------------------------------------------
    enum IntArrayStringConverter implements TypedStringConverter<Integer[]> {
        INSTANCE {
            @Override
            public String convertToString(Integer[] array) {
                if (array.length == 0) {
                    return "";
                }
                StringBuilder buf = new StringBuilder(array.length * 6);
                buf.append(array[0] != null ? array[0] : "-");
                for (int i = 1; i < array.length; i++) {
                    buf.append(',').append(array[i] != null ? array[i] : "-");
                }
                return buf.toString();
            }
            @Override
            public Integer[] convertFromString(Class<? extends Integer[]> cls, String str) {
                if (str.length() == 0) {
                    return EMPTY;
                }
                int count = 0;
                int base = 0;
                int sep = str.indexOf(',');
                Integer[] array = new Integer[str.length() / 2 + 1];
                while (sep >= 0) {
                    String split = str.substring(base, sep);
                    array[count++] = split.equals("-") ? null : Integer.valueOf(split);
                    base = sep + 1;
                    sep = str.indexOf(',', sep + 1);
                }
                String split = str.substring(base, str.length());
                array[count++] = split.equals("-") ? null : Integer.valueOf(split);
                return Arrays.copyOf(array, count);
            }
            @Override
            public Class<?> getEffectiveType() {
                return Integer[].class;
            }
        };
        private static final Integer[] EMPTY = new Integer[0];
    }

    //-----------------------------------------------------------------------
    enum ShortArrayStringConverter implements TypedStringConverter<Short[]> {
        INSTANCE {
            @Override
            public String convertToString(Short[] array) {
                if (array.length == 0) {
                    return "";
                }
                StringBuilder buf = new StringBuilder(array.length * 3);
                buf.append(array[0] != null ? array[0] : "-");
                for (int i = 1; i < array.length; i++) {
                    buf.append(',').append(array[i] != null ? array[i] : "-");
                }
                return buf.toString();
            }
            @Override
            public Short[] convertFromString(Class<? extends Short[]> cls, String str) {
                if (str.length() == 0) {
                    return EMPTY;
                }
                int count = 0;
                int base = 0;
                int sep = str.indexOf(',');
                Short[] array = new Short[str.length() / 2 + 1];
                while (sep >= 0) {
                    String split = str.substring(base, sep);
                    array[count++] = split.equals("-") ? null : Short.valueOf(split);
                    base = sep + 1;
                    sep = str.indexOf(',', sep + 1);
                }
                String split = str.substring(base, str.length());
                array[count++] = split.equals("-") ? null : Short.valueOf(split);
                return Arrays.copyOf(array, count);
            }
            @Override
            public Class<?> getEffectiveType() {
                return Short[].class;
            }
        };
        private static final Short[] EMPTY = new Short[0];
    }

    //-----------------------------------------------------------------------
    enum DoubleArrayStringConverter implements TypedStringConverter<Double[]> {
        INSTANCE {
            @Override
            public String convertToString(Double[] array) {
                if (array.length == 0) {
                    return "";
                }
                StringBuilder buf = new StringBuilder(array.length * 8);
                buf.append(array[0] != null ? array[0] : "-");
                for (int i = 1; i < array.length; i++) {
                    buf.append(',').append(array[i] != null ? array[i] : "-");
                }
                return buf.toString();
            }
            @Override
            public Double[] convertFromString(Class<? extends Double[]> cls, String str) {
                if (str.length() == 0) {
                    return EMPTY;
                }
                int count = 0;
                int base = 0;
                int sep = str.indexOf(',');
                Double[] array = new Double[str.length() / 2 + 1];
                while (sep >= 0) {
                    String split = str.substring(base, sep);
                    array[count++] = split.equals("-") ? null : Double.valueOf(split);
                    base = sep + 1;
                    sep = str.indexOf(',', sep + 1);
                }
                String split = str.substring(base, str.length());
                array[count++] = split.equals("-") ? null : Double.valueOf(split);
                return Arrays.copyOf(array, count);
            }
            @Override
            public Class<?> getEffectiveType() {
                return Double[].class;
            }
        };
        private static final Double[] EMPTY = new Double[0];
    }

    //-----------------------------------------------------------------------
    enum FloatArrayStringConverter implements TypedStringConverter<Float[]> {
        INSTANCE {
            @Override
            public String convertToString(Float[] array) {
                if (array.length == 0) {
                    return "";
                }
                StringBuilder buf = new StringBuilder(array.length * 8);
                buf.append(array[0] != null ? array[0] : "-");
                for (int i = 1; i < array.length; i++) {
                    buf.append(',').append(array[i] != null ? array[i] : "-");
                }
                return buf.toString();
            }
            @Override
            public Float[] convertFromString(Class<? extends Float[]> cls, String str) {
                if (str.length() == 0) {
                    return EMPTY;
                }
                int count = 0;
                int base = 0;
                int sep = str.indexOf(',');
                Float[] array = new Float[str.length() / 2 + 1];
                while (sep >= 0) {
                    String split = str.substring(base, sep);
                    array[count++] = split.equals("-") ? null : Float.valueOf(split);
                    base = sep + 1;
                    sep = str.indexOf(',', sep + 1);
                }
                String split = str.substring(base, str.length());
                array[count++] = split.equals("-") ? null : Float.valueOf(split);
                return Arrays.copyOf(array, count);
            }
            @Override
            public Class<?> getEffectiveType() {
                return Float[].class;
            }
        };
        private static final Float[] EMPTY = new Float[0];
    }

}
