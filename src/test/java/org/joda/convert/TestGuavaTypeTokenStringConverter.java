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

import java.lang.reflect.ParameterizedType;
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
        var token = TypeToken.of(String.class);
        doTest(token, "java.lang.String");
    }

    @Test
    public void test_simpleClass_Integer() {
        var token = TypeToken.of(Integer.class);
        doTest(token, "java.lang.Integer");
    }

    @Test
    public void test_simpleClass_rawList() {
        var token = TypeToken.of(List.class);
        doTest(token, "java.util.List");
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_primitive_int() {
        var token = TypeToken.of(Integer.TYPE);
        doTest(token, "int");
    }

    @Test
    public void test_primitive_char() {
        var token = TypeToken.of(Character.TYPE);
        doTest(token, "char");
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_oneParam() {
        var token = new TypeToken<List<String>>() {};
        doTest(token, "java.util.List<java.lang.String>");
    }

    @Test
    public void test_oneWild() {
        var token = new TypeToken<List<?>>() {};
        doTest(token, "java.util.List<?>");
    }

    @Test
    public void test_oneArray() {
        var token = new TypeToken<List<String[]>>() {};
        // two different output formats to parse
        var test = new TypeTokenStringConverter();
        var asStr = test.convertToString(token);
        var reverse1 = test.convertFromString(TypeToken.class, "java.util.List<java.lang.String[]>");
        var reverse2 = test.convertFromString(TypeToken.class, "java.util.List<[Ljava.lang.String;>");
        assertEquals(reverse1, reverse2);
        var expected = (asStr.equals("java.util.List<java.lang.String[]>") ?
                "java.util.List<java.lang.String[]>" : "java.util.List<[Ljava.lang.String;>");
        doTest(token, expected);
    }

    @Test
    public void test_oneExtends() {
        var token = new TypeToken<List<? extends Number>>() {};
        doTest(token, "java.util.List<? extends java.lang.Number>");
    }

    @Test
    public void test_oneSuper() {
        var token = new TypeToken<List<? super Integer>>() {};
        doTest(token, "java.util.List<? super java.lang.Integer>");
    }

    @Test
    public void test_twoParams() {
        var token = new TypeToken<Map<String, Integer>>() {};
        doTest(token, "java.util.Map<java.lang.String, java.lang.Integer>");
    }

    @Test
    public void test_twoParamsExtends() {
        var token = new TypeToken<Map<? extends CharSequence, ? extends Number>>() {};
        doTest(token, "java.util.Map<? extends java.lang.CharSequence, ? extends java.lang.Number>");
    }

    @Test
    public void test_twoParamsSuper() {
        var token = new TypeToken<Map<? super String, ? super Integer>>() {};
        doTest(token, "java.util.Map<? super java.lang.String, ? super java.lang.Integer>");
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_twoParamNested() {
        var token = new TypeToken<Map<String, List<Integer>>>() {};
        doTest(token, "java.util.Map<java.lang.String, java.util.List<java.lang.Integer>>");
    }

    @Test
    public void test_twoParamNestedExtends() {
        var token = new TypeToken<Map<String, ? extends List<? extends Integer>>>() {};
        doTest(token, "java.util.Map<java.lang.String, ? extends java.util.List<? extends java.lang.Integer>>");
    }

    @Test
    public void test_twoParamComplex() {
        var token = new TypeToken<Map<String, Map<Integer, Double>>>() {};
        doTest(token, "java.util.Map<java.lang.String, java.util.Map<java.lang.Integer, java.lang.Double>>");
    }

    @Test
    public void test_twoParamComplexExtends() {
        var token = new TypeToken<Map<String, Map<? super Integer, ? extends List<? extends Number>>>>() {};
        doTest(token,
                "java.util.Map<java.lang.String, java.util.Map<" +
                "? super java.lang.Integer, ? extends java.util.List<? extends java.lang.Number>>>");
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void doTest(TypeToken<?> obj, String str) {
        var test = new TypeTokenStringConverter();
        assertEquals(TypeToken.class, test.getEffectiveType());
        assertEquals(str, test.convertToString(obj));
        assertEquals(obj, test.convertFromString(TypeToken.class, str));

        var test2 = StringConvert.INSTANCE.findTypedConverterNoGenerics(TypeToken.class);
        assertEquals(TypeToken.class, test2.getEffectiveType());
        assertEquals(str, test2.convertToString(obj));
        assertEquals(obj, test2.convertFromString(TypeToken.class, str));

        var test3 = TypeStringConverterFactory.INSTANCE;
        var converter3 = (TypedStringConverter<Type>) test3.findConverter(Type.class);
        assertEquals(Type.class, converter3.getEffectiveType());
        assertEquals(str, converter3.convertToString(obj.getType()));
        assertEquals(obj.getType(), converter3.convertFromString(Type.class, str));

        var test4 = StringConvert.INSTANCE.findTypedConverterNoGenerics(Type.class);
        assertEquals(Type.class, test4.getEffectiveType());
        assertEquals(str, test4.convertToString(obj.getType()));
        assertEquals(obj.getType(), test4.convertFromString(Type.class, str));

        var test5 = StringConvert.INSTANCE.findTypedConverterNoGenerics(ParameterizedType.class);
        assertEquals(ParameterizedType.class, test5.getEffectiveType());
        assertEquals(str, test5.convertToString(obj.getType()));
        assertEquals(obj.getType(), test5.convertFromString(ParameterizedType.class, str));
    }

}
