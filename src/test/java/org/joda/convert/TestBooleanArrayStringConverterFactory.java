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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.joda.convert.factory.BooleanArrayStringConverterFactory;
import org.junit.jupiter.api.Test;

/**
 * Test BooleanArrayStringConverterFactory.
 */
class TestBooleanArrayStringConverterFactory {

    @Test
    void test_longArray() {
        doTest(new boolean[0], "");
        doTest(new boolean[] {true}, "T");
        doTest(new boolean[] {false}, "F");
        doTest(new boolean[] {true, true, false, true, false, false}, "TTFTFF");
    }

    private void doTest(boolean[] array, String str) {
        var test = new StringConvert(true, BooleanArrayStringConverterFactory.INSTANCE);
        assertEquals(str, test.convertToString(array));
        assertEquals(str, test.convertToString(boolean[].class, array));
        assertTrue(Arrays.equals(array, test.convertFromString(boolean[].class, str)));
    }

}
