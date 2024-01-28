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

/**
 * Interface defining conversion to and from a {@code String} together with the type.
 * <p>
 * TypedStringConverter is an interface and must be implemented with care.
 * Implementations must be immutable and thread-safe.
 * 
 * @param <T>  the type of the converter
 * @since 1.7
 */
public interface TypedStringConverter<T> extends StringConverter<T>, TypedFromStringConverter<T> {

    /**
     * Gets the effective type that the converter works on.
     * <p>
     * For example, if a class declares the {@code FromString} and {@code ToString}
     * then the effective type of the converter is that class. If a subclass is
     * queried for a converter, then the effective type is that of the superclass.
     * 
     * @return the effective type
     */
    @Override
    public abstract Class<?> getEffectiveType();

    /**
     * Returns this converter with the generics removed.
     * <p>
     * This returns {@code this} cast to remove the generics.
     * This is designed for framework usage where the generics are unknown'?'.
     * The returned type is declared with {@code Object} instead of '?' to allow the {@link ToStringConverter} to be invoked.
     * 
     * @return this converter, with a different type signature
     */
    @SuppressWarnings("unchecked")
    public default TypedStringConverter<Object> withoutGenerics() {
        return (TypedStringConverter<Object>) this;
    }

}
