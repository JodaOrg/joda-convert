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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.RoundingMode;
import java.text.ParseException;

import org.joda.convert.test1.Test1Class;
import org.joda.convert.test2.Test2Class;
import org.joda.convert.test2.Test2Interface;
import org.joda.convert.test3.Test3Class;
import org.joda.convert.test3.Test3SuperClass;
import org.joda.convert.test4.Test4Class;
import org.joda.convert.test4.Test4Interface;
import org.junit.jupiter.api.Test;

/**
 * Test StringConvert.
 */
public class TestStringConvert {
    // avoid var in this class, as precise type checks are useful

    @Test
    void test_constructor() {
        StringConvert test = new StringConvert();
        TypedStringConverter<?> conv = test.findTypedConverter(Integer.class);
        assertTrue(conv instanceof JDKStringConverter);
        assertEquals(Integer.class, conv.getEffectiveType());
    }

    @Test
    void test_constructor_true() {
        StringConvert test = new StringConvert(true);
        StringConverter<?> conv = test.findConverter(Integer.class);
        assertTrue(conv instanceof JDKStringConverter);
    }

    @Test
    void test_constructor_false() {
        assertThrows(IllegalStateException.class, () -> {
            StringConvert test = new StringConvert(false);
            StringConverter<?> conv = test.findConverter(Integer.class);
            assertNull(conv);
        });
    }

    //-----------------------------------------------------------------------
    @Test
    void test_isConvertible() {
        assertTrue(StringConvert.INSTANCE.isConvertible(Integer.class));
        assertTrue(StringConvert.INSTANCE.isConvertible(String.class));
        assertFalse(StringConvert.INSTANCE.isConvertible(Object.class));
    }

    //-----------------------------------------------------------------------
    @Test
    void test_convertToString() {
        Integer i = 6;
        assertEquals("6", StringConvert.INSTANCE.convertToString(i));
    }

    @Test
    void test_convertToString_primitive() {
        int i = 6;
        assertEquals("6", StringConvert.INSTANCE.convertToString(i));
    }

    @Test
    void test_convertToString_inherit() {
        assertEquals("CEILING", StringConvert.INSTANCE.convertToString(RoundingMode.CEILING));
    }

    @Test
    void test_convertToString_null() {
        assertNull(StringConvert.INSTANCE.convertToString(null));
    }

    //-----------------------------------------------------------------------
    @Test
    void test_convertToString_withType() {
        Integer i = 6;
        assertEquals("6", StringConvert.INSTANCE.convertToString(Integer.class, i));
    }

    @Test
    void test_convertToString_withType_noGenerics() {
        Integer i = 6;
        Class<?> cls = Integer.class;
        assertEquals("6", StringConvert.INSTANCE.convertToString(cls, i));
    }

    @Test
    void test_convertToString_withType_primitive1() {
        int i = 6;
        assertEquals("6", StringConvert.INSTANCE.convertToString(Integer.class, i));
    }

    @Test
    void test_convertToString_withType_primitive2() {
        int i = 6;
        assertEquals("6", StringConvert.INSTANCE.convertToString(Integer.TYPE, i));
    }

    @Test
    void test_convertToString_withType_inherit1() {
        assertEquals("CEILING", StringConvert.INSTANCE.convertToString(RoundingMode.class, RoundingMode.CEILING));
    }

    @Test
    void test_convertToString_withType_null() {
        assertNull(StringConvert.INSTANCE.convertToString(Integer.class, null));
    }

    @Test
    void test_convertToString_withType_nullClass() {
        assertThrows(IllegalArgumentException.class, () -> {
            assertNull(StringConvert.INSTANCE.convertToString(null, "6"));
        });
    }

    //-----------------------------------------------------------------------
    @Test
    void test_convertFromString() {
        assertEquals(Integer.valueOf(6), StringConvert.INSTANCE.convertFromString(Integer.class, "6"));
    }

    @Test
    void test_convertFromString_primitiveInt() {
        assertEquals(Integer.valueOf(6), StringConvert.INSTANCE.convertFromString(Integer.TYPE, "6"));
    }

    @Test
    void test_convertFromString_primitiveBoolean() {
        assertEquals(Boolean.TRUE, StringConvert.INSTANCE.convertFromString(Boolean.TYPE, "true"));
    }

    @Test
    void test_convertFromString_enumSubclass() {
        assertEquals(ValidityCheck.VALID, StringConvert.INSTANCE.convertFromString(ValidityCheck.class, "VALID"));
    }

    @Test
    void test_convertFromString_inherit() {
        assertEquals(RoundingMode.CEILING, StringConvert.INSTANCE.convertFromString(RoundingMode.class, "CEILING"));
    }

    @Test
    void test_convertFromString_inheritNotSearchedFor() {
        assertThrows(RuntimeException.class, () -> {
            StringConvert.INSTANCE.convertFromString(AltCharSequence.class, "A");
        });
    }

    @Test
    void test_convertFromString_null() {
        assertNull(StringConvert.INSTANCE.convertFromString(Integer.class, null));
    }

    @Test
    void test_convertFromString_nullClass() {
        assertThrows(IllegalArgumentException.class, () -> {
            assertNull(StringConvert.INSTANCE.convertFromString(null, "6"));
        });
    }

    //-----------------------------------------------------------------------
    @Test
    void test_findConverter() {
        Class<Integer> cls = Integer.class;
        StringConverter<Integer> conv = StringConvert.INSTANCE.findConverter(cls);
        assertEquals(Integer.valueOf(12), conv.convertFromString(cls, "12"));
        assertEquals("12", conv.convertToString(12));
    }

    @Test
    void test_findConverter_null() {
        assertThrows(IllegalArgumentException.class, () -> {
            StringConvert.INSTANCE.findConverter(null);
        });
    }

    @Test
    void test_findConverter_Object() {
        assertThrows(IllegalStateException.class, () -> {
            StringConvert.INSTANCE.findConverter(Object.class);
        });
    }

    //-----------------------------------------------------------------------
    @Test
    void test_findConverterNoGenerics() {
        Class<?> cls = Integer.class;
        StringConverter<Object> conv = StringConvert.INSTANCE.findConverterNoGenerics(cls);
        assertEquals(Integer.valueOf(12), conv.convertFromString(cls, "12"));
        assertEquals("12", conv.convertToString(12));
    }

    @Test
    void test_findConverterNoGenerics_null() {
        assertThrows(IllegalArgumentException.class, () -> {
            StringConvert.INSTANCE.findConverterNoGenerics(null);
        });
    }

    @Test
    void test_findConverterNoGenerics_Object() {
        assertThrows(IllegalStateException.class, () -> {
            StringConvert.INSTANCE.findConverterNoGenerics(Object.class);
        });
    }

    //-----------------------------------------------------------------------
    @Test
    void test_convert_annotationMethodMethod() {
        StringConvert test = new StringConvert();
        DistanceMethodMethod d = new DistanceMethodMethod(25);
        assertEquals("25m", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(DistanceMethodMethod.class, "25m").amount);
        TypedStringConverter<DistanceMethodMethod> conv = test.findTypedConverter(DistanceMethodMethod.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertSame(conv, test.findConverter(DistanceMethodMethod.class));
        assertEquals(DistanceMethodMethod.class, conv.getEffectiveType());
        assertTrue(conv.toString().startsWith("RefectionStringConverter"));
    }

    @Test
    void test_convert_annotationMethodMethodCharSequence() {
        StringConvert test = new StringConvert();
        DistanceMethodMethodCharSequence d = new DistanceMethodMethodCharSequence(25);
        assertEquals("25m", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(DistanceMethodMethodCharSequence.class, "25m").amount);
        TypedStringConverter<DistanceMethodMethodCharSequence> conv = test.findTypedConverter(DistanceMethodMethodCharSequence.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertSame(conv, test.findConverter(DistanceMethodMethodCharSequence.class));
        assertEquals(DistanceMethodMethodCharSequence.class, conv.getEffectiveType());
        assertTrue(conv.toString().startsWith("RefectionStringConverter"));
    }

    @Test
    void test_convert_annotationMethodConstructor() {
        StringConvert test = new StringConvert();
        DistanceMethodConstructor d = new DistanceMethodConstructor(25);
        assertEquals("25m", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(DistanceMethodConstructor.class, "25m").amount);
        TypedStringConverter<DistanceMethodConstructor> conv = test.findTypedConverter(DistanceMethodConstructor.class);
        assertFromStringConverter(conv, ConstructorFromStringConverter.class);
        assertSame(conv, test.findConverter(DistanceMethodConstructor.class));
        assertEquals(DistanceMethodConstructor.class, conv.getEffectiveType());
        assertTrue(conv.toString().startsWith("RefectionStringConverter"));
    }

    @Test
    void test_convert_annotationMethodConstructorCharSequence() {
        StringConvert test = new StringConvert();
        DistanceMethodConstructorCharSequence d = new DistanceMethodConstructorCharSequence(25);
        assertEquals("25m", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(DistanceMethodConstructorCharSequence.class, "25m").amount);
        TypedStringConverter<DistanceMethodConstructorCharSequence> conv =
                test.findTypedConverter(DistanceMethodConstructorCharSequence.class);
        assertFromStringConverter(conv, ConstructorFromStringConverter.class);
        assertSame(conv, test.findConverter(DistanceMethodConstructorCharSequence.class));
        assertEquals(DistanceMethodConstructorCharSequence.class, conv.getEffectiveType());
        assertTrue(conv.toString().startsWith("RefectionStringConverter"));
    }

    @Test
    void test_convert_annotationMethodBridgeMethod() {
        StringConvert test = new StringConvert();
        HasCodeImpl d = new HasCodeImpl("CODE");
        assertEquals("CODE", test.convertToString(d));
        assertEquals(d.code, test.convertFromString(HasCodeImpl.class, "CODE").code);
        TypedStringConverter<HasCodeImpl> conv = test.findTypedConverter(HasCodeImpl.class);
        assertFromStringConverter(conv, ConstructorFromStringConverter.class);
        assertSame(conv, test.findConverter(HasCodeImpl.class));
        assertEquals(HasCodeImpl.class, conv.getEffectiveType());
        assertTrue(conv.toString().startsWith("RefectionStringConverter"));
    }

    @Test
    void test_convert_annotationSubMethodMethod() {
        StringConvert test = new StringConvert();
        SubMethodMethod d = new SubMethodMethod(25);
        assertEquals("25m", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(SubMethodMethod.class, "25m").amount);
        TypedStringConverter<SubMethodMethod> conv = test.findTypedConverter(SubMethodMethod.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertEquals(SubMethodMethod.class, conv.getEffectiveType());
        assertSame(conv, test.findConverter(SubMethodMethod.class));
    }

    @Test
    void test_convert_annotationSubMethodConstructor() {
        StringConvert test = new StringConvert();
        SubMethodConstructor d = new SubMethodConstructor("25m");
        assertEquals("25m", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(SubMethodConstructor.class, "25m").amount);
        TypedStringConverter<SubMethodConstructor> conv = test.findTypedConverter(SubMethodConstructor.class);
        assertFromStringConverter(conv, ConstructorFromStringConverter.class);
        assertEquals(SubMethodConstructor.class, conv.getEffectiveType());
        assertSame(conv, test.findConverter(SubMethodConstructor.class));
    }

    @Test
    void test_convert_annotationSuperFactorySuper() {
        StringConvert test = new StringConvert();
        SuperFactorySuper d = new SuperFactorySuper(25);
        assertEquals("25m", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(SuperFactorySuper.class, "25m").amount);
        TypedStringConverter<SuperFactorySuper> conv = test.findTypedConverter(SuperFactorySuper.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertEquals(SuperFactorySuper.class, conv.getEffectiveType());
        assertSame(conv, test.findConverter(SuperFactorySuper.class));
    }

    @Test
    void test_convert_annotationSuperFactorySubViaSuper() {
        StringConvert test = new StringConvert();
        SuperFactorySub d = new SuperFactorySub(8);
        assertEquals("8m", test.convertToString(d));
        SuperFactorySuper fromStr = test.convertFromString(SuperFactorySuper.class, "8m");
        assertEquals(d.amount, fromStr.amount);
        assertTrue(fromStr instanceof SuperFactorySub);
        TypedStringConverter<SuperFactorySub> conv = test.findTypedConverter(SuperFactorySub.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertEquals(SuperFactorySuper.class, conv.getEffectiveType());
        assertSame(conv, test.findConverter(SuperFactorySub.class));
    }

    @Test
    void test_convert_annotationSuperFactorySubViaSub1() {
        StringConvert test = new StringConvert();
        SuperFactorySub d = new SuperFactorySub(25);
        assertEquals("25m", test.convertToString(d));
    }

    // TODO problem is fwks, that just request a converter based on the type of the object
    @Test
    void test_convert_annotationSuperFactorySubViaSub2() {
        assertThrows(ClassCastException.class, () -> {
            StringConvert test = new StringConvert();
            test.convertFromString(SuperFactorySub.class, "25m");
        });
    }

    @Test
    void test_convert_annotationToStringInvokeException() {
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
    void test_convert_annotationFromStringInvokeException() {
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
    void test_convert_annotationFactoryMethod() {
        StringConvert test = new StringConvert();
        DistanceWithFactory d = new DistanceWithFactory(25);
        assertEquals("25m", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(DistanceWithFactory.class, "25m").amount);
        TypedStringConverter<DistanceWithFactory> conv = test.findTypedConverter(DistanceWithFactory.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertEquals(DistanceWithFactory.class, conv.getEffectiveType());
        assertSame(conv, test.findConverter(DistanceWithFactory.class));
        assertTrue(conv.toString().startsWith("RefectionStringConverter"));
    }

    @Test
    void test_convert_annotation_ToStringOnInterface() {
        StringConvert test = new StringConvert();
        Test1Class d = new Test1Class(25);
        assertEquals("25g", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(Test1Class.class, "25g").amount);
        TypedStringConverter<Test1Class> conv = test.findTypedConverter(Test1Class.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertEquals(Test1Class.class, conv.getEffectiveType());
        assertSame(conv, test.findConverter(Test1Class.class));
        assertTrue(conv.toString().startsWith("RefectionStringConverter"));
    }

    @Test
    void test_convert_annotation_FactoryAndToStringOnInterface() {
        StringConvert test = new StringConvert();
        Test2Class d = new Test2Class(25);
        assertEquals("25g", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(Test2Class.class, "25g").amount);
        TypedStringConverter<Test2Class> conv = test.findTypedConverter(Test2Class.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertEquals(Test2Interface.class, conv.getEffectiveType());
        assertSame(conv, test.findConverter(Test2Class.class));
        assertTrue(conv.toString().startsWith("RefectionStringConverter"));
    }

    @Test
    void test_convert_annotation_FactoryAndToStringOnInterface_usingInterface() {
        StringConvert test = new StringConvert();
        Test2Class d = new Test2Class(25);
        assertEquals("25g", test.convertToString(d));
        assertEquals("25g", test.convertFromString(Test2Interface.class, "25g").print());
        TypedStringConverter<Test2Interface> conv = test.findTypedConverter(Test2Interface.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertEquals(Test2Interface.class, conv.getEffectiveType());
        assertSame(conv, test.findConverter(Test2Interface.class));
        assertTrue(conv.toString().startsWith("RefectionStringConverter"));
    }

    @Test
    void test_convert_annotation_ToStringFromStringOnSuperClassBeatsInterface() {
        StringConvert test = new StringConvert();
        Test3Class d = new Test3Class(25);
        assertEquals("25g", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(Test3Class.class, "25g").amount);
        TypedStringConverter<Test3Class> conv = test.findTypedConverter(Test3Class.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertSame(conv, test.findConverter(Test3Class.class));
        assertEquals(Test3SuperClass.class, conv.getEffectiveType());
        assertTrue(conv.toString().startsWith("RefectionStringConverter"));
    }

    @Test
    void test_convert_annotation_FromStringFactoryClashingMethods_fromClass() {
        assertThrows(IllegalStateException.class, () -> {
            StringConvert test = new StringConvert();
            test.findConverter(Test4Class.class);
        });
    }

    @Test
    void test_convert_annotation_FromStringFactoryClashingMethods_fromInterface() {
        assertThrows(IllegalStateException.class, () -> {
            StringConvert test = new StringConvert();
            test.findConverter(Test4Interface.class);
        });
    }

    //-----------------------------------------------------------------------
    @Test
    void test_convert_annotationNoMethods() {
        assertThrows(IllegalStateException.class, () -> {
            StringConvert test = new StringConvert();
            test.findConverter(DistanceNoAnnotations.class);
        });
    }

    @Test
    void test_convert_annotatedMethodAndConstructor() {
        assertThrows(IllegalStateException.class, () -> {
            StringConvert test = new StringConvert();
            test.findConverter(DistanceMethodAndConstructorAnnotations.class);
        });
    }

    @Test
    void test_convert_annotatedTwoToString() {
        assertThrows(IllegalStateException.class, () -> {
            StringConvert test = new StringConvert();
            test.findConverter(DistanceTwoToStringAnnotations.class);
        });
    }

    @Test
    void test_convert_annotatedToStringInvalidReturnType() {
        assertThrows(IllegalStateException.class, () -> {
            StringConvert test = new StringConvert();
            test.findConverter(DistanceToStringInvalidReturnType.class);
        });
    }

    @Test
    void test_convert_annotatedToStringInvalidParameters() {
        assertThrows(IllegalStateException.class, () -> {
            StringConvert test = new StringConvert();
            test.findConverter(DistanceToStringInvalidParameters.class);
        });
    }

    @Test
    void test_convert_annotatedFromStringInvalidReturnType() {
        assertThrows(IllegalStateException.class, () -> {
            StringConvert test = new StringConvert();
            test.findConverter(DistanceFromStringInvalidReturnType.class);
        });
    }

    @Test
    void test_convert_annotatedFromStringInvalidParameter() {
        assertThrows(IllegalStateException.class, () -> {
            StringConvert test = new StringConvert();
            test.findConverter(DistanceFromStringInvalidParameter.class);
        });
    }

    @Test
    void test_convert_annotatedFromStringInvalidParameterCount() {
        assertThrows(IllegalStateException.class, () -> {
            StringConvert test = new StringConvert();
            test.findConverter(DistanceFromStringInvalidParameterCount.class);
        });
    }

    @Test
    void test_convert_annotatedFromStringConstructorInvalidParameter() {
        assertThrows(IllegalStateException.class, () -> {
            StringConvert test = new StringConvert();
            test.findConverter(DistanceFromStringConstructorInvalidParameter.class);
        });
    }

    @Test
    void test_convert_annotatedFromStringConstructorInvalidParameterCount() {
        assertThrows(IllegalStateException.class, () -> {
            StringConvert test = new StringConvert();
            test.findConverter(DistanceFromStringConstructorInvalidParameterCount.class);
        });
    }

    @Test
    void test_convert_annotatedToStringNoFromString() {
        assertThrows(IllegalStateException.class, () -> {
            StringConvert test = new StringConvert();
            test.findConverter(DistanceToStringNoFromString.class);
        });
    }

    @Test
    void test_convert_annotatedFromStringNoToString() {
        assertThrows(IllegalStateException.class, () -> {
            StringConvert test = new StringConvert();
            test.findConverter(DistanceFromStringNoToString.class);
        });
    }

    @Test
    void test_convert_annotatedTwoFromStringMethod() {
        assertThrows(IllegalStateException.class, () -> {
            StringConvert test = new StringConvert();
            test.findConverter(DistanceTwoFromStringMethodAnnotations.class);
        });
    }

    //-----------------------------------------------------------------------
    @Test
    void test_convertFromString_annotatedFromStringNoToString() {
        StringConvert test = new StringConvert();
        DistanceFromStringNoToString result = test.convertFromString(DistanceFromStringNoToString.class, "2m");
        assertEquals(new DistanceFromStringNoToString(2), result);
    }

    //-----------------------------------------------------------------------
    @Test
    void test_convert_Enum_overrideDefaultWithConverter() {
        StringConvert test = new StringConvert();
        test.register(Validity.class, ValidityStringConverter.INSTANCE);
        assertEquals("VALID", test.convertToString(Validity.class, Validity.VALID));
        assertEquals("INVALID", test.convertToString(Validity.class, Validity.INVALID));
        assertEquals(Validity.VALID, test.convertFromString(Validity.class, "VALID"));
        assertEquals(Validity.INVALID, test.convertFromString(Validity.class, "INVALID"));
        assertEquals(Validity.VALID, test.convertFromString(Validity.class, "OK"));
    }

    //-----------------------------------------------------------------------
    @Test
    void test_register_classNotNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            StringConvert.INSTANCE.register(null, MockIntegerStringConverter.INSTANCE);
        });
    }

    @Test
    void test_register_converterNotNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            StringConvert.INSTANCE.register(Integer.class, null);
        });
    }

    @Test
    void test_register_notOnShared() {
        assertThrows(IllegalStateException.class, () -> {
            StringConvert.INSTANCE.register(Integer.class, MockIntegerStringConverter.INSTANCE);
        });
    }

    @Test
    void test_register_classAlreadyRegistered() {
        new StringConvert().register(Integer.class, MockIntegerStringConverter.INSTANCE);
    }

    public void test_register_distance() {
        StringConvert test = new StringConvert();
        test.register(DistanceMethodMethod.class, MockDistanceStringConverter.INSTANCE);
        assertSame(MockDistanceStringConverter.INSTANCE, test.findConverter(DistanceMethodMethod.class));
    }

    //-------------------------------------------------------------------------
    ToStringConverter<DistanceNoAnnotations> DISTANCE_TO_STRING_CONVERTER = new ToStringConverter<>() {
        @Override
        public String convertToString(DistanceNoAnnotations object) {
            return object.toString();
        }
    };
    FromStringConverter<DistanceNoAnnotations> DISTANCE_FROM_STRING_CONVERTER = new FromStringConverter<>() {
        @Override
        public DistanceNoAnnotations convertFromString(Class<? extends DistanceNoAnnotations> cls, String str) {
            return DistanceNoAnnotations.parse(str);
        }
    };

    @Test
    void test_register_FunctionalInterfaces() {
        StringConvert test = new StringConvert();
        test.register(DistanceNoAnnotations.class, DISTANCE_TO_STRING_CONVERTER, DISTANCE_FROM_STRING_CONVERTER);
        DistanceNoAnnotations d = new DistanceNoAnnotations(25);
        assertEquals("Distance[25m]", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(DistanceNoAnnotations.class, "25m").amount);
        StringConverter<DistanceNoAnnotations> conv = test.findConverter(DistanceNoAnnotations.class);
        assertTrue(conv.getClass().getName().contains("$"));
        assertSame(conv, test.findConverter(DistanceNoAnnotations.class));
    }

    @Test
    void test_register_FunctionalInterfaces_nullClass() {
        assertThrows(IllegalArgumentException.class, () -> {
            StringConvert test = new StringConvert();
            test.register(null, DISTANCE_TO_STRING_CONVERTER, DISTANCE_FROM_STRING_CONVERTER);
        });
    }

    @Test
    void test_register_FunctionalInterfaces_nullToString() {
        assertThrows(IllegalArgumentException.class, () -> {
            StringConvert test = new StringConvert();
            test.register(DistanceNoAnnotations.class, null, DISTANCE_FROM_STRING_CONVERTER);
        });
    }

    @Test
    void test_register_FunctionalInterfaces_nullFromString() {
        assertThrows(IllegalArgumentException.class, () -> {
            StringConvert test = new StringConvert();
            test.register(DistanceNoAnnotations.class, DISTANCE_TO_STRING_CONVERTER, null);
        });
    }

    @Test
    void test_registerFactory_cannotChangeSingleton() {
        assertThrows(IllegalStateException.class, () -> {
            StringConvert.INSTANCE.register(
                    DistanceNoAnnotations.class, DISTANCE_TO_STRING_CONVERTER, DISTANCE_FROM_STRING_CONVERTER);
        });
    }

    //-------------------------------------------------------------------------
    @Test
    void test_registerMethods() {
        StringConvert test = new StringConvert();
        test.registerMethods(DistanceNoAnnotations.class, "toString", "parse");
        DistanceNoAnnotations d = new DistanceNoAnnotations(25);
        assertEquals("Distance[25m]", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(DistanceNoAnnotations.class, "25m").amount);
        StringConverter<DistanceNoAnnotations> conv = test.findConverter(DistanceNoAnnotations.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertSame(conv, test.findConverter(DistanceNoAnnotations.class));
    }

    @Test
    void test_registerMethodsCharSequence() {
        StringConvert test = new StringConvert();
        test.registerMethods(DistanceNoAnnotationsCharSequence.class, "toString", "parse");
        DistanceNoAnnotationsCharSequence d = new DistanceNoAnnotationsCharSequence(25);
        assertEquals("Distance[25m]", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(DistanceNoAnnotationsCharSequence.class, "25m").amount);
        StringConverter<DistanceNoAnnotationsCharSequence> conv = test.findConverter(DistanceNoAnnotationsCharSequence.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertSame(conv, test.findConverter(DistanceNoAnnotationsCharSequence.class));
    }

    @Test
    void test_registerMethods_nullClass() {
        assertThrows(IllegalArgumentException.class, () -> {
            StringConvert test = new StringConvert();
            test.registerMethods(null, "toString", "parse");
        });
    }

    @Test
    void test_registerMethods_nullToString() {
        assertThrows(IllegalArgumentException.class, () -> {
            StringConvert test = new StringConvert();
            test.registerMethods(DistanceNoAnnotations.class, null, "parse");
        });
    }

    @Test
    void test_registerMethods_nullFromString() {
        assertThrows(IllegalArgumentException.class, () -> {
            StringConvert test = new StringConvert();
            test.registerMethods(DistanceNoAnnotations.class, "toString", null);
        });
    }

    @Test
    void test_registerMethods_noSuchToStringMethod() {
        assertThrows(IllegalArgumentException.class, () -> {
            StringConvert test = new StringConvert();
            test.registerMethods(DistanceNoAnnotations.class, "rubbishName", "parse");
        });
    }

    @Test
    void test_registerMethods_invalidToStringMethod() {
        assertThrows(IllegalArgumentException.class, () -> {
            StringConvert test = new StringConvert();
            test.registerMethods(Thread.class, "currentThread", "toString");
        });
    }

    @Test
    void test_registerMethods_noSuchFromStringMethod() {
        assertThrows(IllegalArgumentException.class, () -> {
            StringConvert test = new StringConvert();
            test.registerMethods(DistanceNoAnnotations.class, "toString", "rubbishName");
        });
    }

    @Test
    void ttest_registerMethods_cannotChangeSingleton() {
        assertThrows(IllegalStateException.class, () -> {
            StringConvert.INSTANCE.registerMethods(DistanceNoAnnotationsCharSequence.class, "toString", "parse");
        });
    }

    @Test
    void test_registerMethods_classAlreadyRegistered() {
        StringConvert test = new StringConvert();
        test.registerMethods(DistanceNoAnnotations.class, "toString", "parse");
        test.registerMethods(DistanceNoAnnotations.class, "toString", "parse");
    }

    //-------------------------------------------------------------------------
    @Test
    void test_registerMethodConstructorCharSequence() {
        StringConvert test = new StringConvert();
        test.registerMethodConstructor(DistanceNoAnnotationsCharSequence.class, "toString");
        DistanceNoAnnotationsCharSequence d = new DistanceNoAnnotationsCharSequence(25);
        assertEquals("Distance[25m]", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(DistanceNoAnnotationsCharSequence.class, "25m").amount);
        StringConverter<DistanceNoAnnotationsCharSequence> conv = test.findConverter(DistanceNoAnnotationsCharSequence.class);
        assertFromStringConverter(conv, ConstructorFromStringConverter.class);
        assertSame(conv, test.findConverter(DistanceNoAnnotationsCharSequence.class));
    }

    @Test
    void test_registerMethodConstructor() {
        StringConvert test = new StringConvert();
        test.registerMethodConstructor(DistanceNoAnnotations.class, "toString");
        DistanceNoAnnotations d = new DistanceNoAnnotations(25);
        assertEquals("Distance[25m]", test.convertToString(d));
        assertEquals(d.amount, test.convertFromString(DistanceNoAnnotations.class, "25m").amount);
        StringConverter<DistanceNoAnnotations> conv = test.findConverter(DistanceNoAnnotations.class);
        assertFromStringConverter(conv, ConstructorFromStringConverter.class);
        assertSame(conv, test.findConverter(DistanceNoAnnotations.class));
    }

    @Test
    void test_registerMethodConstructor_nullClass() {
        assertThrows(IllegalArgumentException.class, () -> {
            StringConvert test = new StringConvert();
            test.registerMethodConstructor(null, "toString");
        });
    }

    @Test
    void test_registerMethodConstructor_nullToString() {
        assertThrows(IllegalArgumentException.class, () -> {
            StringConvert test = new StringConvert();
            test.registerMethodConstructor(DistanceNoAnnotations.class, null);
        });
    }

    @Test
    void test_registerMethodConstructor_noSuchConstructor() {
        assertThrows(IllegalArgumentException.class, () -> {
            StringConvert test = new StringConvert();
            test.registerMethodConstructor(Enum.class, "toString");
        });
    }

    @Test
    void ttest_registerMethodConstructor_cannotChangeSingleton() {
        assertThrows(IllegalStateException.class, () -> {
            StringConvert.INSTANCE.registerMethodConstructor(DistanceNoAnnotationsCharSequence.class, "toString");
        });
    }

    @Test
    void test_registerMethodConstructor_classAlreadyRegistered() {
        StringConvert test = new StringConvert();
        test.registerMethodConstructor(DistanceNoAnnotations.class, "toString");
        test.registerMethodConstructor(DistanceNoAnnotations.class, "toString");
    }

    //-----------------------------------------------------------------------
    @Test
    void test_convert_toString() {
        assertEquals("StringConvert", new StringConvert().toString());
    }

    private void assertFromStringConverter(StringConverter<?> conv, Class<?> expectedType) {
        assertTrue(conv instanceof ReflectionStringConverter<?>);
        Object obj = ((ReflectionStringConverter<?>) conv).fromString;
        assertEquals(expectedType, obj.getClass());
    }

}
