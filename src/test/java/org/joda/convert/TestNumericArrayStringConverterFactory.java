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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.joda.convert.factory.NumericArrayStringConverterFactory;
import org.junit.Test;

/**
 * Test NumericArrayStringConverterFactory.
 */
public class TestNumericArrayStringConverterFactory {

    @Test
    public void test_longArray() {
        doTest(new long[0], "");
        doTest(new long[] {5}, "5");
        doTest(new long[] {-1234, 56789}, "-1234,56789");
        doTest(new long[] {12345678912345L, 12345678912345L}, "12345678912345,12345678912345");
    }

    private void doTest(long[] array, String str) {
        StringConvert test = new StringConvert(true, NumericArrayStringConverterFactory.INSTANCE);
        assertEquals(str, test.convertToString(array));
        assertEquals(str, test.convertToString(long[].class, array));
        assertTrue(Arrays.equals(array, test.convertFromString(long[].class, str)));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_intArray() {
        doTest(new int[0], "");
        doTest(new int[] {5}, "5");
        doTest(new int[] {-1234, 56789}, "-1234,56789");
    }

    private void doTest(int[] array, String str) {
        StringConvert test = new StringConvert(true, NumericArrayStringConverterFactory.INSTANCE);
        assertEquals(str, test.convertToString(array));
        assertEquals(str, test.convertToString(int[].class, array));
        assertTrue(Arrays.equals(array, test.convertFromString(int[].class, str)));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_shortArray() {
        doTest(new short[0], "");
        doTest(new short[] {5}, "5");
        doTest(new short[] {-1234, 5678}, "-1234,5678");
    }

    private void doTest(short[] array, String str) {
        StringConvert test = new StringConvert(true, NumericArrayStringConverterFactory.INSTANCE);
        assertEquals(str, test.convertToString(array));
        assertEquals(str, test.convertToString(short[].class, array));
        assertTrue(Arrays.equals(array, test.convertFromString(short[].class, str)));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_doubleArray() {
        doTest(new double[0], "");
        doTest(new double[] {5d}, "5.0");
        doTest(new double[] {5.123456789d}, "5.123456789");
        doTest(new double[] {-1234d, 5678d}, "-1234.0,5678.0");
        doTest(new double[] {Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, -0.0d, +0.0d, 0d}, "NaN,-Infinity,Infinity,-0.0,0.0,0.0");
        doTest(new double[] {0.0000006d, 6000000000d}, "6.0E-7,6.0E9");
    }

    private void doTest(double[] array, String str) {
        StringConvert test = new StringConvert(true, NumericArrayStringConverterFactory.INSTANCE);
        assertEquals(str, test.convertToString(array));
        assertEquals(str, test.convertToString(double[].class, array));
        assertTrue(Arrays.equals(array, test.convertFromString(double[].class, str)));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_floatArray() {
        doTest(new float[0], "");
        doTest(new float[] {5f}, "5.0");
        doTest(new float[] {5.1234f}, "5.1234");
        doTest(new float[] {-1234f, 5678f}, "-1234.0,5678.0");
        doTest(new float[] {Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, -0.0f, +0.0f, 0f}, "NaN,-Infinity,Infinity,-0.0,0.0,0.0");
        doTest(new float[] {0.0000006f, 6000000000f}, "6.0E-7,6.0E9");
    }

    private void doTest(float[] array, String str) {
        StringConvert test = new StringConvert(true, NumericArrayStringConverterFactory.INSTANCE);
        assertEquals(str, test.convertToString(array));
        assertEquals(str, test.convertToString(float[].class, array));
        assertTrue(Arrays.equals(array, test.convertFromString(float[].class, str)));
    }

}
