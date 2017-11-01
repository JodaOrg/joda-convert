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
import java.lang.reflect.Type;

/**
 * Parse the string format of Guava TypeToken.
 * <p>
 * This is loaded by reflection only when Guava is on the classpath.
 * It relies on internal methods in Guava that could change in any release.
 * <p>
 * This parser is incomplete, but handles common cases.
 * It does not handle union types or multi-dimensional arrays.
 */
final class TypeTokenStringConverter
        implements TypedStringConverter<Object> {

    static final Class<?> TYPE_TOKEN_CLASS;
    static final Method TYPE_TOKEN_METHOD_OF;
    static {
        try {
            // see StringConvert, which adds the necessary read edge for Java 9
            TYPE_TOKEN_CLASS = Class.forName("com.google.common.reflect.TypeToken");
            TYPE_TOKEN_METHOD_OF = TYPE_TOKEN_CLASS.getDeclaredMethod("of", Type.class);

        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    TypeTokenStringConverter() {
    }

    @Override
    public String convertToString(Object object) {
        return object.toString();
    }

    @Override
    public Object convertFromString(Class<?> cls, String str) {
        Type parsed = TypeUtils.parse(str);
        try {
            return TYPE_TOKEN_METHOD_OF.invoke(null, parsed);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    public Class<?> getEffectiveType() {
        return TYPE_TOKEN_CLASS;
    }

}
