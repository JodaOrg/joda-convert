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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A general purpose utility for registering renames.
 * <p>
 * This handles type and enum constant renames.
 * For example, use as follows:
 * <pre>
 *  RenameHandler.INSTANCE.renamedType("org.joda.OldName", NewName.class);
 *  RenameHandler.INSTANCE.renamedEnum("CORRECT", Status.VALID);
 *  RenameHandler.INSTANCE.renamedEnum("INCORRECT", Status.INVALID);
 * </pre>
 * The recommended usage is to edit the static singleton before using other classes.
 * Editing a static is acceptable because renames are driven by bytecode which is static.
 * For additional security, an application should lock the rename handler instance
 * once any types and enums have been registered using {@link #lock()}.
 * <p>
 * This class is thread-safe with concurrent caches.
 * 
 * @since 1.6
 */
public final class RenameHandler {

    /**
     * A mutable global instance.
     * This is a singleton instance which is mutated.
     */
    public static final RenameHandler INSTANCE = new RenameHandler();

    /**
     * The lock flag.
     */
    private volatile boolean locked;
    /**
     * The type renames.
     */
    private final ConcurrentHashMap<String, Class<?>> typeRenames =
                    new ConcurrentHashMap<String, Class<?>>(16, 0.75f, 2);
    /**
     * The enum renames.
     */
    private final ConcurrentHashMap<Class<?>, Map<String, Enum<?>>> enumRenames =
                    new ConcurrentHashMap<Class<?>, Map<String, Enum<?>>>(16, 0.75f, 2);

    //-----------------------------------------------------------------------
    /**
     * Creates an instance.
     * <p>
     * This is not normally used as the preferred option is to edit the singleton.
     * 
     * @return a new instance, not null
     */
    public static RenameHandler create() {
        return new RenameHandler();
    }

    //-----------------------------------------------------------------------
    /**
     * Restricted constructor.
     */
    private RenameHandler() {
    }

    //-----------------------------------------------------------------------
    /**
     * Register the fact that a type was renamed.
     * <p>
     * This handles the use case where a class is renamed.
     * 
     * @param oldName  the old name of the type including the package name, not null
     * @param currentValue  the current type, not null
     */
    public void renamedType(String oldName, Class<?> currentValue) {
        if (oldName == null) {
            throw new IllegalArgumentException("oldName must not be null");
        }
        if (currentValue == null) {
            throw new IllegalArgumentException("currentValue must not be null");
        }
        if (oldName.startsWith("java.") || oldName.startsWith("javax.") || oldName.startsWith("org.joda.")) {
            throw new IllegalArgumentException("oldName must not be a java.*, javax.* or org.joda.* type");
        }
        checkNotLocked();
        typeRenames.put(oldName, currentValue);
    }

    /**
     * Gets the map of renamed types.
     * <p>
     * An empty map is returned if there are no renames.
     * 
     * @return a copy of the set of enum types with renames, not null
     */
    public Map<String, Class<?>> getTypeRenames() {
        return new HashMap<String, Class<?>>(typeRenames);
    }

    /**
     * Lookup a type from a name, handling renames.
     * 
     * @param name  the name of the type to lookup, not null
     * @return the type, not null
     * @throws ClassNotFoundException if the name is not a valid type
     */
    public Class<?> lookupType(String name) throws ClassNotFoundException {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        Class<?> type = typeRenames.get(name);
        if (type == null) {
            type = loadType(name);
        }
        return type;
    }

    /**
     * Loads a type avoiding nulls
     * 
     * @param fullName  the full class name
     * @return the loaded class
     * @throws ClassNotFoundException if the class is not found
     */
    Class<?> loadType(String fullName) throws ClassNotFoundException {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            return loader != null ? loader.loadClass(fullName) : Class.forName(fullName);
        } catch (ClassNotFoundException ex) {
            return loadPrimitiveType(fullName, ex);
        }
    }

    // handle primitive types
    private Class<?> loadPrimitiveType(String fullName, ClassNotFoundException ex) throws ClassNotFoundException {
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
     * Register the fact that an enum constant was renamed.
     * <p>
     * This handles the use case where an enum constant is renamed, but the enum class remains the same.
     * 
     * @param oldName  the old name of the enum constant, not null
     * @param currentValue  the current enum constant, not null
     */
    public void renamedEnum(String oldName, Enum<?> currentValue) {
        if (oldName == null) {
            throw new IllegalArgumentException("oldName must not be null");
        }
        if (currentValue == null) {
            throw new IllegalArgumentException("currentValue must not be null");
        }
        checkNotLocked();
        Class<?> enumType = currentValue.getDeclaringClass();
        Map<String, Enum<?>> perClass = enumRenames.get(enumType);
        if (perClass == null) {
            enumRenames.putIfAbsent(enumType, new ConcurrentHashMap<String, Enum<?>>(16, 0.75f, 2));
            perClass = enumRenames.get(enumType);
        }
        perClass.put(oldName, currentValue);
    }

    /**
     * Gets the set of enum types that have renames.
     * <p>
     * An empty set is returned if there are no renames.
     * 
     * @return a copy of the set of enum types with renames, not null
     */
    public Set<Class<?>> getEnumTypesWithRenames() {
        return new HashSet<Class<?>>(enumRenames.keySet());
    }

    /**
     * Gets the map of renamed for an enum type.
     * <p>
     * An empty map is returned if there are no renames.
     * 
     * @param type  the enum type, not null
     * @return a copy of the set of enum renames, not null
     */
    public Map<String, Enum<?>> getEnumRenames(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        Map<String, Enum<?>> map = enumRenames.get(type);
        if (map == null) {
            return new HashMap<String, Enum<?>>();
        }
        return new HashMap<String, Enum<?>>(map);
    }

    /**
     * Lookup an enum from a name, handling renames.
     * 
     * @param <T>  the type of the desired enum
     * @param type  the enum type, not null
     * @param name  the name of the enum to lookup, not null
     * @return the enum value, not null
     * @throws IllegalArgumentException if the name is not a valid enum constant
     */
    public <T extends Enum<T>> T lookupEnum(Class<T> type, String name) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        Map<String, Enum<?>> map = getEnumRenames(type);
        Enum<?> value = map.get(name);
        if (value != null) {
            return type.cast(value);
        }
        return Enum.valueOf(type, name);
    }

    //-----------------------------------------------------------------------
    /**
     * Locks this instance of the rename handler.
     * <p>
     * For additional security, an application should lock the rename handler
     * once any types and enums have been registered.
     */
    public void lock() {
        checkNotLocked();
        locked = true;
    }

    // ensure not locked
    private void checkNotLocked() {
        if (locked) {
            throw new IllegalStateException("RenameHandler has been locked and it cannot now be changed");
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
        return "RenamedTypes" + typeRenames + ",RenamedEnumConstants" + enumRenames;
    }

}
