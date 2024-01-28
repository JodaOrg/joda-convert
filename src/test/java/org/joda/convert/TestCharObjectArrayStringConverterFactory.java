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

import static org.assertj.core.api.Assertions.assertThat;

import org.joda.convert.factory.CharObjectArrayStringConverterFactory;
import org.junit.jupiter.api.Test;

/**
 * Test CharObjectArrayStringConverterFactory.
 */
class TestCharObjectArrayStringConverterFactory {

    @Test
    void test_CharacterArray() {
        doTest(new Character[0], "");
        doTest(new Character[] {'T'}, "T");
        doTest(new Character[] {'-'}, "-");
        doTest(new Character[] {null}, "\\-");
        doTest(new Character[] {'J', '-', 'T'}, "J-T");
        doTest(new Character[] {'\\', '\\', null}, "\\\\\\\\\\-");
        doTest(new Character[] {'-', 'H', 'e', null, null, 'o'}, "-He\\-\\-o");
    }

    private void doTest(Character[] array, String str) {
        var test = new StringConvert(true, CharObjectArrayStringConverterFactory.INSTANCE);
        assertThat(test.convertToString(array)).isEqualTo(str);
        assertThat(test.convertToString(Character[].class, array)).isEqualTo(str);
        assertThat(test.convertFromString(Character[].class, str)).isEqualTo(array);
    }

}
