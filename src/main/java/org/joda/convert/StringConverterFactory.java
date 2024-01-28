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
 * Factory for {@code StringConverter} that allows converters to be
 * created dynamically or easily initialised.
 * <p>
 * Implementations must be immutable and thread-safe.
 * 
 * @since 1.5
 */
public interface StringConverterFactory {

    /**
     * Finds a converter by type.
     * <p>
     * If the converter is not found, the implementation should return null.
     * This method should only throw an exception if there is a problem that the developer must be made aware of.
     * For example, the annotation-based factory throws an exception if the annotations are used incorrectly.
     * 
     * @param cls  the type to lookup, not null
     * @return the converter, null if not found
     * @throws RuntimeException (or subclass) if there a developer error is found during lookup
     */
    public abstract StringConverter<?> findConverter(Class<?> cls);

}
