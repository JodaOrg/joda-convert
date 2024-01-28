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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Test StringConvert factory.
 */
class TestStringConverterFactory {

    @Test
    void test_constructor() {
        var test = new StringConvert(true, new Factory1());
        assertEquals(DistanceMethodMethod.class, test.findTypedConverter(DistanceMethodMethod.class).getEffectiveType());
    }

    @Test
    void test_constructor_null() {
        assertThrows(IllegalArgumentException.class, () -> {
            new StringConvert(true, (StringConverterFactory[]) null);
        });
    }

    @Test
    void test_constructor_nullInArray() {
        assertThrows(IllegalArgumentException.class, () -> {
            new StringConvert(true, new StringConverterFactory[] {null});
        });
    }

    @Test
    void test_registerFactory() {
        var test = new StringConvert();
        test.registerFactory(new Factory1());
        assertEquals(DistanceMethodMethod.class, test.findTypedConverter(DistanceMethodMethod.class).getEffectiveType());
    }

    @Test
    void test_registerFactory_null() {
        assertThrows(IllegalArgumentException.class, () -> {
            var test = new StringConvert();
            test.registerFactory(null);
        });
    }

    @Test
    void test_registerFactory_cannotChangeSingleton() {
        assertThrows(IllegalStateException.class, () -> {
            StringConvert.INSTANCE.registerFactory(new Factory1());
        });
    }

    static class Factory1 implements StringConverterFactory {
        @Override
        public StringConverter<?> findConverter(Class<?> cls) {
            if (cls == DistanceMethodMethod.class) {
                return MockDistanceStringConverter.INSTANCE;
            }
            return null;
        }

    }

}
