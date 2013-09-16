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
 * Conversion between an {@code Integer} and a {@code String}.
 */
public enum MockIntegerStringConverter implements StringConverter<Integer> {

    /** Singleton instance. */
    INSTANCE;

    /**
     * Converts the {@code Integer} to a {@code String}.
     * @param object  the object to convert, not null
     * @return the converted string, may be null but generally not
     */
    public String convertToString(Integer object) {
        return object.toString();
    }

    /**
     * Converts the {@code String} to an {@code Integer}.
     * @param cls  the class to convert to, not null
     * @param str  the string to convert, not null
     * @return the converted integer, may be null but generally not
     */
    public Integer convertFromString(Class<? extends Integer> cls, String str) {
        return new Integer(str);
    }

}
