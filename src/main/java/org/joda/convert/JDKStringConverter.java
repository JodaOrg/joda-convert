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

/**
 * Conversion between JDK classes and a {@code String}.
 */
enum JDKStringConverter implements TypedStringConverter<Object> {

    /**
     * String converter.
     */
    STRING(String.class) {
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            return str;
        }
    },
    /**
     * CharSequence converter.
     */
    CHAR_SEQUENCE(CharSequence.class) {
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            return str;
        }
    },
    /**
     * StringBuffer converter.
     */
    STRING_BUFFER(StringBuffer.class) {
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            return new StringBuffer(str);
        }
    },
    /**
     * StringBuilder converter.
     */
    STRING_BUILDER(StringBuilder.class) {
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            return new StringBuilder(str);
        }
    },
    /**
     * Long converter.
     */
    LONG(Long.class) {
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            return new Long(str);
        }
    },

    /**
     * Integer converter.
     */
    INTEGER(Integer.class) {
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            return new Integer(str);
        }
    },

    /**
     * Short converter.
     */
    SHORT (Short.class) {
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            return new Short(str);
        }
    },

    /**
     * Byte converter.
     */
    BYTE(Byte.class) {
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            return new Byte(str);
        }
    },
    /**
     * String converter.
     */
    BYTE_ARRAY(byte[].class) {
        @Override
        public String convertToString(Object object) {
            return printBase64Binary((byte[]) object);
        }
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            return parseBase64Binary(str);
        }
    },
    /**
     * Character converter.
     */
    CHARACTER(Character.class) {
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            if (str.length() != 1) {
                throw new IllegalArgumentException("Character value must be a string length 1");
            }
            return new Character(str.charAt(0));
        }
    },
    /**
     * String converter.
     */
    CHAR_ARRAY(char[].class) {
        @Override
        public String convertToString(Object object) {
            return new String((char[]) object);
        }
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            return str.toCharArray();
        }
    },
    /**
     * Boolean converter.
     */
    BOOLEAN(Boolean.class) {
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            if ("true".equalsIgnoreCase(str)) {
                return Boolean.TRUE;
            }
            if ("false".equalsIgnoreCase(str)) {
                return Boolean.FALSE;
            }
            throw new IllegalArgumentException("Boolean value must be 'true' or 'false', case insensitive");
        }
    },
    /**
     * Double converter.
     */
    DOUBLE(Double.class) {
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            return new Double(str);
        }
    },
    /**
     * Float converter.
     */
    FLOAT(Float.class) {
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            return new Float(str);
        }
    },
    /**
     * BigInteger converter.
     */
    BIG_INTEGER(BigInteger.class) {
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            return new BigInteger(str);
        }
    },
    /**
     * BigDecimal converter.
     */
    BIG_DECIMAL(BigDecimal.class) {
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            return new BigDecimal(str);
        }
    },
    /**
     * AtomicLong converter.
     */
    ATOMIC_LONG(AtomicLong.class) {
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            long val = Long.parseLong(str);
            return new AtomicLong(val);
        }
    },
    /**
     * AtomicLong converter.
     */
    ATOMIC_INTEGER(AtomicInteger.class) {
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            int val = Integer.parseInt(str);
            return new AtomicInteger(val);
        }
    },
    /**
     * AtomicBoolean converter.
     */
    ATOMIC_BOOLEAN(AtomicBoolean.class) {
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            if ("true".equalsIgnoreCase(str)) {
                return new AtomicBoolean(true);
            }
            if ("false".equalsIgnoreCase(str)) {
                return new AtomicBoolean(false);
            }
            throw new IllegalArgumentException("Boolean value must be 'true' or 'false', case insensitive");
        }
    },
    /**
     * Locale converter.
     */
    LOCALE(Locale.class) {
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            String[] split = str.split("_", 3);
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
    },
    /**
     * Class converter.
     */
    CLASS(Class.class) {
        @Override
        public String convertToString(Object object) {
            return ((Class<?>) object).getName();
        }
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            try {
                return RenameHandler.INSTANCE.lookupType(str);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException("Unable to create type: " + str, ex);
            }
        }
    },
    /**
     * Package converter.
     */
    PACKAGE(Package.class) {
        @Override
        public String convertToString(Object object) {
            return ((Package) object).getName();
        }
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            return Package.getPackage(str);
        }
    },
    /**
     * Currency converter.
     */
    CURRENCY(Currency.class) {
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            return Currency.getInstance(str);
        }
    },
    /**
     * TimeZone converter.
     */
    TIME_ZONE(TimeZone.class) {
        @Override
        public String convertToString(Object object) {
            return ((TimeZone) object).getID();
        }
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            return TimeZone.getTimeZone(str);
        }
    },
    /**
     * UUID converter.
     */
    UUID(UUID.class) {
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            return java.util.UUID.fromString(str);
        }
    },
    /**
     * URL converter.
     */
    URL(URL.class) {
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            try {
                return new URL(str);
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
    },
    /**
     * URI converter.
     */
    URI(URI.class) {
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            return java.net.URI.create(str);
        }
    },
    /**
     * InetAddress converter.
     */
    INET_ADDRESS(InetAddress.class) {
        @Override
        public String convertToString(Object object) {
            return ((InetAddress) object).getHostAddress();
        }
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            try {
                return InetAddress.getByName(str);
            } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }
        }
    },
    /**
     * File converter.
     */
    FILE(File.class) {
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            return new File(str);
        }
    },
    /**
     * Date converter.
     */
    DATE(Date.class) {
        @Override
        public String convertToString(Object object) {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            String str = f.format(object);
            return str.substring(0, 26) + ":" + str.substring(26);
        }
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            if (str.length() != 29) {
                throw new IllegalArgumentException("Unable to parse date: " + str);
            }
            str = str.substring(0, 26) + str.substring(27);
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            try {
                return f.parseObject(str);
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
        }
    },
    /**
     * Calendar converter.
     */
    CALENDAR(Calendar.class) {
        @Override
        public String convertToString(Object object) {
            if (object instanceof GregorianCalendar == false) {
                throw new RuntimeException("Unable to convert calendar as it is not a GregorianCalendar");
            }
            GregorianCalendar cal = (GregorianCalendar) object;
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            f.setCalendar(cal);
            String str = f.format(cal.getTime());
            return str.substring(0, 26) + ":" + str.substring(26) + "[" + cal.getTimeZone().getID() + "]";
        }
        @Override
        public Object convertFromString(Class<?> cls, String str) {
            if (str.length() < 31 || str.charAt(26) != ':'
                    || str.charAt(29) != '[' || str.charAt(str.length() - 1) != ']') {
                throw new IllegalArgumentException("Unable to parse date: " + str);
            }
            TimeZone zone = TimeZone.getTimeZone(str.substring(30, str.length() - 1));
            str = str.substring(0, 26) + str.substring(27, 29);
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            GregorianCalendar cal = new GregorianCalendar(zone);
            cal.setTimeInMillis(0);
            f.setCalendar(cal);
            try {
                f.parseObject(str);
                return f.getCalendar();
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
        }
    },
    ;

    /** The type. */
    private Class<?> type;

    /**
     * Creates an enum.
     * @param type  the type, not null
     */
    JDKStringConverter(Class<?> type) {
        this.type = type;
    }

    /**
     * Gets the type of the converter.
     * @return the type, not null
     */
    Class<?> getType() {
        return type;
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
        return object.toString();
    }

    //-----------------------------------------------------------------------
    private static String base64Str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
    private static char[] base64Array = base64Str.toCharArray();
    private static final int MASK_8BIT = 0xff;
    private static final int MASK_6BIT = 0x3f;

    private static String printBase64Binary(byte[] array) {
        int len = array.length;
        char[] buf = new char[((len + 2) / 3) * 4];
        int pos = 0;
        for (int i = 0; i < len; i += 3) {
            int remaining = len - i;
            if (remaining >= 3) {
                int bits = (array[i] & MASK_8BIT) << 16 | (array[i + 1] & MASK_8BIT) <<  8 | (array[i + 2] & MASK_8BIT);
                buf[pos++] = base64Array[(bits >>> 18) & MASK_6BIT];
                buf[pos++] = base64Array[(bits >>> 12) & MASK_6BIT];
                buf[pos++] = base64Array[(bits >>> 6) & MASK_6BIT];
                buf[pos++] = base64Array[bits & MASK_6BIT];
            } else if (remaining == 2) {
                int bits = (array[i] & MASK_8BIT) << 16 | (array[i + 1] & MASK_8BIT) <<  8;
                buf[pos++] = base64Array[(bits >>> 18) & MASK_6BIT];
                buf[pos++] = base64Array[(bits >>> 12) & MASK_6BIT];
                buf[pos++] = base64Array[(bits >>> 6) & MASK_6BIT];
                buf[pos++] = '=';
            } else {
                int bits = (array[i] & MASK_8BIT) << 16;
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
        int parsedLen = (str.length() * 3) / 4;
        byte[] decoded = new byte[parsedLen];
        char[] inChars = str.toCharArray();
        int pos = 0;
        for (int i = 0; i < inChars.length; ) {
            int bits = (base64Str.indexOf(inChars[i++]) & MASK_6BIT) << 18 |
                            (base64Str.indexOf(inChars[i++]) & MASK_6BIT) << 12 |
                            (base64Str.indexOf(inChars[i++]) & MASK_6BIT) << 6 |
                            (base64Str.indexOf(inChars[i++]) & MASK_6BIT);
            decoded[pos++] = (byte) ((bits >>> 16) & MASK_8BIT);
            decoded[pos++] = (byte) ((bits >>> 8) & MASK_8BIT);
            decoded[pos++] = (byte)  (bits & MASK_8BIT);
        }
        // fixup avoiding Arrays.copyRange
        if (str.endsWith("==")) {
            byte[] result = new byte[parsedLen - 2];
            System.arraycopy(decoded, 0, result, 0, parsedLen - 2);
            return result;
        } else if (str.endsWith("=")) {
            byte[] result = new byte[parsedLen - 1];
            System.arraycopy(decoded, 0, result, 0, parsedLen - 1);
            return result;
        }
        return decoded;
    }

}
