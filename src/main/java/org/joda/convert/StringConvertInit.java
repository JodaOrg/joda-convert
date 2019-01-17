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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * Initialization of static constants, which does not include loading from config files.
 */
final class StringConvertInit {
    // NOTE!
    // There must be no references (direct or indirect) to RenameHandler or StringConvert
    // This class must be loaded first to avoid horrid loops in class initialization

    /**
     * Errors in class initialization are hard to debug.
     * Set -Dorg.joda.convert.debug=true on the command line to add extra logging to System.err
     */
    static final boolean LOG;
    static {
        String log = null;
        try {
            log = System.getProperty("org.joda.convert.debug");
        } catch (SecurityException ex) {
            // ignore
        }
        LOG = "true".equalsIgnoreCase(log);
    }

    /**
     * Singleton instance.
     */
    static final StringConvertInit INSTANCE = new StringConvertInit();

    /**
     * The set of standard factories.
     * These must not be mutated from outside this class.
     */
    final List<StringConverterFactory> factories = new ArrayList<StringConverterFactory>();  // CSIGNORE
    /**
     * The set of standard converters.
     * These must not be mutated from outside this class.
     */
    final Map<Class<?>, TypedStringConverter<?>> converters = new HashMap<Class<?>, TypedStringConverter<?>>();  // CSIGNORE

    //-----------------------------------------------------------------------
    /**
     * Creates a new instance.
     */
    private StringConvertInit() {
        registerJava6();
        tryRegisterGuava();
        tryRegisterJava8Optionals();
        tryRegisterTimeZone();
        tryRegisterJava8();
        tryRegisterThreeTenBackport();
        tryRegisterThreeTenOld();
    }

    // registers basic Java 6 types
    private void registerJava6() {
        for (JDKStringConverter conv : JDKStringConverter.values()) {
            converters.put(conv.getType(), conv);
        }
        converters.put(Boolean.TYPE, JDKStringConverter.BOOLEAN);
        converters.put(Byte.TYPE, JDKStringConverter.BYTE);
        converters.put(Short.TYPE, JDKStringConverter.SHORT);
        converters.put(Integer.TYPE, JDKStringConverter.INTEGER);
        converters.put(Long.TYPE, JDKStringConverter.LONG);
        converters.put(Float.TYPE, JDKStringConverter.FLOAT);
        converters.put(Double.TYPE, JDKStringConverter.DOUBLE);
        converters.put(Character.TYPE, JDKStringConverter.CHARACTER);
        factories.add(EnumStringConverterFactory.INSTANCE);
        factories.add(TypeStringConverterFactory.INSTANCE);
    }

    // tries to register the Guava converters
    private void tryRegisterGuava() {
        try {
            // Guava is not a direct dependency, which is significant in the Java 9 module system
            // to access Guava this module must add a read edge to the module graph
            // but since this code is written for Java 6, we have to do this by reflection
            // yuck
            Class<?> moduleClass = Class.class.getMethod("getModule").getReturnType();
            Object convertModule = Class.class.getMethod("getModule").invoke(StringConvertInit.class);
            Object layer = convertModule.getClass().getMethod("getLayer").invoke(convertModule);
            if (layer != null) {
                Object optGuava = layer.getClass().getMethod("findModule", String.class).invoke(layer, "com.google.common");
                boolean found = (Boolean) optGuava.getClass().getMethod("isPresent").invoke(optGuava);
                if (found) {
                    Object guavaModule = optGuava.getClass().getMethod("get").invoke(optGuava);
                    moduleClass.getMethod("addReads", moduleClass).invoke(convertModule, guavaModule);
                }
            }

        } catch (Throwable ex) {
            if (LOG) {
                System.err.println("tryRegisterGuava1: " + ex);
            }
        }
        try {
            // can now check for Guava
            // if we have created a read edge, or if we are on the classpath, this will succeed
            loadType("com.google.common.reflect.TypeToken");
            @SuppressWarnings("unchecked")
            Class<?> cls = (Class<TypedStringConverter<?>>) loadType("org.joda.convert.TypeTokenStringConverter");
            TypedStringConverter<?> conv = (TypedStringConverter<?>) cls.getDeclaredConstructor().newInstance();
            converters.put(conv.getEffectiveType(), conv);

        } catch (Throwable ex) {
            if (LOG) {
                System.err.println("tryRegisterGuava2: " + ex);
            }
        }
    }

    // tries to register the Java 8 optional classes
    private void tryRegisterJava8Optionals() {
        try {
            Class.forName("java.util.OptionalInt");
            @SuppressWarnings("unchecked")
            Class<?> cls1 = (Class<TypedStringConverter<?>>) Class.forName("org.joda.convert.OptionalIntStringConverter");
            TypedStringConverter<?> conv1 = (TypedStringConverter<?>) cls1.getDeclaredConstructor().newInstance();
            converters.put(conv1.getEffectiveType(), conv1);

            @SuppressWarnings("unchecked")
            Class<?> cls2 = (Class<TypedStringConverter<?>>) Class.forName("org.joda.convert.OptionalLongStringConverter");
            TypedStringConverter<?> conv2 = (TypedStringConverter<?>) cls2.getDeclaredConstructor().newInstance();
            converters.put(conv2.getEffectiveType(), conv2);

            @SuppressWarnings("unchecked")
            Class<?> cls3 = (Class<TypedStringConverter<?>>) Class.forName("org.joda.convert.OptionalDoubleStringConverter");
            TypedStringConverter<?> conv3 = (TypedStringConverter<?>) cls3.getDeclaredConstructor().newInstance();
            converters.put(conv3.getEffectiveType(), conv3);

        } catch (Throwable ex) {
            if (LOG) {
                System.err.println("tryRegisterOptionals: " + ex);
            }
        }
    }

    // tries to register the subclasses of TimeZone
    // try various things, doesn't matter if the map entry gets overwritten.
    private void tryRegisterTimeZone() {
        try {
            converters.put(SimpleTimeZone.class, JDKStringConverter.TIME_ZONE);

        } catch (Throwable ex) {
            if (LOG) {
                System.err.println("tryRegisterTimeZone1: " + ex);
            }
        }
        try {
            TimeZone zone = TimeZone.getDefault();
            converters.put(zone.getClass(), JDKStringConverter.TIME_ZONE);

        } catch (Throwable ex) {
            if (LOG) {
                System.err.println("tryRegisterTimeZone2: " + ex);
            }
        }
        try {
            TimeZone zone = TimeZone.getTimeZone("Europe/London");
            converters.put(zone.getClass(), JDKStringConverter.TIME_ZONE);

        } catch (Throwable ex) {
            if (LOG) {
                System.err.println("tryRegisterTimeZone3: " + ex);
            }
        }
    }

    // tries to register Java 8 classes
    private void tryRegisterJava8() {
        try {
            registerJdk("java.time.Instant", "parse");
            registerJdk("java.time.Duration", "parse");
            registerJdk("java.time.LocalDate", "parse");
            registerJdk("java.time.LocalTime", "parse");
            registerJdk("java.time.LocalDateTime", "parse");
            registerJdk("java.time.OffsetTime", "parse");
            registerJdk("java.time.OffsetDateTime", "parse");
            registerJdk("java.time.ZonedDateTime", "parse");
            registerJdk("java.time.Year", "parse");
            registerJdk("java.time.YearMonth", "parse");
            registerJdk("java.time.MonthDay", "parse");
            registerJdk("java.time.Period", "parse");
            registerJdk("java.time.ZoneOffset", "of");
            registerJdk("java.time.ZoneId", "of");

        } catch (Throwable ex) {
            if (LOG) {
                System.err.println("tryRegisterJava8: " + ex);
            }
        }
    }

    // tries to register ThreeTen backport classes
    private void tryRegisterThreeTenBackport() {
        try {
            registerJar("org.threeten.bp.Instant", "parse");
            registerJar("org.threeten.bp.Duration", "parse");
            registerJar("org.threeten.bp.LocalDate", "parse");
            registerJar("org.threeten.bp.LocalTime", "parse");
            registerJar("org.threeten.bp.LocalDateTime", "parse");
            registerJar("org.threeten.bp.OffsetTime", "parse");
            registerJar("org.threeten.bp.OffsetDateTime", "parse");
            registerJar("org.threeten.bp.ZonedDateTime", "parse");
            registerJar("org.threeten.bp.Year", "parse");
            registerJar("org.threeten.bp.YearMonth", "parse");
            registerJar("org.threeten.bp.MonthDay", "parse");
            registerJar("org.threeten.bp.Period", "parse");
            registerJar("org.threeten.bp.ZoneOffset", "of");
            registerJar("org.threeten.bp.ZoneId", "of");

        } catch (Throwable ex) {
            if (LOG) {
                System.err.println("tryRegisterThreeTenBackport: " + ex);
            }
        }
    }

    // tries to register ThreeTen ThreeTen/JSR-310 classes v0.6.3 and beyond
    private void tryRegisterThreeTenOld() {
        try {
            registerJar("javax.time.Instant", "parse");
            registerJar("javax.time.Duration", "parse");
            registerJar("javax.time.calendar.LocalDate", "parse");
            registerJar("javax.time.calendar.LocalTime", "parse");
            registerJar("javax.time.calendar.LocalDateTime", "parse");
            registerJar("javax.time.calendar.OffsetDate", "parse");
            registerJar("javax.time.calendar.OffsetTime", "parse");
            registerJar("javax.time.calendar.OffsetDateTime", "parse");
            registerJar("javax.time.calendar.ZonedDateTime", "parse");
            registerJar("javax.time.calendar.Year", "parse");
            registerJar("javax.time.calendar.YearMonth", "parse");
            registerJar("javax.time.calendar.MonthDay", "parse");
            registerJar("javax.time.calendar.Period", "parse");
            registerJar("javax.time.calendar.ZoneOffset", "of");
            registerJar("javax.time.calendar.ZoneId", "of");
            registerJar("javax.time.calendar.TimeZone", "of");

        } catch (Throwable ex) {
            if (LOG) {
                System.err.println("tryRegisterThreeTenOld: " + ex);
            }
        }
    }

    // registers a class
    private void registerJdk(String className, String fromStringMethodName) throws ClassNotFoundException {
        register(Class.forName(className), fromStringMethodName);
    }

    // registers a class
    private void registerJar(String className, String fromStringMethodName) throws ClassNotFoundException {
        register(loadType(className), fromStringMethodName);
    }

    // registers a class using the standard toString/parse pattern
    private void register(Class<?> cls, String fromStringMethodName) throws ClassNotFoundException {
        Method toString = findToStringMethod(cls, "toString");
        Method fromString = findFromStringMethod(cls, fromStringMethodName);
        @SuppressWarnings({"rawtypes", "unchecked"})
        MethodsStringConverter converter = new MethodsStringConverter(cls, toString, fromString, cls);
        if (!converters.containsKey(cls)) {
            converters.put(cls, converter);
        }
    }

    // finds the conversion method
    static Method findToStringMethod(Class<?> cls, String methodName) {
        Method m;
        try {
            m = cls.getMethod(methodName);
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException(ex);
        }
        if (Modifier.isStatic(m.getModifiers())) {
            throw new IllegalArgumentException("Method must not be static: " + methodName);
        }
        return m;
    }

    // finds the conversion method
    static Method findFromStringMethod(Class<?> cls, String methodName) {
        Method m;
        try {
            m = cls.getMethod(methodName, String.class);
        } catch (NoSuchMethodException ex) {
            try {
                m = cls.getMethod(methodName, CharSequence.class);
            } catch (NoSuchMethodException ex2) {
                throw new IllegalArgumentException("Method not found", ex2);
            }
        }
        if (Modifier.isStatic(m.getModifiers()) == false) {
            throw new IllegalArgumentException("Method must be static: " + methodName);
        }
        return m;
    }

    //-----------------------------------------------------------------------
    // loads a type avoiding nulls, context class loader if available
    private static Class<?> loadType(String fullName) throws ClassNotFoundException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return loader != null ? loader.loadClass(fullName) : Class.forName(fullName);
    }

}
