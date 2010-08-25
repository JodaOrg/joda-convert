/*
 *  Copyright 2010 Stephen Colebourne
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

/**
 * Test JDKStringConverters.
 */
public class TestJDKStringConverters {

    @Test
    public void test_String() {
        JDKStringConverter test = JDKStringConverter.STRING;
        doTest(test, String.class, "Hello", "Hello");
    }

    @Test
    public void test_StringBuffer() {
        JDKStringConverter test = JDKStringConverter.STRING_BUFFER;
        Object obj = new StringBuffer("Hello");
        assertEquals(StringBuffer.class, test.getType());
        assertEquals("Hello", test.convertToString(obj));
        StringBuffer back = (StringBuffer) test.convertFromString("Hello");
        assertEquals("Hello", back.toString());
    }

    @Test
    public void test_StringBuilder() {
        JDKStringConverter test = JDKStringConverter.STRING_BUILDER;
        Object obj = new StringBuilder("Hello");
        assertEquals(StringBuilder.class, test.getType());
        assertEquals("Hello", test.convertToString(obj));
        StringBuilder back = (StringBuilder) test.convertFromString("Hello");
        assertEquals("Hello", back.toString());
    }

    @Test
    public void test_CharSequence() {
        JDKStringConverter test = JDKStringConverter.CHAR_SEQUENCE;
        doTest(test, CharSequence.class, "Hello", "Hello");
        doTest(test, CharSequence.class, new StringBuffer("Hello"), "Hello", "Hello");
        doTest(test, CharSequence.class, new StringBuilder("Hello"), "Hello", "Hello");
    }

    @Test
    public void test_Boolean() {
        JDKStringConverter test = JDKStringConverter.BOOLEAN;
        doTest(test, Boolean.class, Boolean.TRUE, "true");
        doTest(test, Boolean.class, Boolean.FALSE, "false");
    }

    @Test
    public void test_Character() {
        JDKStringConverter test = JDKStringConverter.CHARACTER;
        doTest(test, Character.class, Character.valueOf('a'), "a");
        doTest(test, Character.class, Character.valueOf('z'), "z");
    }

    @Test
    public void test_Byte() {
        JDKStringConverter test = JDKStringConverter.BYTE;
        doTest(test, Byte.class, Byte.valueOf((byte) 12), "12");
    }

    @Test
    public void test_Short() {
        JDKStringConverter test = JDKStringConverter.SHORT;
        doTest(test, Short.class, Short.valueOf((byte) 12), "12");
    }

    @Test
    public void test_Int() {
        JDKStringConverter test = JDKStringConverter.INTEGER;
        doTest(test, Integer.class, Integer.valueOf(12), "12");
    }

    @Test
    public void test_Long() {
        JDKStringConverter test = JDKStringConverter.LONG;
        doTest(test, Long.class, Long.valueOf(12L), "12");
    }

    @Test
    public void test_Float() {
        JDKStringConverter test = JDKStringConverter.FLOAT;
        doTest(test, Float.class, Float.valueOf(12.2f), "12.2");
    }

    @Test
    public void test_Double() {
        JDKStringConverter test = JDKStringConverter.DOUBLE;
        doTest(test, Double.class, Double.valueOf(12.4d), "12.4");
    }

    @Test
    public void test_BigInteger() {
        JDKStringConverter test = JDKStringConverter.BIG_INTEGER;
        doTest(test, BigInteger.class, BigInteger.valueOf(12L), "12");
    }

    @Test
    public void test_BigDecimal() {
        JDKStringConverter test = JDKStringConverter.BIG_DECIMAL;
        doTest(test, BigDecimal.class, BigDecimal.valueOf(12.23d), "12.23");
    }

    @Test
    public void test_AtomicBoolean() {
        JDKStringConverter test = JDKStringConverter.ATOMIC_BOOLEAN;
        AtomicBoolean obj = new AtomicBoolean(true);
        assertEquals(AtomicBoolean.class, test.getType());
        assertEquals("true", test.convertToString(obj));
        AtomicBoolean back = (AtomicBoolean) test.convertFromString("true");
        assertEquals(true, back.get());
    }

    @Test
    public void test_AtomicInt() {
        JDKStringConverter test = JDKStringConverter.ATOMIC_INTEGER;
        AtomicInteger obj = new AtomicInteger(12);
        assertEquals(AtomicInteger.class, test.getType());
        assertEquals("12", test.convertToString(obj));
        AtomicInteger back = (AtomicInteger) test.convertFromString("12");
        assertEquals(12, back.get());
    }

    @Test
    public void test_AtomicLong() {
        JDKStringConverter test = JDKStringConverter.ATOMIC_LONG;
        AtomicLong obj = new AtomicLong(12);
        assertEquals(AtomicLong.class, test.getType());
        assertEquals("12", test.convertToString(obj));
        AtomicLong back = (AtomicLong) test.convertFromString("12");
        assertEquals(12, back.get());
    }

    @Test
    public void test_Locale() {
        JDKStringConverter test = JDKStringConverter.LOCALE;
        doTest(test, Locale.class, new Locale("en"), "en");
        doTest(test, Locale.class, new Locale("en", "GB"), "en_GB");
    }

    //-----------------------------------------------------------------------
    public void doTest(JDKStringConverter test, Class<?> cls, Object obj, String str) {
        doTest(test, cls, obj, str, obj);
    }

    public void doTest(JDKStringConverter test, Class<?> cls, Object obj, String str, Object objFromStr) {
        assertEquals(cls, test.getType());
        assertEquals(str, test.convertToString(obj));
        assertEquals(objFromStr, test.convertFromString(str));
    }

}
