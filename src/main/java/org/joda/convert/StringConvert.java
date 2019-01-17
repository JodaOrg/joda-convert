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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.joda.convert.factory.BooleanArrayStringConverterFactory;
import org.joda.convert.factory.BooleanObjectArrayStringConverterFactory;
import org.joda.convert.factory.ByteObjectArrayStringConverterFactory;
import org.joda.convert.factory.CharObjectArrayStringConverterFactory;
import org.joda.convert.factory.NumericArrayStringConverterFactory;
import org.joda.convert.factory.NumericObjectArrayStringConverterFactory;

/**
 * Manager for conversion to and from a {@code String}, acting as the main client interface.
 * <p>
 * Support is provided for conversions based on the {@link StringConverter} interface
 * or the {@link ToString} and {@link FromString} annotations.
 * <p>
 * StringConvert is thread-safe with concurrent caches.
 */
public final class StringConvert {
    // NOTE!
    // There must be no references (direct or indirect) to RenameHandler
    // This class must be loaded after StringConvertInit to avoid horrid loops in class initialization

    /**
     * Errors in class initialization are hard to debug.
     * Set -Dorg.joda.convert.debug=true on the command line to add extra logging to System.err
     * <p>
     * NOTE! This also forces {@link StringConvertInit} to be loaded before calling {@link #create()}
     * which is vital to avoid horrid ordering issues when loading Renamed.ini classes that
     * reference {@code StringConvert}.
     */
    static final boolean LOG = StringConvertInit.LOG;
    /**
     * The cached null object. Ensure this is above public constants.
     */
    private static final TypedStringConverter<?> CACHED_NULL = new TypedStringConverter<Object>() {
        @Override
        public String convertToString(Object object) {
            return null;
        }
        @Override
        public Object convertFromString(Class<? extends Object> cls, String str) {
            return null;
        }
        @Override
        public Class<?> getEffectiveType() {
            return null;
        }
    };
    /**
     * An immutable global instance.
     * <p>
     * This instance cannot be added to using {@link #register}, however annotated classes
     * are picked up. To register your own converters, simply create an instance of this class.
     */
    public static final StringConvert INSTANCE = new StringConvert();

    /**
     * The list of factories.
     */
    private final CopyOnWriteArrayList<StringConverterFactory> factories = new CopyOnWriteArrayList<StringConverterFactory>();
    /**
     * The cache of converters.
     */
    private final ConcurrentMap<Class<?>, TypedStringConverter<?>> registered = new ConcurrentHashMap<Class<?>, TypedStringConverter<?>>();

    //-----------------------------------------------------------------------
    /**
     * Creates a new conversion manager including the extended standard set of converters.
     * <p>
     * The returned converter is a new instance that includes additional converters:
     * <ul>
     * <li>JDK converters
     * <li>{@link NumericArrayStringConverterFactory}
     * <li>{@link NumericObjectArrayStringConverterFactory}
     * <li>{@link CharObjectArrayStringConverterFactory}
     * <li>{@link ByteObjectArrayStringConverterFactory}
     * <li>{@link BooleanArrayStringConverterFactory}
     * <li>{@link BooleanObjectArrayStringConverterFactory}
     * </ul>
     * <p>
     * The convert instance is mutable in a thread-safe manner.
     * Converters may be altered at any time, including the JDK converters.
     * It is strongly recommended to only alter the converters before performing
     * actual conversions.
     * 
     * @return the new converter, not null
     * @since 1.5
     */
    public static StringConvert create() {
        return new StringConvert(true, 
                        NumericArrayStringConverterFactory.INSTANCE,
                        NumericObjectArrayStringConverterFactory.INSTANCE,
                        CharObjectArrayStringConverterFactory.INSTANCE,
                        ByteObjectArrayStringConverterFactory.INSTANCE,
                        BooleanArrayStringConverterFactory.INSTANCE,
                        BooleanObjectArrayStringConverterFactory.INSTANCE);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a new conversion manager including the standard converters.
     * <p>
     * The convert instance is mutable in a thread-safe manner.
     * Converters may be altered at any time, including the standard converters.
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
     * Converters may be altered at any time, including the standard converters.
     * It is strongly recommended to only alter the converters before performing
     * actual conversions.
     * <p>
     * If specified, the factories will be queried in the order specified.
     * 
     * @param includeStandardConverters  true to include the standard converters
     * @param factories  optional array of factories to use, not null
     */
    public StringConvert(boolean includeStandardConverters, StringConverterFactory... factories) {
        if (factories == null) {
            throw new IllegalArgumentException("StringConverterFactory array must not be null");
        }
        for (int i = 0; i < factories.length; i++) {
            if (factories[i] == null) {
                throw new IllegalArgumentException("StringConverterFactory array must not contain a null element");
            }
        }
        if (includeStandardConverters) {
            registered.putAll(StringConvertInit.INSTANCE.converters);
        }
        // factories, adding once to avoid multiple calls to CopyOnWriteArrayList
        List<StringConverterFactory> factoriesToAdd = new ArrayList<StringConverterFactory>();
        if (factories.length > 0) {
            factoriesToAdd.addAll(Arrays.asList(factories));
        }
        factoriesToAdd.add(AnnotationStringConverterFactory.INSTANCE);
        if (includeStandardConverters) {
            factoriesToAdd.addAll(StringConvertInit.INSTANCE.factories);
        }
        this.factories.addAll(factoriesToAdd);
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
     * @since 1.5
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
     * The search algorithm first searches the registered converters.
     * It then searches for {@code ToString} and {@code FromString} annotations on the
     * specified class, class hierarchy or immediate parent interfaces.
     * Finally, it handles {@code Enum} instances.
     * 
     * @param <T>  the type of the converter
     * @param cls  the class to find a converter for, not null
     * @return the converter, not null
     * @throws RuntimeException (or subclass) if no converter found
     */
    public <T> StringConverter<T> findConverter(final Class<T> cls) {
        return findTypedConverter(cls);
    }

    /**
     * Finds a suitable converter for the type with open generics.
     * <p>
     * This returns an instance of {@code StringConverter} for the specified class.
     * This is designed for framework usage where the {@code Class} object generics are unknown'?'.
     * The returned type is declared with {@code Object} instead of '?' to
     * allow the {@link ToStringConverter} to be invoked.
     * <p>
     * The search algorithm first searches the registered converters.
     * It then searches for {@code ToString} and {@code FromString} annotations on the
     * specified class, class hierarchy or immediate parent interfaces.
     * Finally, it handles {@code Enum} instances.
     * 
     * @param cls  the class to find a converter for, not null
     * @return the converter, using {@code Object} to avoid generics, not null
     * @throws RuntimeException (or subclass) if no converter found
     * @since 1.5
     */
    public StringConverter<Object> findConverterNoGenerics(final Class<?> cls) {
        return findTypedConverterNoGenerics(cls);
    }

    /**
     * Finds a suitable converter for the type.
     * <p>
     * This returns an instance of {@code TypedStringConverter} for the specified class.
     * This is designed for user code where the {@code Class} object generics is known.
     * <p>
     * The search algorithm first searches the registered converters.
     * It then searches for {@code ToString} and {@code FromString} annotations on the
     * specified class, class hierarchy or immediate parent interfaces.
     * Finally, it handles {@code Enum} instances.
     * <p>
     * The returned converter may be queried for the effective type of the conversion.
     * This can be used to find the best type to send in a serialized form.
     * <p>
     * NOTE: Changing the method return type of {@link #findConverter(Class)}
     * would be source compatible but not binary compatible. As this is a low-level
     * library, binary compatibility is important, hence the addition of this method.
     * 
     * @param <T>  the type of the converter
     * @param cls  the class to find a converter for, not null
     * @return the converter, not null
     * @throws RuntimeException (or subclass) if no converter found
     * @since 1.7
     */
    public <T> TypedStringConverter<T> findTypedConverter(final Class<T> cls) {
        TypedStringConverter<T> conv = findConverterQuiet(cls);
        if (conv == null) {
            throw new IllegalStateException("No registered converter found: " + cls);
        }
        return conv;
    }

    /**
     * Finds a suitable converter for the type with open generics.
     * <p>
     * This returns an instance of {@code TypedStringConverter} for the specified class.
     * This is designed for framework usage where the {@code Class} object generics are unknown'?'.
     * The returned type is declared with {@code Object} instead of '?' to
     * allow the {@link ToStringConverter} to be invoked.
     * <p>
     * The search algorithm first searches the registered converters.
     * It then searches for {@code ToString} and {@code FromString} annotations on the
     * specified class, class hierarchy or immediate parent interfaces.
     * Finally, it handles {@code Enum} instances.
     * <p>
     * The returned converter may be queried for the effective type of the conversion.
     * This can be used to find the best type to send in a serialized form.
     * <p>
     * NOTE: Changing the method return type of {@link #findConverterNoGenerics(Class)}
     * would be source compatible but not binary compatible. As this is a low-level
     * library, binary compatibility is important, hence the addition of this method.
     * 
     * @param cls  the class to find a converter for, not null
     * @return the converter, using {@code Object} to avoid generics, not null
     * @throws RuntimeException (or subclass) if no converter found
     * @since 1.7
     */
    @SuppressWarnings("unchecked")
    public TypedStringConverter<Object> findTypedConverterNoGenerics(final Class<?> cls) {
        TypedStringConverter<Object> conv = (TypedStringConverter<Object>) findConverterQuiet(cls);
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
    private <T> TypedStringConverter<T> findConverterQuiet(final Class<T> cls) {
        if (cls == null) {
            throw new IllegalArgumentException("Class must not be null");
        }
        TypedStringConverter<T> conv = (TypedStringConverter<T>) registered.get(cls);
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
    private <T> TypedStringConverter<T> findAnyConverter(final Class<T> cls) {
        // check factories
        for (StringConverterFactory factory : factories) {
            StringConverter<T> factoryConv = (StringConverter<T>) factory.findConverter(cls);
            if (factoryConv != null) {
                return TypedAdapter.adapt(cls, factoryConv);
            }
        }
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Registers a converter factory.
     * <p>
     * This will be registered ahead of all existing factories.
     * <p>
     * No new factories may be registered for the global singleton.
     * 
     * @param factory  the converter factory, not null
     * @throws IllegalStateException if trying to alter the global singleton
     * @since 1.5
     */
    public void registerFactory(final StringConverterFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Factory must not be null");
        }
        if (this == INSTANCE) {
            throw new IllegalStateException("Global singleton cannot be extended");
        }
        factories.add(0, factory);
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
        registered.put(cls, TypedAdapter.adapt(cls, converter));
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
        register(cls, new TypedStringConverter<T>() {
            @Override
            public String convertToString(T object) {
                return toString.convertToString(object);
            }
            @Override
            public T convertFromString(Class<? extends T> cls, String str) {
                return fromString.convertFromString(cls, str);
            }
            @Override
            public Class<?> getEffectiveType() {
                return cls;
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
        Method toString = StringConvertInit.findToStringMethod(cls, toStringMethodName);
        Method fromString = StringConvertInit.findFromStringMethod(cls, fromStringMethodName);
        MethodsStringConverter<T> converter = new MethodsStringConverter<T>(cls, toString, fromString, cls);
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
        Method toString = StringConvertInit.findToStringMethod(cls, toStringMethodName);
        Constructor<T> fromString = findFromStringConstructorByType(cls);
        MethodConstructorStringConverter<T> converter = new MethodConstructorStringConverter<T>(cls, toString, fromString);
        registered.putIfAbsent(cls, converter);
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
    // loads a type avoiding nulls, context class loader if available
    static Class<?> loadType(String fullName) throws ClassNotFoundException {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            return loader != null ? loader.loadClass(fullName) : Class.forName(fullName);
        } catch (ClassNotFoundException ex) {
            return loadPrimitiveType(fullName, ex);
        }
    }

    // handle primitive types
    private static Class<?> loadPrimitiveType(String fullName, ClassNotFoundException ex) throws ClassNotFoundException {
        if (fullName.equals("int")) {
            return int.class;
        } else if (fullName.equals("long")) {
            return long.class;
        } else if (fullName.equals("double")) {
            return double.class;
        } else if (fullName.equals("boolean")) {
            return boolean.class;
        } else if (fullName.equals("short")) {
            return short.class;
        } else if (fullName.equals("byte")) {
            return byte.class;
        } else if (fullName.equals("char")) {
            return char.class;
        } else if (fullName.equals("float")) {
            return float.class;
        } else if (fullName.equals("void")) {
            return void.class;
        }
        throw ex;
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
