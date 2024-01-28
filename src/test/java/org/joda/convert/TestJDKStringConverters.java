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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;

/**
 * Test JDKStringConverters.
 */
class TestJDKStringConverters {
    // avoid var in this class, as precise type checks are useful

    @Test
    void test_String() {
        JDKStringConverter test = JDKStringConverter.STRING;
        doTest(test, String.class, "Hello", "Hello");
    }

    @Test
    void test_StringBuffer() {
        JDKStringConverter test = JDKStringConverter.STRING_BUFFER;
        Object obj = new StringBuffer("Hello");
        assertThat(test.getEffectiveType()).isEqualTo(StringBuffer.class);
        assertThat(test.convertToString(obj)).isEqualTo("Hello");
        StringBuffer back = (StringBuffer) test.convertFromString(StringBuffer.class, "Hello");
        assertThat(back.toString()).isEqualTo("Hello");
    }

    @Test
    void test_StringBuilder() {
        JDKStringConverter test = JDKStringConverter.STRING_BUILDER;
        Object obj = new StringBuilder("Hello");
        assertThat(test.getEffectiveType()).isEqualTo(StringBuilder.class);
        assertThat(test.convertToString(obj)).isEqualTo("Hello");
        StringBuilder back = (StringBuilder) test.convertFromString(StringBuilder.class, "Hello");
        assertThat(back.toString()).isEqualTo("Hello");
    }

    @Test
    void test_CharSequence() {
        JDKStringConverter test = JDKStringConverter.CHAR_SEQUENCE;
        doTest(test, CharSequence.class, "Hello", "Hello");
        doTest(test, CharSequence.class, new StringBuffer("Hello"), "Hello", "Hello");
        doTest(test, CharSequence.class, new StringBuilder("Hello"), "Hello", "Hello");
    }

    @Test
    void test_Long() {
        JDKStringConverter test = JDKStringConverter.LONG;
        doTest(test, Long.class, Long.valueOf(12L), "12");
    }

    @Test
    void test_Int() {
        JDKStringConverter test = JDKStringConverter.INTEGER;
        doTest(test, Integer.class, Integer.valueOf(12), "12");
    }

    @Test
    void test_Short() {
        JDKStringConverter test = JDKStringConverter.SHORT;
        doTest(test, Short.class, Short.valueOf((byte) 12), "12");
    }

    @Test
    void test_Character() {
        JDKStringConverter test = JDKStringConverter.CHARACTER;
        doTest(test, Character.class, Character.valueOf('a'), "a");
        doTest(test, Character.class, Character.valueOf('z'), "z");
    }

    @Test
    void test_Character_fail() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> JDKStringConverter.CHARACTER.convertFromString(Character.class, "RUBBISH"));
    }

    @Test
    void test_charArray() {
        JDKStringConverter test = JDKStringConverter.CHAR_ARRAY;
        char[] array = new char[] {'M', 'a', 'p'};
        String str = "Map";
        assertThat(test.getEffectiveType()).isEqualTo(char[].class);
        assertThat(test.convertToString(array)).isEqualTo(str);
        assertThat((char[]) test.convertFromString(char[].class, str)).isEqualTo(array);
    }

    @Test
    void test_Byte() {
        JDKStringConverter test = JDKStringConverter.BYTE;
        doTest(test, Byte.class, Byte.valueOf((byte) 12), "12");
    }

    @Test
    void test_byteArray1() {
        JDKStringConverter test = JDKStringConverter.BYTE_ARRAY;
        byte[] array = new byte[] {77, 97, 112};
        String str = "TWFw";
        assertThat(test.getEffectiveType()).isEqualTo(byte[].class);
        assertThat(test.convertToString(array)).isEqualTo(str);
        assertThat((byte[]) test.convertFromString(byte[].class, str)).isEqualTo(array);
    }

    @Test
    void test_byteArray2() {
        JDKStringConverter test = JDKStringConverter.BYTE_ARRAY;
        byte[] array = new byte[] {77, 97};
        String str = "TWE=";
        assertThat(test.getEffectiveType()).isEqualTo(byte[].class);
        assertThat(test.convertToString(array)).isEqualTo(str);
        assertThat((byte[]) test.convertFromString(byte[].class, str)).isEqualTo(array);
    }

    @Test
    void test_byteArray3() {
        JDKStringConverter test = JDKStringConverter.BYTE_ARRAY;
        byte[] array = new byte[] {77};
        String str = "TQ==";
        assertThat(test.getEffectiveType()).isEqualTo(byte[].class);
        assertThat(test.convertToString(array)).isEqualTo(str);
        assertThat((byte[]) test.convertFromString(byte[].class, str)).isEqualTo(array);
    }

    @Test
    void test_byteArray4() {
        JDKStringConverter test = JDKStringConverter.BYTE_ARRAY;
        byte[] array = new byte[] {73, 97, 112, 77};
        String str = "SWFwTQ==";
        assertThat(test.getEffectiveType()).isEqualTo(byte[].class);
        assertThat(test.convertToString(array)).isEqualTo(str);
        assertThat((byte[]) test.convertFromString(byte[].class, str)).isEqualTo(array);
    }

    @Test
    void test_Boolean() {
        JDKStringConverter test = JDKStringConverter.BOOLEAN;
        doTest(test, Boolean.class, Boolean.TRUE, "true");
        doTest(test, Boolean.class, Boolean.FALSE, "false");
    }

    @Test
    void test_Boolean_fail() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> JDKStringConverter.BOOLEAN.convertFromString(Boolean.class, "RUBBISH"));
    }

    @Test
    void test_Double() {
        JDKStringConverter test = JDKStringConverter.DOUBLE;
        doTest(test, Double.class, Double.valueOf(12.4d), "12.4");
    }

    @Test
    void test_Float() {
        JDKStringConverter test = JDKStringConverter.FLOAT;
        doTest(test, Float.class, Float.valueOf(12.2f), "12.2");
    }

    @Test
    void test_BigInteger() {
        JDKStringConverter test = JDKStringConverter.BIG_INTEGER;
        doTest(test, BigInteger.class, BigInteger.valueOf(12L), "12");
    }

    @Test
    void test_BigDecimal() {
        JDKStringConverter test = JDKStringConverter.BIG_DECIMAL;
        doTest(test, BigDecimal.class, BigDecimal.valueOf(12.23d), "12.23");
    }

    @Test
    void test_AtomicLong() {
        JDKStringConverter test = JDKStringConverter.ATOMIC_LONG;
        AtomicLong obj = new AtomicLong(12);
        assertThat(test.getEffectiveType()).isEqualTo(AtomicLong.class);
        assertThat(test.convertToString(obj)).isEqualTo("12");
        AtomicLong back = (AtomicLong) test.convertFromString(AtomicLong.class, "12");
        assertThat(back.get()).isEqualTo(12);
    }

    @Test
    void test_AtomicInteger() {
        JDKStringConverter test = JDKStringConverter.ATOMIC_INTEGER;
        AtomicInteger obj = new AtomicInteger(12);
        assertThat(test.getEffectiveType()).isEqualTo(AtomicInteger.class);
        assertThat(test.convertToString(obj)).isEqualTo("12");
        AtomicInteger back = (AtomicInteger) test.convertFromString(AtomicInteger.class, "12");
        assertThat(back.get()).isEqualTo(12);
    }

    @Test
    void test_AtomicBoolean_true() {
        JDKStringConverter test = JDKStringConverter.ATOMIC_BOOLEAN;
        AtomicBoolean obj = new AtomicBoolean(true);
        assertThat(test.getEffectiveType()).isEqualTo(AtomicBoolean.class);
        assertThat(test.convertToString(obj)).isEqualTo("true");
        AtomicBoolean back = (AtomicBoolean) test.convertFromString(AtomicBoolean.class, "true");
        assertThat(back.get()).isTrue();
    }

    @Test
    void test_AtomicBoolean_false() {
        JDKStringConverter test = JDKStringConverter.ATOMIC_BOOLEAN;
        AtomicBoolean obj = new AtomicBoolean(false);
        assertThat(test.getEffectiveType()).isEqualTo(AtomicBoolean.class);
        assertThat(test.convertToString(obj)).isEqualTo("false");
        AtomicBoolean back = (AtomicBoolean) test.convertFromString(AtomicBoolean.class, "false");
        assertThat(back.get()).isFalse();
    }

    @Test
    void test_AtomicBoolean_fail() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> JDKStringConverter.ATOMIC_BOOLEAN.convertFromString(AtomicBoolean.class, "RUBBISH"));
    }

    @Test
    void test_Locale() {
        JDKStringConverter test = JDKStringConverter.LOCALE;
        doTest(test, Locale.class, new Locale("en"), "en");
        doTest(test, Locale.class, new Locale("en", "GB"), "en_GB");
        doTest(test, Locale.class, new Locale("en", "GB", "VARIANT_B"), "en_GB_VARIANT_B");
    }

    @Test
    void test_Class() {
        JDKStringConverter test = JDKStringConverter.CLASS;
        doTest(test, Class.class, Locale.class, "java.util.Locale");
        doTest(test, Class.class, FromString.class, "org.joda.convert.FromString");
    }

    @Test
    void test_Class_primitive() {
        JDKStringConverter test = JDKStringConverter.CLASS;
        doTest(test, Class.class, byte.class, "byte");
        doTest(test, Class.class, short.class, "short");
        doTest(test, Class.class, int.class, "int");
        doTest(test, Class.class, long.class, "long");
        doTest(test, Class.class, float.class, "float");
        doTest(test, Class.class, double.class, "double");
        doTest(test, Class.class, boolean.class, "boolean");
        doTest(test, Class.class, char.class, "char");
        doTest(test, Class.class, void.class, "void");
    }

    @Test
    void test_Class_array() {
        JDKStringConverter test = JDKStringConverter.CLASS;
        doTest(test, Class.class, byte[].class, "[B");
        doTest(test, Class.class, FromString[].class, "[Lorg.joda.convert.FromString;");
        doTest(test, Class.class, FromString[][].class, "[[Lorg.joda.convert.FromString;");
    }

    @Test
    void test_Class_fail() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> JDKStringConverter.CLASS.convertFromString(Class.class, "RUBBISH"));
    }

    @Test
    void test_Class_withRename() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> JDKStringConverter.CLASS.convertFromString(Class.class, "org.foo.StringConvert"));
        RenameHandler.INSTANCE.renamedType("org.foo.StringConvert", StringConvert.class);
        assertThat(JDKStringConverter.CLASS.convertFromString(Class.class, "org.foo.StringConvert")).isEqualTo(StringConvert.class);
    }

    @Test
    void test_Package() {
        JDKStringConverter test = JDKStringConverter.PACKAGE;
        doTest(test, Package.class, Locale.class.getPackage(), "java.util");
        doTest(test, Package.class, FromString.class.getPackage(), "org.joda.convert");
    }

    @Test
    void test_Currency() {
        JDKStringConverter test = JDKStringConverter.CURRENCY;
        doTest(test, Currency.class, Currency.getInstance("GBP"), "GBP");
        doTest(test, Currency.class, Currency.getInstance("USD"), "USD");
    }

    @Test
    void test_TimeZone() {
        JDKStringConverter test = JDKStringConverter.TIME_ZONE;
        doTest(test, TimeZone.class, TimeZone.getTimeZone("Europe/London"), "Europe/London");
        doTest(test, TimeZone.class, TimeZone.getTimeZone("America/New_York"), "America/New_York");
    }

    @Test
    void test_UUID() {
        JDKStringConverter test = JDKStringConverter.UUID;
        UUID uuid = UUID.randomUUID();
        doTest(test, UUID.class, uuid, uuid.toString());
    }

    @Test
    void test_URL() throws Exception {
        JDKStringConverter test = JDKStringConverter.URL;
        doTest(test, URL.class, new URL("http://localhost:8080/my/test"), "http://localhost:8080/my/test");
        doTest(test, URL.class, new URL(null, "ftp:world"), "ftp:world");
    }

    @Test
    void test_URL_invalidFormat() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> JDKStringConverter.URL.convertFromString(URL.class, "RUBBISH:RUBBISH"));
    }

    @Test
    void test_URI() {
        JDKStringConverter test = JDKStringConverter.URI;
        doTest(test, URI.class, URI.create("http://localhost:8080/my/test"), "http://localhost:8080/my/test");
        doTest(test, URI.class, URI.create("/my/test"), "/my/test");
        doTest(test, URI.class, URI.create("/my/../test"), "/my/../test");
        doTest(test, URI.class, URI.create("urn:hello"), "urn:hello");
    }

    @Test
    void test_InetAddress() throws Exception {
        JDKStringConverter test = JDKStringConverter.INET_ADDRESS;
        doTest(test, InetAddress.class, InetAddress.getByName("1.2.3.4"), "1.2.3.4");

        InetAddress obj = InetAddress.getByName("2001:0db8:85a3:0000:0000:8a2e:0370:7334");
        String str = test.convertToString(obj);
        assertThat(str.equals("2001:db8:85a3:0:0:8a2e:370:7334") || str.equals("2001:db8:85a3::8a2e:370:7334")).isTrue();
        assertThat(test.convertFromString(InetAddress.class, str)).isEqualTo(obj);
        assertThat(test.convertFromString(InetAddress.class, "2001:db8:85a3:0:0:8a2e:370:7334")).isEqualTo(obj);
        assertThat(test.convertFromString(InetAddress.class, "2001:db8:85a3::8a2e:370:7334")).isEqualTo(obj);
    }

//    @Test(expected=RuntimeException.class)
//    public void test_InetAddress_invalidFormat() {
//        JDKStringConverter.INET_ADDRESS.convertFromString(InetAddress.class, "RUBBISH");
//    }

    @Test
    void test_File() {
        JDKStringConverter test = JDKStringConverter.FILE;
        File file = new File("/path/to/file");
        doTest(test, File.class, file, file.toString());
    }

    @SuppressWarnings("deprecation")
    @Test
    void test_Date() {
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

    @Test
    void test_Date_invalidLength() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> JDKStringConverter.DATE.convertFromString(Date.class, "2010-09-03"));
    }

    @Test
    void test_Date_invalidFormat() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> JDKStringConverter.DATE.convertFromString(Date.class, "2010-09-03XXX:34:05.000+02:00"));
    }

    @Test
    void test_Calendar() {
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

    @Test
    void test_Calendar_invalidLength() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> JDKStringConverter.CALENDAR.convertFromString(GregorianCalendar.class, "2010-09-03"));
    }

    @Test
    void test_Calendar_invalidFormat() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> JDKStringConverter.CALENDAR.convertFromString(
                        GregorianCalendar.class, "2010-09-03XXX:34:05.000+02:00[Europe/London]"));
    }

    @Test
    void test_Calendar_notGregorian() {
        Calendar cal = new Calendar() {
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
        };
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> JDKStringConverter.CALENDAR.convertToString(cal));
    }

    @Test
    void test_Enum() {
        TypedStringConverter<RoundingMode> test = StringConvert.create().findTypedConverter(RoundingMode.class);
        assertThat(test.getEffectiveType()).isEqualTo(RoundingMode.class);
        assertThat(test.convertToString(RoundingMode.CEILING)).isEqualTo("CEILING");
        assertThat(test.convertFromString(RoundingMode.class, "CEILING")).isEqualTo(RoundingMode.CEILING);
    }

    @Test
    void test_Enum_invalidConstant() {
        TypedStringConverter<RoundingMode> test = StringConvert.create().findTypedConverter(RoundingMode.class);
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> test.convertFromString(RoundingMode.class, "RUBBISH"));
    }

    @Test
    void test_Enum_withRename() {
        TypedStringConverter<Status> test = StringConvert.create().findTypedConverter(Status.class);
        assertThat(test.convertToString(Status.VALID)).isEqualTo("VALID");
        assertThat(test.convertToString(Status.INVALID)).isEqualTo("INVALID");
        assertThat(test.convertFromString(Status.class, "VALID")).isEqualTo(Status.VALID);
        assertThat(test.convertFromString(Status.class, "INVALID")).isEqualTo(Status.INVALID);
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> test.convertFromString(Status.class, "OK"));
        RenameHandler.INSTANCE.renamedEnum("OK", Status.VALID);
        assertThat(test.convertFromString(Status.class, "OK")).isEqualTo(Status.VALID);
        assertThat(test.convertFromString(Status.class, "VALID")).isEqualTo(Status.VALID);
        assertThat(test.convertFromString(Status.class, "INVALID")).isEqualTo(Status.INVALID);
    }

    @Test
    void test_OptionalInt() {
        JDKStringConverter test = JDKStringConverter.OPTIONAL_INT;
        doTest(test, OptionalInt.class, OptionalInt.of(2), "2");
        doTest(test, OptionalInt.class, OptionalInt.empty(), "");
    }

    @Test
    void test_OptionalLong() {
        JDKStringConverter test = JDKStringConverter.OPTIONAL_LONG;
        doTest(test, OptionalLong.class, OptionalLong.of(2), "2");
        doTest(test, OptionalLong.class, OptionalLong.empty(), "");
    }

    @Test
    void test_OptionalDouble() {
        JDKStringConverter test = JDKStringConverter.OPTIONAL_DOUBLE;
        doTest(test, OptionalDouble.class, OptionalDouble.of(2.3), "2.3");
        doTest(test, OptionalDouble.class, OptionalDouble.empty(), "");
    }

    //-----------------------------------------------------------------------
    private void doTest(JDKStringConverter test, Class<?> cls, Object obj, String str) {
        doTest(test, cls, obj, str, obj);
    }

    private void doTest(JDKStringConverter test, Class<?> cls, Object obj, String str, Object objFromStr) {
        assertThat(test.getEffectiveType()).isEqualTo(cls);
        assertThat(test.convertToString(obj)).isEqualTo(str);
        assertThat(test.convertFromString(cls, str)).isEqualTo(objFromStr);
    }

}
