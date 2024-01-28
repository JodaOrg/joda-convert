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

import org.joda.convert.factory.ByteObjectArrayStringConverterFactory;
import org.junit.jupiter.api.Test;

/**
 * Test ByteObjectArrayStringConverterFactory.
 */
class TestByteObjectArrayStringConverterFactory {

    @Test
    void test_ByteArray() {
        doTest(new Byte[0], "");
        doTest(new Byte[] {(byte) 0}, "00");
        doTest(new Byte[] {null}, "--");
        doTest(new Byte[] {(byte) 0, (byte) 1, null, null, (byte) 15, (byte) 16, (byte) 127, (byte) -128, (byte) -1}, "0001----0F107F80FF");
    }

    private void doTest(Byte[] array, String str) {
        var test = new StringConvert(true, ByteObjectArrayStringConverterFactory.INSTANCE);
        assertThat(test.convertToString(array)).isEqualTo(str);
        assertThat(test.convertToString(Byte[].class, array)).isEqualTo(str);
        assertThat(test.convertFromString(Byte[].class, str)).isEqualTo(array);
    }

}
