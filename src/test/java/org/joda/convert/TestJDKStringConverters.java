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

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
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
        StringBuffer back = (StringBuffer) test.convertFromString(StringBuffer.class, "Hello");
        assertEquals("Hello", back.toString());
    }

    @Test
    public void test_StringBuilder() {
        JDKStringConverter test = JDKStringConverter.STRING_BUILDER;
        Object obj = new StringBuilder("Hello");
        assertEquals(StringBuilder.class, test.getType());
        assertEquals("Hello", test.convertToString(obj));
        StringBuilder back = (StringBuilder) test.convertFromString(StringBuilder.class, "Hello");
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
    public void test_Long() {
        JDKStringConverter test = JDKStringConverter.LONG;
        doTest(test, Long.class, Long.valueOf(12L), "12");
    }

    @Test
    public void test_Int() {
        JDKStringConverter test = JDKStringConverter.INTEGER;
        doTest(test, Integer.class, Integer.valueOf(12), "12");
    }

    @Test
    public void test_Short() {
        JDKStringConverter test = JDKStringConverter.SHORT;
        doTest(test, Short.class, Short.valueOf((byte) 12), "12");
    }

    @Test
    public void test_Character() {
        JDKStringConverter test = JDKStringConverter.CHARACTER;
        doTest(test, Character.class, Character.valueOf('a'), "a");
        doTest(test, Character.class, Character.valueOf('z'), "z");
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_Character_fail() {
        JDKStringConverter.CHARACTER.convertFromString(Character.class, "RUBBISH");
    }

    @Test
    public void test_charArray() {
        JDKStringConverter test = JDKStringConverter.CHAR_ARRAY;
        char[] array = new char[] {'M', 'a', 'p'};
        String str = "Map";
        assertEquals(char[].class, test.getType());
        assertEquals(str, test.convertToString(array));
        assertTrue(Arrays.equals(array, (char[]) test.convertFromString(char[].class, str)));
    }

    @Test
    public void test_Byte() {
        JDKStringConverter test = JDKStringConverter.BYTE;
        doTest(test, Byte.class, Byte.valueOf((byte) 12), "12");
    }

    @Test
    public void test_byteArray1() {
        JDKStringConverter test = JDKStringConverter.BYTE_ARRAY;
        byte[] array = new byte[] {77, 97, 112};
        String str = "TWFw";
        assertEquals(byte[].class, test.getType());
        assertEquals(str, test.convertToString(array));
        assertTrue(Arrays.equals(array, (byte[]) test.convertFromString(byte[].class, str)));
    }

    @Test
    public void test_byteArray2() {
        JDKStringConverter test = JDKStringConverter.BYTE_ARRAY;
        byte[] array = new byte[] {77, 97};
        String str = "TWE=";
        assertEquals(byte[].class, test.getType());
        assertEquals(str, test.convertToString(array));
        assertTrue(Arrays.equals(array, (byte[]) test.convertFromString(byte[].class, str)));
    }

    @Test
    public void test_Boolean() {
        JDKStringConverter test = JDKStringConverter.BOOLEAN;
        doTest(test, Boolean.class, Boolean.TRUE, "true");
        doTest(test, Boolean.class, Boolean.FALSE, "false");
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_Boolean_fail() {
        JDKStringConverter.BOOLEAN.convertFromString(Boolean.class, "RUBBISH");
    }

    @Test
    public void test_Double() {
        JDKStringConverter test = JDKStringConverter.DOUBLE;
        doTest(test, Double.class, Double.valueOf(12.4d), "12.4");
    }

    @Test
    public void test_Float() {
        JDKStringConverter test = JDKStringConverter.FLOAT;
        doTest(test, Float.class, Float.valueOf(12.2f), "12.2");
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
    public void test_AtomicLong() {
        JDKStringConverter test = JDKStringConverter.ATOMIC_LONG;
        AtomicLong obj = new AtomicLong(12);
        assertEquals(AtomicLong.class, test.getType());
        assertEquals("12", test.convertToString(obj));
        AtomicLong back = (AtomicLong) test.convertFromString(AtomicLong.class, "12");
        assertEquals(12, back.get());
    }

    @Test
    public void test_AtomicInteger() {
        JDKStringConverter test = JDKStringConverter.ATOMIC_INTEGER;
        AtomicInteger obj = new AtomicInteger(12);
        assertEquals(AtomicInteger.class, test.getType());
        assertEquals("12", test.convertToString(obj));
        AtomicInteger back = (AtomicInteger) test.convertFromString(AtomicInteger.class, "12");
        assertEquals(12, back.get());
    }

    @Test
    public void test_AtomicBoolean_true() {
        JDKStringConverter test = JDKStringConverter.ATOMIC_BOOLEAN;
        AtomicBoolean obj = new AtomicBoolean(true);
        assertEquals(AtomicBoolean.class, test.getType());
        assertEquals("true", test.convertToString(obj));
        AtomicBoolean back = (AtomicBoolean) test.convertFromString(AtomicBoolean.class, "true");
        assertEquals(true, back.get());
    }

    @Test
    public void test_AtomicBoolean_false() {
        JDKStringConverter test = JDKStringConverter.ATOMIC_BOOLEAN;
        AtomicBoolean obj = new AtomicBoolean(false);
        assertEquals(AtomicBoolean.class, test.getType());
        assertEquals("false", test.convertToString(obj));
        AtomicBoolean back = (AtomicBoolean) test.convertFromString(AtomicBoolean.class, "false");
        assertEquals(false, back.get());
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_AtomicBoolean_fail() {
        JDKStringConverter.ATOMIC_BOOLEAN.convertFromString(AtomicBoolean.class, "RUBBISH");
    }

    @Test
    public void test_Locale() {
        JDKStringConverter test = JDKStringConverter.LOCALE;
        doTest(test, Locale.class, new Locale("en"), "en");
        doTest(test, Locale.class, new Locale("en", "GB"), "en_GB");
        doTest(test, Locale.class, new Locale("en", "GB", "VARIANT_B"), "en_GB_VARIANT_B");
    }

    @Test
    public void test_Class() {
        JDKStringConverter test = JDKStringConverter.CLASS;
        doTest(test, Class.class, Locale.class, "java.util.Locale");
        doTest(test, Class.class, FromString.class, "org.joda.convert.FromString");
    }

    @Test(expected=RuntimeException.class)
    public void test_Class_fail() {
        JDKStringConverter.CLASS.convertFromString(Class.class, "RUBBISH");
    }

    @Test
    public void test_Package() {
        JDKStringConverter test = JDKStringConverter.PACKAGE;
        doTest(test, Package.class, Locale.class.getPackage(), "java.util");
        doTest(test, Package.class, FromString.class.getPackage(), "org.joda.convert");
    }

    @Test
    public void test_Currency() {
        JDKStringConverter test = JDKStringConverter.CURRENCY;
        doTest(test, Currency.class, Currency.getInstance("GBP"), "GBP");
        doTest(test, Currency.class, Currency.getInstance("USD"), "USD");
    }

    @Test
    public void test_TimeZone() {
        JDKStringConverter test = JDKStringConverter.TIME_ZONE;
        doTest(test, TimeZone.class, TimeZone.getTimeZone("Europe/London"), "Europe/London");
        doTest(test, TimeZone.class, TimeZone.getTimeZone("America/New_York"), "America/New_York");
    }

    @Test
    public void test_UUID() {
        JDKStringConverter test = JDKStringConverter.UUID;
        UUID uuid = UUID.randomUUID();
        doTest(test, UUID.class, uuid, uuid.toString());
    }

    @Test
    public void test_URL() throws Exception {
        JDKStringConverter test = JDKStringConverter.URL;
        doTest(test, URL.class, new URL("http://localhost:8080/my/test"), "http://localhost:8080/my/test");
        doTest(test, URL.class, new URL(null, "ftp:world"), "ftp:world");
    }

    @Test(expected=RuntimeException.class)
    public void test_URL_invalidFormat() {
        JDKStringConverter.URL.convertFromString(URL.class, "RUBBISH:RUBBISH");
    }

    @Test
    public void test_URI() {
        JDKStringConverter test = JDKStringConverter.URI;
        doTest(test, URI.class, URI.create("http://localhost:8080/my/test"), "http://localhost:8080/my/test");
        doTest(test, URI.class, URI.create("/my/test"), "/my/test");
        doTest(test, URI.class, URI.create("/my/../test"), "/my/../test");
        doTest(test, URI.class, URI.create("urn:hello"), "urn:hello");
    }

    @Test
    public void test_InetAddress() throws Exception {
        JDKStringConverter test = JDKStringConverter.INET_ADDRESS;
        doTest(test, InetAddress.class, InetAddress.getByName("1.2.3.4"), "1.2.3.4");
        doTest(test, InetAddress.class, InetAddress.getByName("2001:0db8:85a3:0000:0000:8a2e:0370:7334"), "2001:db8:85a3:0:0:8a2e:370:7334");
    }

//    @Test(expected=RuntimeException.class)
//    public void test_InetAddress_invalidFormat() {
//        JDKStringConverter.INET_ADDRESS.convertFromString(InetAddress.class, "RUBBISH");
//    }

    @Test
    public void test_File() {
        JDKStringConverter test = JDKStringConverter.FILE;
        File file = new File("/path/to/file");
        doTest(test, File.class, file, file.toString());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void test_Date() {
        TimeZone zone = TimeZone.getDefault();
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));
            JDKStringConverter test = JDKStringConverter.DATE;
            doTest(test, Date.class, new Date(2010 - 1900, 9 - 1, 3, 12, 34, 5), "2010-09-03T12:34:05.000+02:00");
            doTest(test, Date.class, new Date(2011 - 1900, 1 - 1, 4, 12, 34, 5), "2011-01-04T12:34:05.000+01:00");
        } finally {
            TimeZone.setDefault(zone);
        }
    }

    @Test(expected=RuntimeException.class)
    public void test_Date_invalidLength() {
        JDKStringConverter.DATE.convertFromString(Date.class, "2010-09-03");
    }

    @Test(expected=RuntimeException.class)
    public void test_Date_invalidFormat() {
        JDKStringConverter.DATE.convertFromString(Date.class, "2010-09-03XXX:34:05.000+02:00");
    }

    @Test
    public void test_Calendar() {
        JDKStringConverter test = JDKStringConverter.CALENDAR;
        GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        cal.set(2010, 9 - 1, 3, 12, 34, 5);
        cal.set(Calendar.MILLISECOND, 0);
        doTest(test, Calendar.class, cal, "2010-09-03T12:34:05.000+02:00[Europe/Paris]");
        
        GregorianCalendar cal2 = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        cal2.set(2011, 1 - 1, 4, 12, 34, 5);
        cal2.set(Calendar.MILLISECOND, 0);
        doTest(test, Calendar.class, cal2, "2011-01-04T12:34:05.000+01:00[Europe/Paris]");
    }

    @Test(expected=RuntimeException.class)
    public void test_Calendar_invalidLength() {
        JDKStringConverter.CALENDAR.convertFromString(GregorianCalendar.class, "2010-09-03");
    }

    @Test(expected=RuntimeException.class)
    public void test_Calendar_invalidFormat() {
        JDKStringConverter.CALENDAR.convertFromString(GregorianCalendar.class, "2010-09-03XXX:34:05.000+02:00[Europe/London]");
    }

    @Test(expected=RuntimeException.class)
    public void test_Calendar_notGregorian() {
        JDKStringConverter.CALENDAR.convertToString(new Calendar() {
            private static final long serialVersionUID = 1L;
            @Override
            public void roll(int field, boolean up) {
            }
            @Override
            public int getMinimum(int field) {
                return 0;
            }
            @Override
            public int getMaximum(int field) {
                return 0;
            }
            @Override
            public int getLeastMaximum(int field) {
                return 0;
            }
            @Override
            public int getGreatestMinimum(int field) {
                return 0;
            }
            @Override
            protected void computeTime() {
            }
            @Override
            protected void computeFields() {
            }
            @Override
            public void add(int field, int amount) {
            }
        });
    }

    @Test
    public void test_Enum() {
        JDKStringConverter test = JDKStringConverter.ENUM;
        assertEquals(Enum.class, test.getType());
        assertEquals("CEILING", test.convertToString(RoundingMode.CEILING));
        assertEquals(RoundingMode.CEILING, test.convertFromString(RoundingMode.class, "CEILING"));
    }

    @Test(expected=RuntimeException.class)
    public void test_Enum_invalidConstant() {
        JDKStringConverter.ENUM.convertFromString(RoundingMode.class, "RUBBISH");
    }

    //-----------------------------------------------------------------------
    public void doTest(JDKStringConverter test, Class<?> cls, Object obj, String str) {
        doTest(test, cls, obj, str, obj);
    }

    public void doTest(JDKStringConverter test, Class<?> cls, Object obj, String str, Object objFromStr) {
        assertEquals(cls, test.getType());
        assertEquals(str, test.convertToString(obj));
        assertEquals(objFromStr, test.convertFromString(cls, str));
    }

}
