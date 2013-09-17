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

import org.joda.convert.factory.NumericObjectArrayStringConverterFactory;
import org.junit.Test;

/**
 * Test NumericObjectArrayStringConverterFactory.
 */
public class TestNumericObjectArrayStringConverterFactory {

    @Test
    public void test_LongArray() {
        doTest(new Long[0], "");
        doTest(new Long[] {5L}, "5");
        doTest(new Long[] {null}, "-");
        doTest(new Long[] {-1234L, null, 56789L, null, null, 5L}, "-1234,-,56789,-,-,5");
        doTest(new Long[] {12345678912345L, 12345678912345L}, "12345678912345,12345678912345");
    }

    private void doTest(Long[] array, String str) {
        StringConvert test = new StringConvert(true, NumericObjectArrayStringConverterFactory.INSTANCE);
        assertEquals(str, test.convertToString(array));
        assertEquals(str, test.convertToString(Long[].class, array));
        assertTrue(Arrays.equals(array, test.convertFromString(Long[].class, str)));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_IntegerArray() {
        doTest(new Integer[0], "");
        doTest(new Integer[] {5}, "5");
        doTest(new Integer[] {null}, "-");
        doTest(new Integer[] {-1234, null, 56789, null, null, 5}, "-1234,-,56789,-,-,5");
    }

    private void doTest(Integer[] array, String str) {
        StringConvert test = new StringConvert(true, NumericObjectArrayStringConverterFactory.INSTANCE);
        assertEquals(str, test.convertToString(array));
        assertEquals(str, test.convertToString(Integer[].class, array));
        assertTrue(Arrays.equals(array, test.convertFromString(Integer[].class, str)));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_ShortArray() {
        doTest(new Short[0], "");
        doTest(new Short[] {5}, "5");
        doTest(new Short[] {null}, "-");
        doTest(new Short[] {-1234, null, 5678, null, null, 5}, "-1234,-,5678,-,-,5");
    }

    private void doTest(Short[] array, String str) {
        StringConvert test = new StringConvert(true, NumericObjectArrayStringConverterFactory.INSTANCE);
        assertEquals(str, test.convertToString(array));
        assertEquals(str, test.convertToString(Short[].class, array));
        assertTrue(Arrays.equals(array, test.convertFromString(Short[].class, str)));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_DoubleArray() {
        doTest(new Double[0], "");
        doTest(new Double[] {5d}, "5.0");
        doTest(new Double[] {null}, "-");
        doTest(new Double[] {5.123456789d}, "5.123456789");
        doTest(new Double[] {-1234d, null, 5678d, null, null, 5d}, "-1234.0,-,5678.0,-,-,5.0");
        doTest(new Double[] {Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, -0.0d, +0.0d, 0d}, "NaN,-Infinity,Infinity,-0.0,0.0,0.0");
        doTest(new Double[] {0.0000006d, 6000000000d}, "6.0E-7,6.0E9");
    }

    private void doTest(Double[] array, String str) {
        StringConvert test = new StringConvert(true, NumericObjectArrayStringConverterFactory.INSTANCE);
        assertEquals(str, test.convertToString(array));
        assertEquals(str, test.convertToString(Double[].class, array));
        assertTrue(Arrays.equals(array, test.convertFromString(Double[].class, str)));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_FloatArray() {
        doTest(new Float[0], "");
        doTest(new Float[] {5f}, "5.0");
        doTest(new Float[] {null}, "-");
        doTest(new Float[] {5.1234f}, "5.1234");
        doTest(new Float[] {-1234f, null, 5678f, null, null, 5f}, "-1234.0,-,5678.0,-,-,5.0");
        doTest(new Float[] {Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, -0.0f, +0.0f, 0f}, "NaN,-Infinity,Infinity,-0.0,0.0,0.0");
        doTest(new Float[] {0.0000006f, 6000000000f}, "6.0E-7,6.0E9");
    }

    private void doTest(Float[] array, String str) {
        StringConvert test = new StringConvert(true, NumericObjectArrayStringConverterFactory.INSTANCE);
        assertEquals(str, test.convertToString(array));
        assertEquals(str, test.convertToString(Float[].class, array));
        assertTrue(Arrays.equals(array, test.convertFromString(Float[].class, str)));
    }

}
