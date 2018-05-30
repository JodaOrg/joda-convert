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
 * Factory for {@code StringConverter} providing support for primitive arrays
 * as a comma separated list.
 * <p>
 * To use, simply register the instance with a {@code StringConvert} instance.
 * <p>
 * This class is immutable and thread-safe.
 * 
 * @since 1.5
 */
public final class NumericArrayStringConverterFactory implements StringConverterFactory {

    /**
     * Singleton instance.
     */
    public static final StringConverterFactory INSTANCE = new NumericArrayStringConverterFactory();

    /**
     * Restricted constructor.
     */
    private NumericArrayStringConverterFactory() {
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
        if (cls.isArray() && cls.getComponentType().isPrimitive()) {
            if (cls == long[].class) {
                return LongArrayStringConverter.INSTANCE;
            }
            if (cls == int[].class) {
                return IntArrayStringConverter.INSTANCE;
            }
            if (cls == short[].class) {
                return ShortArrayStringConverter.INSTANCE;
            }
            if (cls == double[].class) {
                return DoubleArrayStringConverter.INSTANCE;
            }
            if (cls == float[].class) {
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
    enum LongArrayStringConverter implements TypedStringConverter<long[]> {
        INSTANCE {
            @Override
            public String convertToString(long[] array) {
                if (array.length == 0) {
                    return "";
                }
                StringBuilder buf = new StringBuilder(array.length * 8);
                buf.append(array[0]);
                for (int i = 1; i < array.length; i++) {
                    buf.append(',').append(array[i]);
                }
                return buf.toString();
            }
            @Override
            public long[] convertFromString(Class<? extends long[]> cls, String str) {
                if (str.length() == 0) {
                    return EMPTY;
                }
                int count = 0;
                int base = 0;
                int sep = str.indexOf(',');
                long[] array = new long[str.length() / 2 + 1];
                while (sep >= 0) {
                    array[count++] = Long.parseLong(str.substring(base, sep));
                    base = sep + 1;
                    sep = str.indexOf(',', sep + 1);
                }
                array[count++] = Long.parseLong(str.substring(base, str.length()));
                return Arrays.copyOf(array, count);
            }
            @Override
            public Class<?> getEffectiveType() {
                return long[].class;
            }
        };
        private static final long[] EMPTY = new long[0];
    }

    //-----------------------------------------------------------------------
    enum IntArrayStringConverter implements TypedStringConverter<int[]> {
        INSTANCE {
            @Override
            public String convertToString(int[] array) {
                if (array.length == 0) {
                    return "";
                }
                StringBuilder buf = new StringBuilder(array.length * 6);
                buf.append(array[0]);
                for (int i = 1; i < array.length; i++) {
                    buf.append(',').append(array[i]);
                }
                return buf.toString();
            }
            @Override
            public int[] convertFromString(Class<? extends int[]> cls, String str) {
                if (str.length() == 0) {
                    return EMPTY;
                }
                int count = 0;
                int base = 0;
                int sep = str.indexOf(',');
                int[] array = new int[str.length() / 2 + 1];
                while (sep >= 0) {
                    array[count++] = Integer.parseInt(str.substring(base, sep));
                    base = sep + 1;
                    sep = str.indexOf(',', sep + 1);
                }
                array[count++] = Integer.parseInt(str.substring(base, str.length()));
                return Arrays.copyOf(array, count);
            }
            @Override
            public Class<?> getEffectiveType() {
                return int[].class;
            }
        };
        private static final int[] EMPTY = new int[0];
    }

    //-----------------------------------------------------------------------
    enum ShortArrayStringConverter implements TypedStringConverter<short[]> {
        INSTANCE {
            @Override
            public String convertToString(short[] array) {
                if (array.length == 0) {
                    return "";
                }
                StringBuilder buf = new StringBuilder(array.length * 3);
                buf.append(array[0]);
                for (int i = 1; i < array.length; i++) {
                    buf.append(',').append(array[i]);
                }
                return buf.toString();
            }
            @Override
            public short[] convertFromString(Class<? extends short[]> cls, String str) {
                if (str.length() == 0) {
                    return EMPTY;
                }
                int count = 0;
                int base = 0;
                int sep = str.indexOf(',');
                short[] array = new short[str.length() / 2 + 1];
                while (sep >= 0) {
                    array[count++] = Short.parseShort(str.substring(base, sep));
                    base = sep + 1;
                    sep = str.indexOf(',', sep + 1);
                }
                array[count++] = Short.parseShort(str.substring(base, str.length()));
                return Arrays.copyOf(array, count);
            }
            @Override
            public Class<?> getEffectiveType() {
                return short[].class;
            }
        };
        private static final short[] EMPTY = new short[0];
    }

    //-----------------------------------------------------------------------
    enum DoubleArrayStringConverter implements TypedStringConverter<double[]> {
        INSTANCE {
            @Override
            public String convertToString(double[] array) {
                if (array.length == 0) {
                    return "";
                }
                StringBuilder buf = new StringBuilder(array.length * 8);
                buf.append(array[0]);
                for (int i = 1; i < array.length; i++) {
                    buf.append(',').append(array[i]);
                }
                return buf.toString();
            }
            @Override
            public double[] convertFromString(Class<? extends double[]> cls, String str) {
                if (str.length() == 0) {
                    return EMPTY;
                }
                int count = 0;
                int base = 0;
                int sep = str.indexOf(',');
                double[] array = new double[str.length() / 2 + 1];
                while (sep >= 0) {
                    array[count++] = Double.parseDouble(str.substring(base, sep));
                    base = sep + 1;
                    sep = str.indexOf(',', sep + 1);
                }
                array[count++] = Double.parseDouble(str.substring(base, str.length()));
                return Arrays.copyOf(array, count);
            }
            @Override
            public Class<?> getEffectiveType() {
                return double[].class;
            }
        };
        private static final double[] EMPTY = new double[0];
    }

    //-----------------------------------------------------------------------
    enum FloatArrayStringConverter implements TypedStringConverter<float[]> {
        INSTANCE {
            @Override
            public String convertToString(float[] array) {
                if (array.length == 0) {
                    return "";
                }
                StringBuilder buf = new StringBuilder(array.length * 8);
                buf.append(array[0]);
                for (int i = 1; i < array.length; i++) {
                    buf.append(',').append(array[i]);
                }
                return buf.toString();
            }
            @Override
            public float[] convertFromString(Class<? extends float[]> cls, String str) {
                if (str.length() == 0) {
                    return EMPTY;
                }
                int count = 0;
                int base = 0;
                int sep = str.indexOf(',');
                float[] array = new float[str.length() / 2 + 1];
                while (sep >= 0) {
                    array[count++] = Float.parseFloat(str.substring(base, sep));
                    base = sep + 1;
                    sep = str.indexOf(',', sep + 1);
                }
                array[count++] = Float.parseFloat(str.substring(base, str.length()));
                return Arrays.copyOf(array, count);
            }
            @Override
            public Class<?> getEffectiveType() {
                return float[].class;
            }
        };
        private static final float[] EMPTY = new float[0];
    }

}
