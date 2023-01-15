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
 * Interface defining conversion from a {@code String} together with the type.
 * <p>
 * TypedFromStringConverter is an interface and must be implemented with care.
 * Implementations must be immutable and thread-safe.
 * 
 * @param <T>  the type of the converter
 * @since 2.3
 */
interface TypedFromStringConverter<T> extends FromStringConverter<T> {

    /**
     * Gets the effective type that the converter works on.
     * <p>
     * For example, if a class declares the {@code FromString} and  {@code ToString}
     * then the effective type of the converter is that class. If a subclass is
     * queried for a converter, then the effective type is that of the superclass.
     * 
     * @return the effective type
     */
    Class<?> getEffectiveType();

}
