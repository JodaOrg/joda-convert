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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Manager for conversion to and from a {@code String}, acting as the main client interface.
 * <p>
 * Support is provided for conversions based on the {@link StringConverter} interface
 * or the {@link ToString} and {@link FromString} annotations.
 * <p>
 * StringConvert is thread-safe with concurrent caches.
 */
public final class StringConvert {

    /**
     * An immutable global instance.
     * <p>
     * This instance cannot be added to using {@link #register}, however annotated classes
     * are picked up. To register your own converters, simply create an instance of this class.
     */
    public static final StringConvert INSTANCE = new StringConvert();
    /**
     * The cached null object.
     */
    private static final StringConverter<?> CACHED_NULL = new StringConverter<Object>() {
        @Override
        public String convertToString(Object object) {
            return null;
        }
        @Override
        public Object convertFromString(Class<? extends Object> cls, String str) {
            return null;
        }
    };

    /**
     * The cache of converters.
     */
    private final ConcurrentMap<Class<?>, StringConverter<?>> registered = new ConcurrentHashMap<Class<?>, StringConverter<?>>();

    /**
     * Creates a new conversion manager including the JDK converters.
     * <p>
     * The convert instance is mutable in a thread-safe manner.
     * Converters may be altered at any time, including the JDK converters.
     * It is strongly recommended to only alter the converters before performing
     * actual conversions.
     */
    public StringConvert() {
        this(true);
    }

    /**
     * Creates a new conversion manager.
     * <p>
     * The convert instance is mutable in a thread-safe manner.
     * Converters may be altered at any time, including the JDK converters.
     * It is strongly recommended to only alter the converters before performing
     * actual conversions.
     * 
     * @param includeJdkConverters  true to include the JDK converters
     */
    public StringConvert(boolean includeJdkConverters) {
        if (includeJdkConverters) {
            for (JDKStringConverter conv : JDKStringConverter.values()) {
                registered.put(conv.getType(), conv);
            }
            registered.put(Boolean.TYPE, JDKStringConverter.BOOLEAN);
            registered.put(Byte.TYPE, JDKStringConverter.BYTE);
            registered.put(Short.TYPE, JDKStringConverter.SHORT);
            registered.put(Integer.TYPE, JDKStringConverter.INTEGER);
            registered.put(Long.TYPE, JDKStringConverter.LONG);
            registered.put(Float.TYPE, JDKStringConverter.FLOAT);
            registered.put(Double.TYPE, JDKStringConverter.DOUBLE);
            registered.put(Character.TYPE, JDKStringConverter.CHARACTER);
            // JDK 1.8 classes
            tryRegister("java.time.Instant", "parse");
            tryRegister("java.time.Duration", "parse");
            tryRegister("java.time.LocalDate", "parse");
            tryRegister("java.time.LocalTime", "parse");
            tryRegister("java.time.LocalDateTime", "parse");
            tryRegister("java.time.OffsetTime", "parse");
            tryRegister("java.time.OffsetDateTime", "parse");
            tryRegister("java.time.ZonedDateTime", "parse");
            tryRegister("java.time.Year", "parse");
            tryRegister("java.time.YearMonth", "parse");
            tryRegister("java.time.MonthDay", "parse");
            tryRegister("java.time.Period", "parse");
            tryRegister("java.time.ZoneOffset", "of");
            tryRegister("java.time.ZoneId", "of");
            // ThreeTen backport classes
            tryRegister("org.threeten.bp.Instant", "parse");
            tryRegister("org.threeten.bp.Duration", "parse");
            tryRegister("org.threeten.bp.LocalDate", "parse");
            tryRegister("org.threeten.bp.LocalTime", "parse");
            tryRegister("org.threeten.bp.LocalDateTime", "parse");
            tryRegister("org.threeten.bp.OffsetTime", "parse");
            tryRegister("org.threeten.bp.OffsetDateTime", "parse");
            tryRegister("org.threeten.bp.ZonedDateTime", "parse");
            tryRegister("org.threeten.bp.Year", "parse");
            tryRegister("org.threeten.bp.YearMonth", "parse");
            tryRegister("org.threeten.bp.MonthDay", "parse");
            tryRegister("org.threeten.bp.Period", "parse");
            tryRegister("org.threeten.bp.ZoneOffset", "of");
            tryRegister("org.threeten.bp.ZoneId", "of");
            // Old ThreeTen/JSR-310 classes v0.6.3 and beyond
            tryRegister("javax.time.Instant", "parse");
            tryRegister("javax.time.Duration", "parse");
            tryRegister("javax.time.calendar.LocalDate", "parse");
            tryRegister("javax.time.calendar.LocalTime", "parse");
            tryRegister("javax.time.calendar.LocalDateTime", "parse");
            tryRegister("javax.time.calendar.OffsetDate", "parse");
            tryRegister("javax.time.calendar.OffsetTime", "parse");
            tryRegister("javax.time.calendar.OffsetDateTime", "parse");
            tryRegister("javax.time.calendar.ZonedDateTime", "parse");
            tryRegister("javax.time.calendar.Year", "parse");
            tryRegister("javax.time.calendar.YearMonth", "parse");
            tryRegister("javax.time.calendar.MonthDay", "parse");
            tryRegister("javax.time.calendar.Period", "parse");
            tryRegister("javax.time.calendar.ZoneOffset", "of");
            tryRegister("javax.time.calendar.ZoneId", "of");
            tryRegister("javax.time.calendar.TimeZone", "of");
        }
    }

    /**
     * Tries to register a class using the standard toString/parse pattern.
     * 
     * @param className  the class name, not null
     */
    private void tryRegister(String className, String fromStringMethodName) {
        try {
            Class<?> cls = getClass().getClassLoader().loadClass(className);
            registerMethods(cls, "toString", fromStringMethodName);
        } catch (Exception ex) {
            // ignore
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Converts the specified object to a {@code String}.
     * <p>
     * This uses {@link #findConverter} to provide the converter.
     * 
     * @param object  the object to convert, null returns null
     * @return the converted string, may be null
     * @throws RuntimeException (or subclass) if unable to convert
     */
    public String convertToString(Object object) {
        if (object == null) {
            return null;
        }
        Class<?> cls = object.getClass();
        StringConverter<Object> conv = findConverterNoGenerics(cls);
        return conv.convertToString(object);
    }

    /**
     * Converts the specified object to a {@code String}.
     * <p>
     * This uses {@link #findConverter} to provide the converter.
     * The class can be provided to select a more specific converter.
     * 
     * @param cls  the class to convert from, not null
     * @param object  the object to convert, null returns null
     * @return the converted string, may be null
     * @throws RuntimeException (or subclass) if unable to convert
     */
    public String convertToString(Class<?> cls, Object object) {
        if (object == null) {
            return null;
        }
        StringConverter<Object> conv = findConverterNoGenerics(cls);
        return conv.convertToString(object);
    }

    /**
     * Converts the specified object from a {@code String}.
     * <p>
     * This uses {@link #findConverter} to provide the converter.
     * 
     * @param <T>  the type to convert to
     * @param cls  the class to convert to, not null
     * @param str  the string to convert, null returns null
     * @return the converted object, may be null
     * @throws RuntimeException (or subclass) if unable to convert
     */
    public <T> T convertFromString(Class<T> cls, String str) {
        if (str == null) {
            return null;
        }
        StringConverter<T> conv = findConverter(cls);
        return conv.convertFromString(cls, str);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if a suitable converter exists for the type.
     * <p>
     * This performs the same checks as the {@code findConverter} methods.
     * Calling this before {@code findConverter} will cache the converter.
     * <p>
     * Note that all exceptions, including developer errors are caught and hidden.
     * 
     * @param cls  the class to find a converter for, null returns false
     * @return true if convertible
     */
    public boolean isConvertible(final Class<?> cls) {
        try {
            return cls != null && findConverterQuiet(cls) != null;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    /**
     * Finds a suitable converter for the type.
     * <p>
     * This returns an instance of {@code StringConverter} for the specified class.
     * This is designed for user code where the {@code Class} object generics is known.
     * <p>
     * The search algorithm first searches the registered converters in the
     * class hierarchy and immediate parent interfaces.
     * It then searches for {@code ToString} and {@code FromString} annotations on the
     * specified class, class hierarchy or immediate parent interfaces.
     * 
     * @param <T>  the type of the converter
     * @param cls  the class to find a converter for, not null
     * @return the converter, not null
     * @throws RuntimeException (or subclass) if no converter found
     */
    public <T> StringConverter<T> findConverter(final Class<T> cls) {
        StringConverter<T> conv = findConverterQuiet(cls);
        if (conv == null) {
            throw new IllegalStateException("No registered converter found: " + cls);
        }
        return conv;
    }

    /**
     * Finds a suitable converter for the type with open generics.
     * <p>
     * This returns an instance of {@code StringConverter} for the specified class.
     * This is designed for framework usage where the {@code Class} object generics are unknown'?'.
     * The returned type is declared with {@code Object} instead of '?' to
     * allow the {@link ToStringConverter} to be invoked.
     * <p>
     * The search algorithm first searches the registered converters in the
     * class hierarchy and immediate parent interfaces.
     * It then searches for {@code ToString} and {@code FromString} annotations on the
     * specified class, class hierarchy or immediate parent interfaces.
     * 
     * @param cls  the class to find a converter for, not null
     * @return the converter, using {@code Object} to avoid generics, not null
     * @throws RuntimeException (or subclass) if no converter found
     */
    @SuppressWarnings("unchecked")
    public StringConverter<Object> findConverterNoGenerics(final Class<?> cls) {
        StringConverter<Object> conv = (StringConverter<Object>) findConverterQuiet(cls);
        if (conv == null) {
            throw new IllegalStateException("No registered converter found: " + cls);
        }
        return conv;
    }

    /**
     * Finds a converter searching registered and annotated.
     * 
     * @param <T>  the type of the converter
     * @param cls  the class to find a method for, not null
     * @return the converter, null if no converter
     * @throws RuntimeException if invalid
     */
    @SuppressWarnings("unchecked")
    private <T> StringConverter<T> findConverterQuiet(final Class<T> cls) {
        if (cls == null) {
            throw new IllegalArgumentException("Class must not be null");
        }
        StringConverter<T> conv = (StringConverter<T>) registered.get(cls);
        if (conv == CACHED_NULL) {
            return null;
        }
        if (conv == null) {
            try {
                conv = findAnyConverter(cls);
            } catch (RuntimeException ex) {
                registered.putIfAbsent(cls, CACHED_NULL);
                throw ex;
            }
            if (conv == null) {
                registered.putIfAbsent(cls, CACHED_NULL);
                return null;
            }
            registered.putIfAbsent(cls, conv);
        }
        return conv;
    }

    /**
     * Finds a converter searching registered and annotated.
     * 
     * @param <T>  the type of the converter
     * @param cls  the class to find a method for, not null
     * @return the converter, not null
     * @throws RuntimeException if invalid
     */
    @SuppressWarnings("unchecked")
    private <T> StringConverter<T> findAnyConverter(final Class<T> cls) {
        StringConverter<T> conv = null;
        // check for registered on superclass
        Class<?> loopCls = cls.getSuperclass();
        while (loopCls != null && conv == null) {
            conv = (StringConverter<T>) registered.get(loopCls);
            if (conv != null && conv != CACHED_NULL) {
                return conv;
            }
            loopCls = loopCls.getSuperclass();
        }
        // check for registered on interfaces
        for (Class<?> loopIfc : cls.getInterfaces()) {
            conv = (StringConverter<T>) registered.get(loopIfc);
            if (conv != null && conv != CACHED_NULL) {
                return conv;
            }
        }
        // check for annotations
        conv = findAnnotatedConverter(cls);
        if (conv != null) {
            return conv;
        }
        return null;
    }

    /**
     * Finds a converter searching annotated.
     * 
     * @param <T>  the type of the converter
     * @param cls  the class to find a method for, not null
     * @return the converter, not null
     * @throws RuntimeException if none found
     */
    private <T> StringConverter<T> findAnnotatedConverter(final Class<T> cls) {
        Method toString = findToStringMethod(cls);  // checks superclasses
        if (toString == null) {
            return null;
        }
        Constructor<T> con = findFromStringConstructor(cls);
        Method fromString = findFromStringMethod(cls, con == null);  // optionally checks superclasses
        if (con == null && fromString == null) {
            throw new IllegalStateException("Class annotated with @ToString but not with @FromString: " + cls.getName());
        }
        if (con != null && fromString != null) {
            throw new IllegalStateException("Both method and constructor are annotated with @FromString: " + cls.getName());
        }
        if (con != null) {
            return new MethodConstructorStringConverter<T>(cls, toString, con);
        } else {
            return new MethodsStringConverter<T>(cls, toString, fromString);
        }
    }

    /**
     * Finds the conversion method.
     * 
     * @param cls  the class to find a method for, not null
     * @return the method to call, null means use {@code toString}
     * @throws RuntimeException if invalid
     */
    private Method findToStringMethod(Class<?> cls) {
        Method matched = null;
        // find in superclass hierarchy
        Class<?> loopCls = cls;
        while (loopCls != null && matched == null) {
            Method[] methods = loopCls.getDeclaredMethods();
            for (Method method : methods) {
                ToString toString = method.getAnnotation(ToString.class);
                if (toString != null) {
                    if (matched != null) {
                        throw new IllegalStateException("Two methods are annotated with @ToString: " + cls.getName());
                    }
                    matched = method;
                }
            }
            loopCls = loopCls.getSuperclass();
        }
        // find in immediate parent interfaces
        if (matched == null) {
            for (Class<?> loopIfc : cls.getInterfaces()) {
                Method[] methods = loopIfc.getDeclaredMethods();
                for (Method method : methods) {
                    ToString toString = method.getAnnotation(ToString.class);
                    if (toString != null) {
                        if (matched != null) {
                            throw new IllegalStateException("Two methods are annotated with @ToString on interfaces: " + cls.getName());
                        }
                        matched = method;
                    }
                }
            }
        }
        return matched;
    }

    /**
     * Finds the conversion method.
     * 
     * @param <T>  the type of the converter
     * @param cls  the class to find a method for, not null
     * @return the method to call, null means use {@code toString}
     * @throws RuntimeException if invalid
     */
    private <T> Constructor<T> findFromStringConstructor(Class<T> cls) {
        Constructor<T> con;
        try {
            con = cls.getDeclaredConstructor(String.class);
        } catch (NoSuchMethodException ex) {
            try {
                con = cls.getDeclaredConstructor(CharSequence.class);
            } catch (NoSuchMethodException ex2) {
                return null;
            }
        }
        FromString fromString = con.getAnnotation(FromString.class);
        return fromString != null ? con : null;
    }

    /**
     * Finds the conversion method.
     * 
     * @param cls  the class to find a method for, not null
     * @return the method to call, null means not found
     * @throws RuntimeException if invalid
     */
    private Method findFromStringMethod(Class<?> cls, boolean searchSuperclasses) {
        Method matched = null;
        // find in superclass hierarchy
        Class<?> loopCls = cls;
        while (loopCls != null && matched == null) {
            matched = findFromString(loopCls, matched);
            if (searchSuperclasses == false) {
                break;
            }
            loopCls = loopCls.getSuperclass();
        }
        // find in immediate parent interfaces
        if (searchSuperclasses && matched == null) {
            for (Class<?> loopIfc : cls.getInterfaces()) {
                matched = findFromString(loopIfc, matched);
            }
        }
        return matched;
    }

    /**
     * Finds the conversion method.
     * 
     * @param cls  the class to find a method for, not null
     * @param matched  the matched method, may be null
     * @return the method to call, null means not found
     * @throws RuntimeException if invalid
     */
    private Method findFromString(Class<?> cls, Method matched) {
        // find in declared methods
        Method[] methods = cls.getDeclaredMethods();
        for (Method method : methods) {
            FromString fromString = method.getAnnotation(FromString.class);
            if (fromString != null) {
                if (matched != null) {
                    throw new IllegalStateException("Two methods are annotated with @FromString: " + cls.getName());
                }
                matched = method;
            }
        }
        // check for factory
        FromStringFactory factory = cls.getAnnotation(FromStringFactory.class);
        if (factory != null) {
            if (matched != null) {
                throw new IllegalStateException("Class annotated with @FromString and @FromStringFactory: " + cls.getName());
            }
            Method[] factoryMethods = factory.factory().getDeclaredMethods();
            for (Method method : factoryMethods) {
                // handle factory containing multiple FromString for different types
                if (cls.isAssignableFrom(method.getReturnType())) {
                    FromString fromString = method.getAnnotation(FromString.class);
                    if (fromString != null) {
                        if (matched != null) {
                            throw new IllegalStateException("Two methods are annotated with @FromString on the factory: " + factory.factory().getName());
                        }
                        matched = method;
                    }
                }
            }
        }
        return matched;
    }

    //-----------------------------------------------------------------------
    /**
     * Registers a converter for a specific type.
     * <p>
     * The converter will be used for subclasses unless overidden.
     * <p>
     * No new converters may be registered for the global singleton.
     * 
     * @param <T>  the type of the converter
     * @param cls  the class to register a converter for, not null
     * @param converter  the String converter, not null
     * @throws IllegalArgumentException if the class or converter are null
     * @throws IllegalStateException if trying to alter the global singleton
     */
    public <T> void register(final Class<T> cls, StringConverter<T> converter) {
        if (cls == null) {
            throw new IllegalArgumentException("Class must not be null");
        }
        if (converter == null) {
            throw new IllegalArgumentException("StringConverter must not be null");
        }
        if (this == INSTANCE) {
            throw new IllegalStateException("Global singleton cannot be extended");
        }
        registered.put(cls, converter);
    }

    /**
     * Registers a converter for a specific type using two separate converters.
     * <p>
     * This method registers a converter for the specified class.
     * It is primarily intended for use with JDK 1.8 method references or lambdas:
     * <pre>
     *  sc.register(Distance.class, Distance::toString, Distance::parse);
     * </pre>
     * The converter will be used for subclasses unless overidden.
     * <p>
     * No new converters may be registered for the global singleton.
     * 
     * @param <T>  the type of the converter
     * @param cls  the class to register a converter for, not null
     * @param toString  the to String converter, typically a method reference, not null
     * @param fromString  the from String converter, typically a method reference, not null
     * @throws IllegalArgumentException if the class or converter are null
     * @throws IllegalStateException if trying to alter the global singleton
     * @since 1.3
     */
    public <T> void register(final Class<T> cls, final ToStringConverter<T> toString, final FromStringConverter<T> fromString) {
        if (fromString == null || toString == null) {
            throw new IllegalArgumentException("Converters must not be null");
        }
        register(cls, new StringConverter<T>() {
            public String convertToString(T object) {
                return toString.convertToString(object);
            }
            public T convertFromString(Class<? extends T> cls, String str) {
                return fromString.convertFromString(cls, str);
            }
        });
    }

    /**
     * Registers a converter for a specific type by method names.
     * <p>
     * This method allows the converter to be used when the target class cannot have annotations added.
     * The two method names must obey the same rules as defined by the annotations
     * {@link ToString} and {@link FromString}.
     * The converter will be used for subclasses unless overidden.
     * <p>
     * No new converters may be registered for the global singleton.
     * <p>
     * For example, {@code convert.registerMethods(Distance.class, "toString", "parse");}
     * 
     * @param <T>  the type of the converter
     * @param cls  the class to register a converter for, not null
     * @param toStringMethodName  the name of the method converting to a string, not null
     * @param fromStringMethodName  the name of the method converting from a string, not null
     * @throws IllegalArgumentException if the class or method name are null or invalid
     * @throws IllegalStateException if trying to alter the global singleton
     */
    public <T> void registerMethods(final Class<T> cls, String toStringMethodName, String fromStringMethodName) {
        if (cls == null) {
            throw new IllegalArgumentException("Class must not be null");
        }
        if (toStringMethodName == null || fromStringMethodName == null) {
            throw new IllegalArgumentException("Method names must not be null");
        }
        if (this == INSTANCE) {
            throw new IllegalStateException("Global singleton cannot be extended");
        }
        Method toString = findToStringMethod(cls, toStringMethodName);
        Method fromString = findFromStringMethod(cls, fromStringMethodName);
        MethodsStringConverter<T> converter = new MethodsStringConverter<T>(cls, toString, fromString);
        registered.putIfAbsent(cls, converter);
    }

    /**
     * Registers a converter for a specific type by method and constructor.
     * <p>
     * This method allows the converter to be used when the target class cannot have annotations added.
     * The two method name and constructor must obey the same rules as defined by the annotations
     * {@link ToString} and {@link FromString}.
     * The converter will be used for subclasses unless overidden.
     * <p>
     * No new converters may be registered for the global singleton.
     * <p>
     * For example, {@code convert.registerMethodConstructor(Distance.class, "toString");}
     * 
     * @param <T>  the type of the converter
     * @param cls  the class to register a converter for, not null
     * @param toStringMethodName  the name of the method converting to a string, not null
     * @throws IllegalArgumentException if the class or method name are null or invalid
     * @throws IllegalStateException if trying to alter the global singleton
     */
    public <T> void registerMethodConstructor(final Class<T> cls, String toStringMethodName) {
        if (cls == null) {
            throw new IllegalArgumentException("Class must not be null");
        }
        if (toStringMethodName == null) {
            throw new IllegalArgumentException("Method name must not be null");
        }
        if (this == INSTANCE) {
            throw new IllegalStateException("Global singleton cannot be extended");
        }
        Method toString = findToStringMethod(cls, toStringMethodName);
        Constructor<T> fromString = findFromStringConstructorByType(cls);
        MethodConstructorStringConverter<T> converter = new MethodConstructorStringConverter<T>(cls, toString, fromString);
        registered.putIfAbsent(cls, converter);
    }

    /**
     * Finds the conversion method.
     * 
     * @param cls  the class to find a method for, not null
     * @param methodName  the name of the method to find, not null
     * @return the method to call, null means use {@code toString}
     */
    private Method findToStringMethod(Class<?> cls, String methodName) {
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

    /**
     * Finds the conversion method.
     * 
     * @param cls  the class to find a method for, not null
     * @param methodName  the name of the method to find, not null
     * @return the method to call, null means use {@code toString}
     */
    private Method findFromStringMethod(Class<?> cls, String methodName) {
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

    /**
     * Finds the conversion method.
     * 
     * @param <T>  the type of the converter
     * @param cls  the class to find a method for, not null
     * @return the method to call, null means use {@code toString}
     */
    private <T> Constructor<T> findFromStringConstructorByType(Class<T> cls) {
        try {
            return cls.getDeclaredConstructor(String.class);
        } catch (NoSuchMethodException ex) {
            try {
                return cls.getDeclaredConstructor(CharSequence.class);
            } catch (NoSuchMethodException ex2) {
                throw new IllegalArgumentException("Constructor not found", ex2);
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a simple string representation of the object.
     * 
     * @return the string representation, never null
     */
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
