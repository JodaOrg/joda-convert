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
 * Adapts {@code StringConverter} to {@code TypedStringConverter}.
 * 
 * @param <T>  the type of the converter
 * @since 1.7
 */
final class TypedAdapter<T> implements TypedStringConverter<T> {

    private final StringConverter<T> conv;
    private final Class<?> effectiveType;

    static <R> TypedStringConverter<R> adapt(final Class<R> cls, StringConverter<R> converter) {
        if (converter instanceof TypedStringConverter) {
            return (TypedStringConverter<R>) converter;
        } else {
            return new TypedAdapter<R>(converter, cls);
        }
    }

    private TypedAdapter(StringConverter<T> conv, Class<?> effectiveType) {
        this.conv = conv;
        this.effectiveType = effectiveType;
    }

    @Override
    public String convertToString(T object) {
        return conv.convertToString(object);
    }
    @Override
    public T convertFromString(Class<? extends T> cls, String str) {
        return conv.convertFromString(cls, str);
    }
    @Override
    public Class<?> getEffectiveType() {
        return effectiveType;
    }
    @Override
    public String toString() {
        return "TypedAdapter:" + conv.toString();
    }

}
