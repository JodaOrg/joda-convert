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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.joda.convert.factory.BooleanObjectArrayStringConverterFactory;
import org.junit.Test;

/**
 * Test BooleanObjectArrayStringConverterFactory.
 */
public class TestBooleanObjectArrayStringConverterFactory {

    @Test
    public void test_longArray() {
        doTest(new Boolean[0], "");
        doTest(new Boolean[] {true}, "T");
        doTest(new Boolean[] {false}, "F");
        doTest(new Boolean[] {null}, "-");
        doTest(new Boolean[] {true, true, false, null, true, false, null, null, false}, "TTF-TF--F");
    }

    private void doTest(Boolean[] array, String str) {
        StringConvert test = new StringConvert(true, BooleanObjectArrayStringConverterFactory.INSTANCE);
        assertEquals(str, test.convertToString(array));
        assertEquals(str, test.convertToString(Boolean[].class, array));
        assertTrue(Arrays.equals(array, test.convertFromString(Boolean[].class, str)));
    }

}
