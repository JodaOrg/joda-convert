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

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Currency;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Conversion between JDK classes and a {@code String}.
 */
enum JDKStringConverter implements StringConverter<Object> {
    // TODO BitSet, Enum, 

    /**
     * Long converter.
     */
    LONG(Long.class) {
        public Object convertFromString(String str) {
            return new Long(str);
        }
    },

    /**
     * Integer converter.
     */
    INTEGER(Integer.class) {
        public Object convertFromString(String str) {
            return new Integer(str);
        }
    },

    /**
     * Short converter.
     */
    SHORT (Short.class) {
        public Object convertFromString(String str) {
            return new Short(str);
        }
    },

    /**
     * Byte converter.
     */
    BYTE(Byte.class) {
        public Object convertFromString(String str) {
            return new Byte(str);
        }
    },
    /**
     * Character converter.
     */
    CHARACTER(Character.class) {
        public Object convertFromString(String str) {
            if (str.length() != 1) {
                throw new IllegalArgumentException("Character value must be a string length 1");
            }
            return new Character(str.charAt(0));
        }
    },
    /**
     * Boolean converter.
     */
    BOOLEAN(Boolean.class) {
        public Object convertFromString(String str) {
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
        public Object convertFromString(String str) {
            return new Double(str);
        }
    },
    /**
     * Float converter.
     */
    FLOAT(Float.class) {
        public Object convertFromString(String str) {
            return new Float(str);
        }
    },
    /**
     * BigInteger converter.
     */
    BIG_INTEGER(BigInteger.class) {
        public Object convertFromString(String str) {
            return new BigInteger(str);
        }
    },
    /**
     * BigDecimal converter.
     */
    BIG_DECIMAL(BigDecimal.class) {
        public Object convertFromString(String str) {
            return new BigDecimal(str);
        }
    },
    /**
     * AtomicLong converter.
     */
    ATOMIC_LONG(AtomicLong.class) {
        public Object convertFromString(String str) {
            long val = Long.parseLong(str);
            return new AtomicLong(val);
        }
    },
    /**
     * AtomicLong converter.
     */
    ATOMIC_INTEGER(AtomicInteger.class) {
        public Object convertFromString(String str) {
            int val = Integer.parseInt(str);
            return new AtomicInteger(val);
        }
    },
    /**
     * AtomicBoolean converter.
     */
    ATOMIC_BOOLEAN(AtomicBoolean.class) {
        public Object convertFromString(String str) {
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
        public Object convertFromString(String str) {
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
     * String converter.
     */
    STRING(String.class) {
        public Object convertFromString(String str) {
            return str;
        }
    },
    /**
     * CharSequence converter.
     */
    CHAR_SEQUENCE(CharSequence.class) {
        public Object convertFromString(String str) {
            return str;
        }
    },
    /**
     * StringBuffer converter.
     */
    STRING_BUFFER(StringBuffer.class) {
        public Object convertFromString(String str) {
            return str;
        }
    },
    /**
     * StringBuilder converter.
     */
    STRING_BUILDER(StringBuilder.class) {
        public Object convertFromString(String str) {
            return str;
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
        public Object convertFromString(String str) {
            try {
                return Thread.currentThread().getContextClassLoader().loadClass(str);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException("Unable to create class: " + str, ex);
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
        public Object convertFromString(String str) {
            return Package.getPackage(str);
        }
    },
    /**
     * Currency converter.
     */
    CURRENCY(Currency.class) {
        public Object convertFromString(String str) {
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
        public Object convertFromString(String str) {
            return TimeZone.getTimeZone(str);
        }
    },
    /**
     * UUID converter.
     */
    UUID(UUID.class) {
        public Object convertFromString(String str) {
            return java.util.UUID.fromString(str);
        }
    },
    /**
     * URL converter.
     */
    URL(URL.class) {
        public Object convertFromString(String str) {
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
        public Object convertFromString(String str) {
            return java.net.URI.create(str);
        }
    },
    /**
     * File converter.
     */
    FILE(File.class) {
        public Object convertFromString(String str) {
            return new File(str);
        }
    },
    ;

    /** The type. */
    private Class<?> type;

    /**
     * Creates an enum.
     * @param type  the type, not null
     */
    private JDKStringConverter(Class<?> type) {
        this.type = type;
    }

    /**
     * Gets the type of the converter.
     * @return the type, not null
     */
    Class<?> getType() {
        return type;
    }

    //-----------------------------------------------------------------------
    public String convertToString(Object object) {
        return object.toString();
    }

}
