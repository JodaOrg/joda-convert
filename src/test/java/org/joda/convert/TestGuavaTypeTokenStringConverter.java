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

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.reflect.TypeToken;

/**
 * Test GuavaStringConverters.
 */
@SuppressWarnings("serial")
public class TestGuavaTypeTokenStringConverter {

    @Test
    public void test_simpleClass_String() {
        TypeToken<?> token = TypeToken.of(String.class);
        doTest(token, "java.lang.String");
    }

    @Test
    public void test_simpleClass_Integer() {
        TypeToken<?> token = TypeToken.of(Integer.class);
        doTest(token, "java.lang.Integer");
    }

    @Test
    public void test_simpleClass_rawList() {
        TypeToken<?> token = TypeToken.of(List.class);
        doTest(token, "java.util.List");
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_primitive_int() {
        TypeToken<?> token = TypeToken.of(Integer.TYPE);
        doTest(token, "int");
    }

    @Test
    public void test_primitive_char() {
        TypeToken<?> token = TypeToken.of(Character.TYPE);
        doTest(token, "char");
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_oneParam() {
        TypeToken<?> token = new TypeToken<List<String>>() {};
        doTest(token, "java.util.List<java.lang.String>");
    }

    @Test
    public void test_oneWild() {
        TypeToken<?> token = new TypeToken<List<?>>() {};
        doTest(token, "java.util.List<?>");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void test_oneArray() {
        TypeToken<?> token = new TypeToken<List<String[]>>() {};
        // two different output formats to parse
        TypeTokenStringConverter test = new TypeTokenStringConverter();
        String asStr = test.convertToString(token);
        Object reverse1 = test.convertFromString((Class) TypeToken.class, "java.util.List<java.lang.String[]>");
        Object reverse2 = test.convertFromString((Class) TypeToken.class, "java.util.List<[Ljava.lang.String;>");
        assertEquals(reverse1, reverse2);
        String expected = (asStr.equals("java.util.List<java.lang.String[]>") ?
                "java.util.List<java.lang.String[]>" : "java.util.List<[Ljava.lang.String;>");
        doTest(token, expected);
    }

    @Test
    public void test_oneExtends() {
        TypeToken<?> token = new TypeToken<List<? extends Number>>() {};
        doTest(token, "java.util.List<? extends java.lang.Number>");
    }

    @Test
    public void test_oneSuper() {
        TypeToken<?> token = new TypeToken<List<? super Integer>>() {};
        doTest(token, "java.util.List<? super java.lang.Integer>");
    }

    @Test
    public void test_twoParams() {
        TypeToken<?> token = new TypeToken<Map<String, Integer>>() {};
        doTest(token, "java.util.Map<java.lang.String, java.lang.Integer>");
    }

    @Test
    public void test_twoParamsExtends() {
        TypeToken<?> token = new TypeToken<Map<? extends CharSequence, ? extends Number>>() {};
        doTest(token, "java.util.Map<? extends java.lang.CharSequence, ? extends java.lang.Number>");
    }

    @Test
    public void test_twoParamsSuper() {
        TypeToken<?> token = new TypeToken<Map<? super String, ? super Integer>>() {};
        doTest(token, "java.util.Map<? super java.lang.String, ? super java.lang.Integer>");
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_twoParamNested() {
        TypeToken<?> token = new TypeToken<Map<String, List<Integer>>>() {};
        doTest(token, "java.util.Map<java.lang.String, java.util.List<java.lang.Integer>>");
    }

    @Test
    public void test_twoParamNestedExtends() {
        TypeToken<?> token = new TypeToken<Map<String, ? extends List<? extends Integer>>>() {};
        doTest(token, "java.util.Map<java.lang.String, ? extends java.util.List<? extends java.lang.Integer>>");
    }

    @Test
    public void test_twoParamComplex() {
        TypeToken<?> token = new TypeToken<Map<String, Map<Integer, Double>>>() {};
        doTest(token, "java.util.Map<java.lang.String, java.util.Map<java.lang.Integer, java.lang.Double>>");
    }

    @Test
    public void test_twoParamComplexExtends() {
        TypeToken<?> token = new TypeToken<Map<String, Map<? super Integer, ? extends List<? extends Number>>>>() {};
        doTest(token,
                "java.util.Map<java.lang.String, java.util.Map<" +
                "? super java.lang.Integer, ? extends java.util.List<? extends java.lang.Number>>>");
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void doTest(TypeToken<?> obj, String str) {
        TypeTokenStringConverter test = new TypeTokenStringConverter();
        assertEquals(TypeToken.class, test.getEffectiveType());
        assertEquals(str, test.convertToString(obj));
        assertEquals(obj, test.convertFromString((Class) TypeToken.class, str));

        TypedStringConverter<Object> test2 = StringConvert.INSTANCE.findTypedConverterNoGenerics(TypeToken.class);
        assertEquals(TypeToken.class, test2.getEffectiveType());
        assertEquals(str, test2.convertToString(obj));
        assertEquals(obj, test2.convertFromString(TypeToken.class, str));

        TypeStringConverter test3 = new TypeStringConverter();
        assertEquals(Type.class, test3.getEffectiveType());
        assertEquals(str, test3.convertToString(obj.getType()));
        assertEquals(obj.getType(), test3.convertFromString(Type.class, str));

        TypedStringConverter<Object> test4 = StringConvert.INSTANCE.findTypedConverterNoGenerics(Type.class);
        assertEquals(Type.class, test4.getEffectiveType());
        assertEquals(str, test4.convertToString(obj.getType()));
        assertEquals(obj.getType(), test4.convertFromString(Type.class, str));
    }

}
