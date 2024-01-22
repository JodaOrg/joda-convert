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

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
import java.util.function.Function;

/**
 * Conversion between JDK classes and a {@code String}.
 */
enum JDKStringConverter implements TypedStringConverter<Object> {

    /**
     * String converter.
     */
    STRING(String.class, str -> str),
    /**
     * CharSequence converter.
     */
    CHAR_SEQUENCE(CharSequence.class, str -> str),
    /**
     * StringBuffer converter.
     */
    STRING_BUFFER(StringBuffer.class, StringBuffer::new),
    /**
     * StringBuilder converter.
     */
    STRING_BUILDER(StringBuilder.class, StringBuilder::new),
    /**
     * Long converter.
     */
    LONG(Long.class, Long::valueOf),
    /**
     * Integer converter.
     */
    INTEGER(Integer.class, Integer::valueOf),
    /**
     * Short converter.
     */
    SHORT(Short.class, Short::valueOf),
    /**
     * Byte converter.
     */
    BYTE(Byte.class, Byte::valueOf),
    /**
     * String converter.
     */
    BYTE_ARRAY(byte[].class, JDKStringConverter::printBase64Binary, JDKStringConverter::parseBase64Binary),
    /**
     * Character converter.
     */
    CHARACTER(Character.class, JDKStringConverter::parseCharacter),
    /**
     * String converter.
     */
    CHAR_ARRAY(char[].class, JDKStringConverter::printCharArray, String::toCharArray),
    /**
     * Boolean converter.
     */
    BOOLEAN(Boolean.class, JDKStringConverter::parseBoolean),
    /**
     * Double converter.
     */
    DOUBLE(Double.class, Double::valueOf),
    /**
     * Float converter.
     */
    FLOAT(Float.class, Float::valueOf),
    /**
     * BigInteger converter.
     */
    BIG_INTEGER(BigInteger.class, BigInteger::new),
    /**
     * BigDecimal converter.
     */
    BIG_DECIMAL(BigDecimal.class, BigDecimal::new),
    /**
     * AtomicLong converter.
     */
    ATOMIC_LONG(AtomicLong.class, str -> new AtomicLong(Long.parseLong(str))),
    /**
     * AtomicLong converter.
     */
    ATOMIC_INTEGER(AtomicInteger.class, str -> new AtomicInteger(Integer.parseInt(str))),
    /**
     * AtomicBoolean converter.
     */
    ATOMIC_BOOLEAN(AtomicBoolean.class, JDKStringConverter::parseAtomicBoolean),
    /**
     * Locale converter.
     */
    LOCALE(Locale.class, JDKStringConverter::parseLocale),
    /**
     * Class converter.
     */
    CLASS(Class.class, JDKStringConverter::printClass, JDKStringConverter::parseClass),
    /**
     * Package converter.
     */
    PACKAGE(Package.class, JDKStringConverter::printPackage, Package::getPackage),
    /**
     * Currency converter.
     */
    CURRENCY(Currency.class, Currency::getInstance),
    /**
     * TimeZone converter.
     */
    TIME_ZONE(TimeZone.class, JDKStringConverter::printTimeZone, TimeZone::getTimeZone),
    /**
     * UUID converter.
     */
    UUID(UUID.class, java.util.UUID::fromString),
    /**
     * URL converter.
     */
    URL(URL.class, JDKStringConverter::parseURL),
    /**
     * URI converter.
     */
    URI(URI.class, java.net.URI::create),
    /**
     * InetAddress converter.
     */
    INET_ADDRESS(InetAddress.class, JDKStringConverter::printInetAddress, JDKStringConverter::parseInetAddress),
    /**
     * File converter.
     */
    FILE(File.class, File::new),
    /**
     * Date converter.
     */
    DATE(Date.class, JDKStringConverter::printDate, JDKStringConverter::parseDate),
    /**
     * Calendar converter.
     */
    CALENDAR(Calendar.class, JDKStringConverter::printCalendar, JDKStringConverter::parseCalendar),
    /**
     * Instant converter.
     */
    INSTANT(Instant.class, Instant::parse),
    /**
     * Duration converter.
     */
    DURATION(Duration.class, Duration::parse),
    /**
     * LocalDate converter.
     */
    LOCAL_DATE(LocalDate.class, LocalDate::parse),
    /**
     * LocalTime converter.
     */
    LOCAL_TIME(LocalTime.class, LocalTime::parse),
    /**
     * LocalDateTime converter.
     */
    LOCAL_DATE_TIME(LocalDateTime.class, LocalDateTime::parse),
    /**
     * OffsetTime converter.
     */
    OFFSET_TIME(OffsetTime.class, OffsetTime::parse),
    /**
     * OffsetDateTime converter.
     */
    OFFSET_DATE_TIME(OffsetDateTime.class, OffsetDateTime::parse),
    /**
     * ZonedDateTime converter.
     */
    ZONED_DATE_TIME(ZonedDateTime.class, ZonedDateTime::parse),
    /**
     * Year converter.
     */
    YEAR(Year.class, Year::parse),
    /**
     * YearMonth converter.
     */
    YEAR_MONTH(YearMonth.class, YearMonth::parse),
    /**
     * MonthDay converter.
     */
    MONTH_DAY(MonthDay.class, MonthDay::parse),
    /**
     * Period converter.
     */
    PERIOD(Period.class, Period::parse),
    /**
     * ZoneOffset converter.
     */
    ZONE_OFFSET(ZoneOffset.class, ZoneOffset::of),
    /**
     * ZoneId converter.
     */
    ZONE_ID(ZoneId.class, ZoneId::of),
    /**
     * OptionalInt converter.
     */
    OPTIONAL_INT(OptionalInt.class, JDKStringConverter::printOptionalInt, JDKStringConverter::parseOptionalInt),
    /**
     * OptionalLong converter.
     */
    OPTIONAL_LONG(OptionalLong.class, JDKStringConverter::printOptionalLong, JDKStringConverter::parseOptionalLong),
    /**
     * OptionalDouble converter.
     */
    OPTIONAL_DOUBLE(OptionalDouble.class, JDKStringConverter::printOptionalDouble, JDKStringConverter::parseOptionalDouble),
    ;

    private Class<?> type;
    private Function<Object, String> toStringFn;
    private Function<String, Object> fromStringFn;

    private JDKStringConverter(Class<?> type, Function<String, Object> fromStringFn) {
        this.type = type;
        this.toStringFn = Object::toString;
        this.fromStringFn = fromStringFn;
    }

    private JDKStringConverter(
            Class<?> type,
            Function<Object, String> toStringFn,
            Function<String, Object> fromStringFn) {
        this.type = type;
        this.toStringFn = toStringFn;
        this.fromStringFn = fromStringFn;
    }

    /**
     * Gets the type of the converter.
     * @return the type, not null
     */
    @Override
    public Class<?> getEffectiveType() {
        return type;
    }

    //-----------------------------------------------------------------------
    @Override
    public String convertToString(Object object) {
        return toStringFn.apply(object);
    }

    @Override
    public Object convertFromString(Class<? extends Object> cls, String str) {
        return fromStringFn.apply(str);
    }

    //-----------------------------------------------------------------------
    private static String base64Str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
    private static char[] base64Array = base64Str.toCharArray();
    private static final int MASK_8BIT = 0xff;
    private static final int MASK_6BIT = 0x3f;

    private static String printBase64Binary(Object obj) {
        var array = (byte[]) obj;
        var len = array.length;
        var buf = new char[((len + 2) / 3) * 4];
        var pos = 0;
        for (var i = 0; i < len; i += 3) {
            var remaining = len - i;
            if (remaining >= 3) {
                var bits = (array[i] & MASK_8BIT) << 16 | (array[i + 1] & MASK_8BIT) <<  8 | (array[i + 2] & MASK_8BIT);
                buf[pos++] = base64Array[(bits >>> 18) & MASK_6BIT];
                buf[pos++] = base64Array[(bits >>> 12) & MASK_6BIT];
                buf[pos++] = base64Array[(bits >>> 6) & MASK_6BIT];
                buf[pos++] = base64Array[bits & MASK_6BIT];
            } else if (remaining == 2) {
                var bits = (array[i] & MASK_8BIT) << 16 | (array[i + 1] & MASK_8BIT) <<  8;
                buf[pos++] = base64Array[(bits >>> 18) & MASK_6BIT];
                buf[pos++] = base64Array[(bits >>> 12) & MASK_6BIT];
                buf[pos++] = base64Array[(bits >>> 6) & MASK_6BIT];
                buf[pos++] = '=';
            } else {
                var bits = (array[i] & MASK_8BIT) << 16;
                buf[pos++] = base64Array[(bits >>> 18) & MASK_6BIT];
                buf[pos++] = base64Array[(bits >>> 12) & MASK_6BIT];
                buf[pos++] = '=';
                buf[pos++] = '=';
            }
        }
        return new String(buf);
    }

    private static byte[] parseBase64Binary(String str) {
        // strict parser, must have length divisble by 4
        if (str.length() % 4 != 0) {
            throw new IllegalArgumentException("Invalid Base64 string");
        }
        // base64Str has 65 characters, with '=' at the end which is masked away
        var parsedLen = (str.length() * 3) / 4;
        var decoded = new byte[parsedLen];
        var inChars = str.toCharArray();
        var pos = 0;
        for (var i = 0; i < inChars.length; ) {
            var bits = (base64Str.indexOf(inChars[i++]) & MASK_6BIT) << 18 |
                            (base64Str.indexOf(inChars[i++]) & MASK_6BIT) << 12 |
                            (base64Str.indexOf(inChars[i++]) & MASK_6BIT) << 6 |
                            (base64Str.indexOf(inChars[i++]) & MASK_6BIT);
            decoded[pos++] = (byte) ((bits >>> 16) & MASK_8BIT);
            decoded[pos++] = (byte) ((bits >>> 8) & MASK_8BIT);
            decoded[pos++] = (byte)  (bits & MASK_8BIT);
        }
        // fixup avoiding Arrays.copyRange
        if (str.endsWith("==")) {
            var result = new byte[parsedLen - 2];
            System.arraycopy(decoded, 0, result, 0, parsedLen - 2);
            return result;
        } else if (str.endsWith("=")) {
            var result = new byte[parsedLen - 1];
            System.arraycopy(decoded, 0, result, 0, parsedLen - 1);
            return result;
        }
        return decoded;
    }

    private static Character parseCharacter(String str) {
        if (str.length() != 1) {
            throw new IllegalArgumentException("Character value must be a string length 1");
        }
        return Character.valueOf(str.charAt(0));
    }

    private static String printCharArray(Object obj) {
        return new String((char[]) obj);
    }

    private static Boolean parseBoolean(String str) {
        if ("true".equalsIgnoreCase(str)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(str)) {
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException("Boolean value must be 'true' or 'false', case insensitive");
    }

    private static AtomicBoolean parseAtomicBoolean(String str) {
        if ("true".equalsIgnoreCase(str)) {
            return new AtomicBoolean(true);
        }
        if ("false".equalsIgnoreCase(str)) {
            return new AtomicBoolean(false);
        }
        throw new IllegalArgumentException("Boolean value must be 'true' or 'false', case insensitive");
    }

    private static Locale parseLocale(String str) {
        var split = str.split("_", 3);
        switch (split.length) {
            case 1:
                return new Locale(split[0]);
            case 2:
                return new Locale(split[0], split[1]);
            case 3:
                return new Locale(split[0], split[1], split[2]);
        }
        throw new IllegalArgumentException("Unable to parse Locale: " + str);
    }

    private static String printClass(Object obj) {
        return ((Class<?>) obj).getName();
    }

    private static Class<?> parseClass(String str) {
        try {
            return RenameHandler.INSTANCE.lookupType(str);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Unable to create type: " + str, ex);
        }
    }

    private static String printPackage(Object obj) {
        return ((Package) obj).getName();
    }

    private static String printTimeZone(Object obj) {
        return ((TimeZone) obj).getID();
    }

    private static URL parseURL(String str) {
        try {
            return new URL(str);
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    private static String printInetAddress(Object obj) {
        return ((InetAddress) obj).getHostAddress();
    }

    private static InetAddress parseInetAddress(String str) {
        try {
            return InetAddress.getByName(str);
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String printDate(Object obj) {
        var format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        var str = format.format(obj);
        return str.substring(0, 26) + ":" + str.substring(26);
    }

    private static Date parseDate(String str) {
        if (str.length() != 29) {
            throw new IllegalArgumentException("Unable to parse date: " + str);
        }
        var str2 = str.substring(0, 26) + str.substring(27);
        var format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            return format.parse(str2);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String printCalendar(Object obj) {
        if (obj instanceof GregorianCalendar == false) {
            throw new RuntimeException("Unable to convert calendar as it is not a GregorianCalendar");
        }
        var cal = (GregorianCalendar) obj;
        var format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        format.setCalendar(cal);
        var str = format.format(cal.getTime());
        return str.substring(0, 26) + ":" + str.substring(26) + "[" + cal.getTimeZone().getID() + "]";
    }

    private static Calendar parseCalendar(String str) {
        if (str.length() < 31 || str.charAt(26) != ':' || str.charAt(29) != '[' || str.charAt(str.length() - 1) != ']') {
            throw new IllegalArgumentException("Unable to parse date: " + str);
        }
        var zone = TimeZone.getTimeZone(str.substring(30, str.length() - 1));
        var str2 = str.substring(0, 26) + str.substring(27, 29);
        var format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        var cal = new GregorianCalendar(zone);
        cal.setTimeInMillis(0);
        format.setCalendar(cal);
        try {
            format.parseObject(str2);
            return format.getCalendar();
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String printOptionalInt(Object obj) {
        var optional = (OptionalInt) obj;
        return optional.isEmpty() ? "" : Integer.toString(optional.getAsInt());
    }

    private static OptionalInt parseOptionalInt(String str) {
        return "".equals(str) ? OptionalInt.empty() : OptionalInt.of(Integer.parseInt(str));
    }

    private static String printOptionalLong(Object obj) {
        var optional = (OptionalLong) obj;
        return optional.isEmpty() ? "" : Long.toString(optional.getAsLong());
    }

    private static OptionalLong parseOptionalLong(String str) {
        return "".equals(str) ? OptionalLong.empty() : OptionalLong.of(Long.parseLong(str));
    }

    private static String printOptionalDouble(Object obj) {
        var optional = (OptionalDouble) obj;
        return optional.isEmpty() ? "" : Double.toString(optional.getAsDouble());
    }

    private static OptionalDouble parseOptionalDouble(String str) {
        return "".equals(str) ? OptionalDouble.empty() : OptionalDouble.of(Double.parseDouble(str));
    }

}