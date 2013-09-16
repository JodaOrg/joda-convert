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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.math.RoundingMode;
import java.text.ParseException;

import org.joda.convert.test1.Test1Class;
import org.joda.convert.test2.Test2Class;
import org.joda.convert.test2.Test2Interface;
import org.joda.convert.test3.Test3Class;
import org.joda.convert.test4.Test4Class;
import org.joda.convert.test4.Test4Interface;
import org.junit.Test;

/**
 * Test StringConvert.
 */
public class TestStringConvert {

    @Test
    public void test_constructor() {
        StringConvert test = new StringConvert();
        StringConverter<?> conv = test.findConverter(Integer.class);
        assertEquals(true, conv instanceof JDKStringConverter);
    }

    @Test
    public void test_constructor_true() {
        StringConvert test = new StringConvert(true);
        StringConverter<?> conv = test.findConverter(Integer.class);
        assertEquals(true, conv instanceof JDKStringConverter);
    }

    @Test(expected=IllegalStateException.class)
    public void test_constructor_false() {
        StringConvert test = new StringConvert(false);
        StringConverter<?> conv = test.findConverter(Integer.class);
        assertEquals(null, conv);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_isConvertible() {
        assertTrue(StringConvert.INSTANCE.isConvertible(Integer.class));
        assertTrue(StringConvert.INSTANCE.isConvertible(String.class));
        assertFalse(StringConvert.INSTANCE.isConvertible(Object.class));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_convertToString() {
        Integer i = 6;
        assertEquals("6", StringConvert.INSTANCE.convertToString(i));
    }

    @Test
    public void test_convertToString_primitive() {
        int i = 6;
        assertEquals("6", StringConvert.INSTANCE.convertToString(i));
    }

    @Test
    public void test_convertToString_inherit() {
        assertEquals("CEILING", StringConvert.INSTANCE.convertToString(RoundingMode.CEILING));
    }

    @Test
    public void test_convertToString_null() {
        assertEquals(null, StringConvert.INSTANCE.convertToString(null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_convertToString_withType() {
        Integer i = 6;
        assertEquals("6", StringConvert.INSTANCE.convertToString(Integer.class, i));
    }

    @Test
    public void test_convertToString_withType_noGenerics() {
        Integer i = 6;
        Class<?> cls = Integer.class;
        assertEquals("6", StringConvert.INSTANCE.convertToString(cls, i));
    }

    @Test
    public void test_convertToString_withType_primitive1() {
        int i = 6;
        assertEquals("6", StringConvert.INSTANCE.convertToString(Integer.class, i));
    }

    @Test
    public void test_convertToString_withType_primitive2() {
        int i = 6;
        assertEquals("6", StringConvert.INSTANCE.convertToString(Integer.TYPE, i));
    }

    @Test
    public void test_convertToString_withType_inherit1() {
        assertEquals("CEILING", StringConvert.INSTANCE.convertToString(RoundingMode.class, RoundingMode.CEILING));
    }

    @Test
    public void test_convertToString_withType_inherit2() {
        assertEquals("CEILING", StringConvert.INSTANCE.convertToString(Enum.class, RoundingMode.CEILING));
    }

    @Test
    public void test_convertToString_withType_null() {
        assertEquals(null, StringConvert.INSTANCE.convertToString(Integer.class, null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_convertToString_withType_nullClass() {
        assertEquals(null, StringConvert.INSTANCE.convertToString(null, "6"));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_convertFromString() {
        assertEquals(Integer.valueOf(6), StringConvert.INSTANCE.convertFromString(Integer.class, "6"));
    }

    @Test
    public void test_convertFromString_primitiveInt() {
      assertEquals(Integer.valueOf(6), StringConvert.INSTANCE.convertFromString(Integer.TYPE, "6"));
    }

    @Test
    public void test_convertFromString_primitiveBoolean() {
      assertEquals(Boolean.TRUE, StringConvert.INSTANCE.convertFromString(Boolean.TYPE, "true"));
    }

    @Test
    public void test_convertFromString_inherit() {
        assertEquals(RoundingMode.CEILING, StringConvert.INSTANCE.convertFromString(RoundingMode.class, "CEILING"));
    }

    @Test
    public void test_convertFromString_null() {
        assertEquals(null, StringConvert.INSTANCE.convertFromString(Integer.class, null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_convertFromString_nullClass() {
        assertEquals(null, StringConvert.INSTANCE.convertFromString(null, "6"));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_findConverter() {
        Class<Integer> cls = Integer.class;
        StringConverter<Integer> conv = StringConvert.INSTANCE.findConverter(cls);
        assertEquals(Integer.valueOf(12), conv.convertFromString(cls, "12"));
        assertEquals("12", conv.convertToString(12));
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_findConverter_null() {
        StringConvert.INSTANCE.findConverter(null);
    }

    @Test(expected=IllegalStateException.class)
    public void test_findConverter_Object() {
        StringConvert.INSTANCE.findConverter(Object.class);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_findConverterNoGenerics() {
        Class<?> cls = Integer.class;
        StringConverter<Object> conv = StringConvert.INSTANCE.findConverterNoGenerics(cls);
        assertEquals(Integer.valueOf(12), conv.convertFromString(cls, "12"));
        assertEquals("12", conv.convertToString(12));
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_findConverterNoGenerics_null() {
        StringConvert.INSTANCE.findConverterNoGenerics(null);
    }

    @Test(expected=IllegalStateException.class)
    public void test_findConverterNoGenerics_Object() {
        StringConvert.INSTANCE.findConverterNoGenerics(Object.class);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_convert_annotationMethodMethod() {
        StringConvert test = new StringConvert();
        DistanceMethodMethod d = new DistanceMethodMethod(25);
        assertEquals("25m", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(DistanceMethodMethod.class, "25m").amount);
        StringConverter<DistanceMethodMethod> conv = test.findConverter(DistanceMethodMethod.class);
        assertEquals(true, conv instanceof MethodsStringConverter<?>);
        assertSame(conv, test.findConverter(DistanceMethodMethod.class));
        assertEquals(true, conv.toString().startsWith("RefectionStringConverter"));
    }

    @Test
    public void test_convert_annotationMethodMethodCharSequence() {
        StringConvert test = new StringConvert();
        DistanceMethodMethodCharSequence d = new DistanceMethodMethodCharSequence(25);
        assertEquals("25m", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(DistanceMethodMethodCharSequence.class, "25m").amount);
        StringConverter<DistanceMethodMethodCharSequence> conv = test.findConverter(DistanceMethodMethodCharSequence.class);
        assertEquals(true, conv instanceof MethodsStringConverter<?>);
        assertSame(conv, test.findConverter(DistanceMethodMethodCharSequence.class));
        assertEquals(true, conv.toString().startsWith("RefectionStringConverter"));
    }

    @Test
    public void test_convert_annotationMethodConstructor() {
        StringConvert test = new StringConvert();
        DistanceMethodConstructor d = new DistanceMethodConstructor(25);
        assertEquals("25m", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(DistanceMethodConstructor.class, "25m").amount);
        StringConverter<DistanceMethodConstructor> conv = test.findConverter(DistanceMethodConstructor.class);
        assertEquals(true, conv instanceof MethodConstructorStringConverter<?>);
        assertSame(conv, test.findConverter(DistanceMethodConstructor.class));
        assertEquals(true, conv.toString().startsWith("RefectionStringConverter"));
    }

    @Test
    public void test_convert_annotationMethodConstructorCharSequence() {
        StringConvert test = new StringConvert();
        DistanceMethodConstructorCharSequence d = new DistanceMethodConstructorCharSequence(25);
        assertEquals("25m", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(DistanceMethodConstructorCharSequence.class, "25m").amount);
        StringConverter<DistanceMethodConstructorCharSequence> conv = test.findConverter(DistanceMethodConstructorCharSequence.class);
        assertEquals(true, conv instanceof MethodConstructorStringConverter<?>);
        assertSame(conv, test.findConverter(DistanceMethodConstructorCharSequence.class));
        assertEquals(true, conv.toString().startsWith("RefectionStringConverter"));
    }

    @Test
    public void test_convert_annotationSubMethodMethod() {
        StringConvert test = new StringConvert();
        SubMethodMethod d = new SubMethodMethod(25);
        assertEquals("25m", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(SubMethodMethod.class, "25m").amount);
        StringConverter<SubMethodMethod> conv = test.findConverter(SubMethodMethod.class);
        assertEquals(true, conv instanceof MethodsStringConverter<?>);
        assertSame(conv, test.findConverter(SubMethodMethod.class));
    }

    @Test
    public void test_convert_annotationSubMethodConstructor() {
        StringConvert test = new StringConvert();
        SubMethodConstructor d = new SubMethodConstructor("25m");
        assertEquals("25m", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(SubMethodConstructor.class, "25m").amount);
        StringConverter<SubMethodConstructor> conv = test.findConverter(SubMethodConstructor.class);
        assertEquals(true, conv instanceof MethodConstructorStringConverter<?>);
        assertSame(conv, test.findConverter(SubMethodConstructor.class));
    }

    @Test
    public void test_convert_annotationSuperFactorySuper() {
        StringConvert test = new StringConvert();
        SuperFactorySuper d = new SuperFactorySuper(25);
        assertEquals("25m", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(SuperFactorySuper.class, "25m").amount);
        StringConverter<SuperFactorySuper> conv = test.findConverter(SuperFactorySuper.class);
        assertEquals(true, conv instanceof MethodsStringConverter<?>);
        assertSame(conv, test.findConverter(SuperFactorySuper.class));
    }

    @Test
    public void test_convert_annotationSuperFactorySubViaSuper() {
        StringConvert test = new StringConvert();
        SuperFactorySub d = new SuperFactorySub(8);
        assertEquals("8m", test.convertToString(d));
        SuperFactorySuper fromStr = test.convertFromString(SuperFactorySuper.class, "8m");
        assertEquals(d.amount, fromStr.amount);
        assertEquals(true, fromStr instanceof SuperFactorySub);
        StringConverter<SuperFactorySuper> conv = test.findConverter(SuperFactorySuper.class);
        assertEquals(true, conv instanceof MethodsStringConverter<?>);
        assertSame(conv, test.findConverter(SuperFactorySuper.class));
    }

    @Test
    public void test_convert_annotationSuperFactorySubViaSub1() {
        StringConvert test = new StringConvert();
        SuperFactorySub d = new SuperFactorySub(25);
        assertEquals("25m", test.convertToString(d));
    }

    // TODO problem is fwks, that just request a converter based on the type of the object
    @Test(expected = ClassCastException.class)
    public void test_convert_annotationSuperFactorySubViaSub2() {
        StringConvert test = new StringConvert();
        test.convertFromString(SuperFactorySub.class, "25m");
    }

    @Test
    public void test_convert_annotationToStringInvokeException() {
        StringConvert test = new StringConvert();
        DistanceToStringException d = new DistanceToStringException(25);
        StringConverter<DistanceToStringException> conv = test.findConverter(DistanceToStringException.class);
        try {
            conv.convertToString(d);
            fail();
        } catch (RuntimeException ex) {
            assertEquals(ParseException.class, ex.getCause().getClass());
        }
    }

    @Test
    public void test_convert_annotationFromStringInvokeException() {
        StringConvert test = new StringConvert();
        StringConverter<DistanceFromStringException> conv = test.findConverter(DistanceFromStringException.class);
        try {
            conv.convertFromString(DistanceFromStringException.class, "25m");
            fail();
        } catch (RuntimeException ex) {
            assertEquals(ParseException.class, ex.getCause().getClass());
        }
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_convert_annotationFactoryMethod() {
        StringConvert test = new StringConvert();
        DistanceWithFactory d = new DistanceWithFactory(25);
        assertEquals("25m", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(DistanceWithFactory.class, "25m").amount);
        StringConverter<DistanceWithFactory> conv = test.findConverter(DistanceWithFactory.class);
        assertEquals(true, conv instanceof MethodsStringConverter<?>);
        assertSame(conv, test.findConverter(DistanceWithFactory.class));
        assertEquals(true, conv.toString().startsWith("RefectionStringConverter"));
    }

    @Test
    public void test_convert_annotation_ToStringOnInterface() {
        StringConvert test = new StringConvert();
        Test1Class d = new Test1Class(25);
        assertEquals("25g", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(Test1Class.class, "25g").amount);
        StringConverter<Test1Class> conv = test.findConverter(Test1Class.class);
        assertEquals(true, conv instanceof MethodsStringConverter<?>);
        assertSame(conv, test.findConverter(Test1Class.class));
        assertEquals(true, conv.toString().startsWith("RefectionStringConverter"));
    }

    @Test
    public void test_convert_annotation_FactoryAndToStringOnInterface() {
        StringConvert test = new StringConvert();
        Test2Class d = new Test2Class(25);
        assertEquals("25g", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(Test2Class.class, "25g").amount);
        StringConverter<Test2Class> conv = test.findConverter(Test2Class.class);
        assertEquals(true, conv instanceof MethodsStringConverter<?>);
        assertSame(conv, test.findConverter(Test2Class.class));
        assertEquals(true, conv.toString().startsWith("RefectionStringConverter"));
    }

    @Test
    public void test_convert_annotation_FactoryAndToStringOnInterface_usingInterface() {
        StringConvert test = new StringConvert();
        Test2Class d = new Test2Class(25);
        assertEquals("25g", test.convertToString(d));
        assertEquals("25g", test.convertFromString(Test2Interface.class, "25g").print());
        StringConverter<Test2Interface> conv = test.findConverter(Test2Interface.class);
        assertEquals(true, conv instanceof MethodsStringConverter<?>);
        assertSame(conv, test.findConverter(Test2Interface.class));
        assertEquals(true, conv.toString().startsWith("RefectionStringConverter"));
    }

    @Test
    public void test_convert_annotation_ToStringFromStringOnSuperClassBeatsInterface() {
        StringConvert test = new StringConvert();
        Test3Class d = new Test3Class(25);
        assertEquals("25g", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(Test3Class.class, "25g").amount);
        StringConverter<Test3Class> conv = test.findConverter(Test3Class.class);
        assertEquals(true, conv instanceof MethodsStringConverter<?>);
        assertSame(conv, test.findConverter(Test3Class.class));
        assertEquals(true, conv.toString().startsWith("RefectionStringConverter"));
    }

    @Test(expected=IllegalStateException.class)
    public void test_convert_annotation_FromStringFactoryClashingMethods_fromClass() {
        StringConvert test = new StringConvert();
        test.findConverter(Test4Class.class);
    }

    @Test(expected=IllegalStateException.class)
    public void test_convert_annotation_FromStringFactoryClashingMethods_fromInterface() {
        StringConvert test = new StringConvert();
        test.findConverter(Test4Interface.class);
    }

    //-----------------------------------------------------------------------
    @Test(expected=IllegalStateException.class)
    public void test_convert_annotationNoMethods() {
        StringConvert test = new StringConvert();
        test.findConverter(DistanceNoAnnotations.class);
    }

    @Test(expected=IllegalStateException.class)
    public void test_convert_annotatedMethodAndConstructor() {
        StringConvert test = new StringConvert();
        test.findConverter(DistanceMethodAndConstructorAnnotations.class);
    }

    @Test(expected=IllegalStateException.class)
    public void test_convert_annotatedTwoToString() {
        StringConvert test = new StringConvert();
        test.findConverter(DistanceTwoToStringAnnotations.class);
    }

    @Test(expected=IllegalStateException.class)
    public void test_convert_annotatedToStringInvalidReturnType() {
        StringConvert test = new StringConvert();
        test.findConverter(DistanceToStringInvalidReturnType.class);
    }

    @Test(expected=IllegalStateException.class)
    public void test_convert_annotatedToStringInvalidParameters() {
        StringConvert test = new StringConvert();
        test.findConverter(DistanceToStringInvalidParameters.class);
    }

    @Test(expected=IllegalStateException.class)
    public void test_convert_annotatedFromStringInvalidReturnType() {
        StringConvert test = new StringConvert();
        test.findConverter(DistanceFromStringInvalidReturnType.class);
    }

    @Test(expected=IllegalStateException.class)
    public void test_convert_annotatedFromStringInvalidParameter() {
        StringConvert test = new StringConvert();
        test.findConverter(DistanceFromStringInvalidParameter.class);
    }

    @Test(expected=IllegalStateException.class)
    public void test_convert_annotatedFromStringInvalidParameterCount() {
        StringConvert test = new StringConvert();
        test.findConverter(DistanceFromStringInvalidParameterCount.class);
    }

    @Test(expected=IllegalStateException.class)
    public void test_convert_annotatedFromStringConstructorInvalidParameter() {
        StringConvert test = new StringConvert();
        test.findConverter(DistanceFromStringConstructorInvalidParameter.class);
    }

    @Test(expected=IllegalStateException.class)
    public void test_convert_annotatedFromStringConstructorInvalidParameterCount() {
        StringConvert test = new StringConvert();
        test.findConverter(DistanceFromStringConstructorInvalidParameterCount.class);
    }

    @Test(expected=IllegalStateException.class)
    public void test_convert_annotatedToStringNoFromString() {
        StringConvert test = new StringConvert();
        test.findConverter(DistanceToStringNoFromString.class);
    }

    @Test(expected=IllegalStateException.class)
    public void test_convert_annotatedFromStringNoToString() {
        StringConvert test = new StringConvert();
        test.findConverter(DistanceFromStringNoToString.class);
    }

    @Test(expected=IllegalStateException.class)
    public void test_convert_annotatedTwoFromStringMethod() {
        StringConvert test = new StringConvert();
        test.findConverter(DistanceTwoFromStringMethodAnnotations.class);
    }

    //-----------------------------------------------------------------------
    @Test(expected=IllegalArgumentException.class)
    public void test_register_classNotNull() {
        StringConvert.INSTANCE.register(null, MockIntegerStringConverter.INSTANCE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_register_converterNotNull() {
        StringConvert.INSTANCE.register(Integer.class, null);
    }

    @Test(expected=IllegalStateException.class)
    public void test_register_notOnShared() {
        StringConvert.INSTANCE.register(Integer.class, MockIntegerStringConverter.INSTANCE);
    }

    @Test
    public void test_register_classAlreadyRegistered() {
        new StringConvert().register(Integer.class, MockIntegerStringConverter.INSTANCE);
    }

    public void test_register_distance() {
        StringConvert test = new StringConvert();
        test.register(DistanceMethodMethod.class, MockDistanceStringConverter.INSTANCE);
        assertSame(MockDistanceStringConverter.INSTANCE, test.findConverter(DistanceMethodMethod.class));
    }

    //-------------------------------------------------------------------------
    ToStringConverter<DistanceNoAnnotations> DISTANCE_TO_STRING_CONVERTER = new ToStringConverter<DistanceNoAnnotations>() {
        public String convertToString(DistanceNoAnnotations object) {
            return object.toString();
        }
    };
    FromStringConverter<DistanceNoAnnotations> DISTANCE_FROM_STRING_CONVERTER = new FromStringConverter<DistanceNoAnnotations>() {
        public DistanceNoAnnotations convertFromString(Class<? extends DistanceNoAnnotations> cls, String str) {
            return DistanceNoAnnotations.parse(str);
        }
    };

    @Test
    public void test_register_FunctionalInterfaces() {
        StringConvert test = new StringConvert();
        test.register(DistanceNoAnnotations.class, DISTANCE_TO_STRING_CONVERTER, DISTANCE_FROM_STRING_CONVERTER);
        DistanceNoAnnotations d = new DistanceNoAnnotations(25);
        assertEquals("Distance[25m]", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(DistanceNoAnnotations.class, "25m").amount);
        StringConverter<DistanceNoAnnotations> conv = test.findConverter(DistanceNoAnnotations.class);
        assertEquals(true, conv.getClass().getName().contains("$"));
        assertSame(conv, test.findConverter(DistanceNoAnnotations.class));
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_register_FunctionalInterfaces_nullClass() {
        StringConvert test = new StringConvert();
        test.register(null, DISTANCE_TO_STRING_CONVERTER, DISTANCE_FROM_STRING_CONVERTER);
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_register_FunctionalInterfaces_nullToString() {
        StringConvert test = new StringConvert();
        test.register(DistanceNoAnnotations.class, null, DISTANCE_FROM_STRING_CONVERTER);
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_register_FunctionalInterfaces_nullFromString() {
        StringConvert test = new StringConvert();
        test.register(DistanceNoAnnotations.class, DISTANCE_TO_STRING_CONVERTER, null);
    }

    //-------------------------------------------------------------------------
    @Test
    public void test_registerMethods() {
        StringConvert test = new StringConvert();
        test.registerMethods(DistanceNoAnnotations.class, "toString", "parse");
        DistanceNoAnnotations d = new DistanceNoAnnotations(25);
        assertEquals("Distance[25m]", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(DistanceNoAnnotations.class, "25m").amount);
        StringConverter<DistanceNoAnnotations> conv = test.findConverter(DistanceNoAnnotations.class);
        assertEquals(true, conv instanceof MethodsStringConverter<?>);
        assertSame(conv, test.findConverter(DistanceNoAnnotations.class));
    }

    @Test
    public void test_registerMethodsCharSequence() {
        StringConvert test = new StringConvert();
        test.registerMethods(DistanceNoAnnotationsCharSequence.class, "toString", "parse");
        DistanceNoAnnotationsCharSequence d = new DistanceNoAnnotationsCharSequence(25);
        assertEquals("Distance[25m]", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(DistanceNoAnnotationsCharSequence.class, "25m").amount);
        StringConverter<DistanceNoAnnotationsCharSequence> conv = test.findConverter(DistanceNoAnnotationsCharSequence.class);
        assertEquals(true, conv instanceof MethodsStringConverter<?>);
        assertSame(conv, test.findConverter(DistanceNoAnnotationsCharSequence.class));
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_registerMethods_nullClass() {
        StringConvert test = new StringConvert();
        test.registerMethods(null, "toString", "parse");
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_registerMethods_nullToString() {
        StringConvert test = new StringConvert();
        test.registerMethods(DistanceNoAnnotations.class, null, "parse");
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_registerMethods_nullFromString() {
        StringConvert test = new StringConvert();
        test.registerMethods(DistanceNoAnnotations.class, "toString", null);
    }

    @Test
    public void test_registerMethods_classAlreadyRegistered() {
      StringConvert test = new StringConvert();
      test.registerMethods(DistanceNoAnnotations.class, "toString", "parse");
      test.registerMethods(DistanceNoAnnotations.class, "toString", "parse");
    }

    //-------------------------------------------------------------------------
    @Test
    public void test_registerMethodConstructorCharSequence() {
        StringConvert test = new StringConvert();
        test.registerMethodConstructor(DistanceNoAnnotationsCharSequence.class, "toString");
        DistanceNoAnnotationsCharSequence d = new DistanceNoAnnotationsCharSequence(25);
        assertEquals("Distance[25m]", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(DistanceNoAnnotationsCharSequence.class, "25m").amount);
        StringConverter<DistanceNoAnnotationsCharSequence> conv = test.findConverter(DistanceNoAnnotationsCharSequence.class);
        assertEquals(true, conv instanceof MethodConstructorStringConverter<?>);
        assertSame(conv, test.findConverter(DistanceNoAnnotationsCharSequence.class));
    }

    @Test
    public void test_registerMethodConstructor() {
        StringConvert test = new StringConvert();
        test.registerMethodConstructor(DistanceNoAnnotations.class, "toString");
        DistanceNoAnnotations d = new DistanceNoAnnotations(25);
        assertEquals("Distance[25m]", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(DistanceNoAnnotations.class, "25m").amount);
        StringConverter<DistanceNoAnnotations> conv = test.findConverter(DistanceNoAnnotations.class);
        assertEquals(true, conv instanceof MethodConstructorStringConverter<?>);
        assertSame(conv, test.findConverter(DistanceNoAnnotations.class));
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_registerMethodConstructor_nullClass() {
        StringConvert test = new StringConvert();
        test.registerMethodConstructor(null, "toString");
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_registerMethodConstructor_nullToString() {
        StringConvert test = new StringConvert();
        test.registerMethodConstructor(DistanceNoAnnotations.class, null);
    }

    @Test
    public void test_registerMethodConstructor_classAlreadyRegistered() {
      StringConvert test = new StringConvert();
      test.registerMethodConstructor(DistanceNoAnnotations.class, "toString");
      test.registerMethodConstructor(DistanceNoAnnotations.class, "toString");
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_convert_toString() {
        assertEquals("StringConvert", new StringConvert().toString());
    }

}
