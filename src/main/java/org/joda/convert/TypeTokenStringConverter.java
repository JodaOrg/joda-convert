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

import com.google.common.reflect.TypeToken;

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
        extends AbstractTypeStringConverter
        implements TypedStringConverter<TypeToken<?>> {

    public TypeTokenStringConverter() {
    }

    @Override
    public String convertToString(TypeToken<?> object) {
        return object.toString();
    }

    @Override
    public TypeToken<?> convertFromString(Class<? extends TypeToken<?>> cls, String str) {
        return TypeToken.of(parse(str));
    }

    @Override
    public Class<?> getEffectiveType() {
        return TypeToken.class;
    }

}
