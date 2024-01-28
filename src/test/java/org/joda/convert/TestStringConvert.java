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
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

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
class TestStringConvert {
    // avoid var in this class, as precise type checks are useful

    @Test
    void test_constructor() {
        StringConvert test = new StringConvert();
        TypedStringConverter<?> conv = test.findTypedConverter(Integer.class);
        assertThat(conv).isInstanceOf(JDKStringConverter.class);
        assertThat(conv.getEffectiveType()).isEqualTo(Integer.class);
    }

    @Test
    void test_constructor_true() {
        StringConvert test = new StringConvert(true);
        StringConverter<?> conv = test.findConverter(Integer.class);
        assertThat(conv).isInstanceOf(JDKStringConverter.class);
    }

    @Test
    void test_constructor_false() {
        StringConvert test = new StringConvert(false);
        assertThatIllegalStateException()
                .isThrownBy(() -> test.findConverter(Integer.class));
    }

    //-----------------------------------------------------------------------
    @Test
    void test_isConvertible() {
        assertThat(StringConvert.INSTANCE.isConvertible(Integer.class)).isTrue();
        assertThat(StringConvert.INSTANCE.isConvertible(String.class)).isTrue();
        assertThat(StringConvert.INSTANCE.isConvertible(Object.class)).isFalse();
    }

    //-----------------------------------------------------------------------
    @Test
    void test_convertToString() {
        Integer i = 6;
        assertThat(StringConvert.INSTANCE.convertToString(i)).isEqualTo("6");
    }

    @Test
    void test_convertToString_primitive() {
        int i = 6;
        assertThat(StringConvert.INSTANCE.convertToString(i)).isEqualTo("6");
    }

    @Test
    void test_convertToString_inherit() {
        assertThat(StringConvert.INSTANCE.convertToString(RoundingMode.CEILING)).isEqualTo("CEILING");
    }

    @Test
    void test_convertToString_null() {
        assertThat(StringConvert.INSTANCE.convertToString(null)).isNull();
    }

    //-----------------------------------------------------------------------
    @Test
    void test_convertToString_withType() {
        Integer i = 6;
        assertThat(StringConvert.INSTANCE.convertToString(Integer.class, i)).isEqualTo("6");
    }

    @Test
    void test_convertToString_withType_noGenerics() {
        Integer i = 6;
        Class<?> cls = Integer.class;
        assertThat(StringConvert.INSTANCE.convertToString(cls, i)).isEqualTo("6");
    }

    @Test
    void test_convertToString_withType_primitive1() {
        int i = 6;
        assertThat(StringConvert.INSTANCE.convertToString(Integer.class, i)).isEqualTo("6");
    }

    @Test
    void test_convertToString_withType_primitive2() {
        int i = 6;
        assertThat(StringConvert.INSTANCE.convertToString(Integer.TYPE, i)).isEqualTo("6");
    }

    @Test
    void test_convertToString_withType_inherit1() {
        assertThat(StringConvert.INSTANCE.convertToString(RoundingMode.class, RoundingMode.CEILING)).isEqualTo("CEILING");
    }

    @Test
    void test_convertToString_withType_null() {
        assertThat(StringConvert.INSTANCE.convertToString(Integer.class, null)).isNull();
    }

    @Test
    void test_convertToString_withType_nullClass() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> StringConvert.INSTANCE.convertToString(null, "6"));
    }

    //-----------------------------------------------------------------------
    @Test
    void test_convertFromString() {
        assertThat(StringConvert.INSTANCE.convertFromString(Integer.class, "6")).isEqualTo(Integer.valueOf(6));
    }

    @Test
    void test_convertFromString_primitiveInt() {
        assertThat(StringConvert.INSTANCE.convertFromString(Integer.TYPE, "6")).isEqualTo(Integer.valueOf(6));
    }

    @Test
    void test_convertFromString_primitiveBoolean() {
        assertThat(StringConvert.INSTANCE.convertFromString(Boolean.TYPE, "true")).isEqualTo(Boolean.TRUE);
    }

    @Test
    void test_convertFromString_enumSubclass() {
        assertThat(StringConvert.INSTANCE.convertFromString(ValidityCheck.class, "VALID")).isEqualTo(ValidityCheck.VALID);
    }

    @Test
    void test_convertFromString_inherit() {
        assertThat(StringConvert.INSTANCE.convertFromString(RoundingMode.class, "CEILING")).isEqualTo(RoundingMode.CEILING);
    }

    @Test
    void test_convertFromString_inheritNotSearchedFor() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> StringConvert.INSTANCE.convertFromString(AltCharSequence.class, "A"));
    }

    @Test
    void test_convertFromString_null() {
        assertThat(StringConvert.INSTANCE.convertFromString(Integer.class, null)).isNull();
    }

    @Test
    void test_convertFromString_nullClass() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> StringConvert.INSTANCE.convertFromString(null, "6"));
    }

    //-----------------------------------------------------------------------
    @Test
    void test_findConverter() {
        Class<Integer> cls = Integer.class;
        StringConverter<Integer> conv = StringConvert.INSTANCE.findConverter(cls);
        assertThat(conv.convertFromString(cls, "12")).isEqualTo(Integer.valueOf(12));
        assertThat(conv.convertToString(12)).isEqualTo("12");
    }

    @Test
    void test_findConverter_null() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> StringConvert.INSTANCE.findConverter(null));
    }

    @Test
    void test_findConverter_Object() {
        assertThatIllegalStateException()
                .isThrownBy(() -> StringConvert.INSTANCE.findConverter(Object.class));
    }

    //-----------------------------------------------------------------------
    @Test
    void test_findConverterNoGenerics() {
        Class<?> cls = Integer.class;
        StringConverter<Object> conv = StringConvert.INSTANCE.findConverterNoGenerics(cls);
        assertThat(conv.convertFromString(cls, "12")).isEqualTo(Integer.valueOf(12));
        assertThat(conv.convertToString(12)).isEqualTo("12");
    }

    @Test
    void test_findConverterNoGenerics_null() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> StringConvert.INSTANCE.findConverterNoGenerics(null));
    }

    @Test
    void test_findConverterNoGenerics_Object() {
        assertThatIllegalStateException()
                .isThrownBy(() -> StringConvert.INSTANCE.findConverterNoGenerics(Object.class));
    }

    //-----------------------------------------------------------------------
    @Test
    void test_convert_annotationMethodMethod() {
        StringConvert test = new StringConvert();
        DistanceMethodMethod d = new DistanceMethodMethod(25);
        assertThat(test.convertToString(d)).isEqualTo("25m");
        assertThat(test.convertFromString(DistanceMethodMethod.class, "25m").amount).isEqualTo(d.amount);
        TypedStringConverter<DistanceMethodMethod> conv = test.findTypedConverter(DistanceMethodMethod.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertThat(test.findConverter(DistanceMethodMethod.class)).isSameAs(conv);
        assertThat(conv.getEffectiveType()).isEqualTo(DistanceMethodMethod.class);
        assertThat(conv.toString()).startsWith("RefectionStringConverter");
    }

    @Test
    void test_convert_annotationMethodMethodCharSequence() {
        StringConvert test = new StringConvert();
        DistanceMethodMethodCharSequence d = new DistanceMethodMethodCharSequence(25);
        assertThat(test.convertToString(d)).isEqualTo("25m");
        assertThat(test.convertFromString(DistanceMethodMethodCharSequence.class, "25m").amount).isEqualTo(d.amount);
        TypedStringConverter<DistanceMethodMethodCharSequence> conv = test.findTypedConverter(DistanceMethodMethodCharSequence.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertThat(test.findConverter(DistanceMethodMethodCharSequence.class)).isSameAs(conv);
        assertThat(conv.getEffectiveType()).isEqualTo(DistanceMethodMethodCharSequence.class);
        assertThat(conv.toString()).startsWith("RefectionStringConverter");
    }

    @Test
    void test_convert_annotationMethodConstructor() {
        StringConvert test = new StringConvert();
        DistanceMethodConstructor d = new DistanceMethodConstructor(25);
        assertThat(test.convertToString(d)).isEqualTo("25m");
        assertThat(test.convertFromString(DistanceMethodConstructor.class, "25m").amount).isEqualTo(d.amount);
        TypedStringConverter<DistanceMethodConstructor> conv = test.findTypedConverter(DistanceMethodConstructor.class);
        assertFromStringConverter(conv, ConstructorFromStringConverter.class);
        assertThat(test.findConverter(DistanceMethodConstructor.class)).isSameAs(conv);
        assertThat(conv.getEffectiveType()).isEqualTo(DistanceMethodConstructor.class);
        assertThat(conv.toString()).startsWith("RefectionStringConverter");
    }

    @Test
    void test_convert_annotationMethodConstructorCharSequence() {
        StringConvert test = new StringConvert();
        DistanceMethodConstructorCharSequence d = new DistanceMethodConstructorCharSequence(25);
        assertThat(test.convertToString(d)).isEqualTo("25m");
        assertThat(test.convertFromString(DistanceMethodConstructorCharSequence.class, "25m").amount).isEqualTo(d.amount);
        TypedStringConverter<DistanceMethodConstructorCharSequence> conv =
                test.findTypedConverter(DistanceMethodConstructorCharSequence.class);
        assertFromStringConverter(conv, ConstructorFromStringConverter.class);
        assertThat(test.findConverter(DistanceMethodConstructorCharSequence.class)).isSameAs(conv);
        assertThat(conv.getEffectiveType()).isEqualTo(DistanceMethodConstructorCharSequence.class);
        assertThat(conv.toString()).startsWith("RefectionStringConverter");
    }

    @Test
    void test_convert_annotationMethodBridgeMethod() {
        StringConvert test = new StringConvert();
        HasCodeImpl d = new HasCodeImpl("CODE");
        assertThat(test.convertToString(d)).isEqualTo("CODE");
        assertThat(test.convertFromString(HasCodeImpl.class, "CODE").code).isEqualTo(d.code);
        TypedStringConverter<HasCodeImpl> conv = test.findTypedConverter(HasCodeImpl.class);
        assertFromStringConverter(conv, ConstructorFromStringConverter.class);
        assertThat(test.findConverter(HasCodeImpl.class)).isSameAs(conv);
        assertThat(conv.getEffectiveType()).isEqualTo(HasCodeImpl.class);
        assertThat(conv.toString()).startsWith("RefectionStringConverter");
    }

    @Test
    void test_convert_annotationSubMethodMethod() {
        StringConvert test = new StringConvert();
        SubMethodMethod d = new SubMethodMethod(25);
        assertThat(test.convertToString(d)).isEqualTo("25m");
        assertThat(test.convertFromString(SubMethodMethod.class, "25m").amount).isEqualTo(d.amount);
        TypedStringConverter<SubMethodMethod> conv = test.findTypedConverter(SubMethodMethod.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertThat(conv.getEffectiveType()).isEqualTo(SubMethodMethod.class);
        assertThat(test.findConverter(SubMethodMethod.class)).isSameAs(conv);
    }

    @Test
    void test_convert_annotationSubMethodConstructor() {
        StringConvert test = new StringConvert();
        SubMethodConstructor d = new SubMethodConstructor("25m");
        assertThat(test.convertToString(d)).isEqualTo("25m");
        assertThat(test.convertFromString(SubMethodConstructor.class, "25m").amount).isEqualTo(d.amount);
        TypedStringConverter<SubMethodConstructor> conv = test.findTypedConverter(SubMethodConstructor.class);
        assertFromStringConverter(conv, ConstructorFromStringConverter.class);
        assertThat(conv.getEffectiveType()).isEqualTo(SubMethodConstructor.class);
        assertThat(test.findConverter(SubMethodConstructor.class)).isSameAs(conv);
    }

    @Test
    void test_convert_annotationSuperFactorySuper() {
        StringConvert test = new StringConvert();
        SuperFactorySuper d = new SuperFactorySuper(25);
        assertThat(test.convertToString(d)).isEqualTo("25m");
        assertThat(test.convertFromString(SuperFactorySuper.class, "25m").amount).isEqualTo(d.amount);
        TypedStringConverter<SuperFactorySuper> conv = test.findTypedConverter(SuperFactorySuper.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertThat(conv.getEffectiveType()).isEqualTo(SuperFactorySuper.class);
        assertThat(test.findConverter(SuperFactorySuper.class)).isSameAs(conv);
    }

    @Test
    void test_convert_annotationSuperFactorySubViaSuper() {
        StringConvert test = new StringConvert();
        SuperFactorySub d = new SuperFactorySub(8);
        assertThat(test.convertToString(d)).isEqualTo("8m");
        SuperFactorySuper fromStr = test.convertFromString(SuperFactorySuper.class, "8m");
        assertThat(fromStr.amount).isEqualTo(d.amount);
        assertThat(fromStr).isInstanceOf(SuperFactorySub.class);
        TypedStringConverter<SuperFactorySub> conv = test.findTypedConverter(SuperFactorySub.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertThat(conv.getEffectiveType()).isEqualTo(SuperFactorySuper.class);
        assertThat(test.findConverter(SuperFactorySub.class)).isSameAs(conv);
    }

    @Test
    void test_convert_annotationSuperFactorySubViaSub1() {
        StringConvert test = new StringConvert();
        SuperFactorySub d = new SuperFactorySub(25);
        assertThat(test.convertToString(d)).isEqualTo("25m");
    }

    // TODO problem is fwks, that just request a converter based on the type of the object
    @Test
    void test_convert_annotationSuperFactorySubViaSub2() {
        StringConvert test = new StringConvert();
        assertThatExceptionOfType(ClassCastException.class)
                .isThrownBy(() -> test.convertFromString(SuperFactorySub.class, "25m"));
    }

    @Test
    void test_convert_annotationToStringInvokeException() {
        StringConvert test = new StringConvert();
        DistanceToStringException d = new DistanceToStringException(25);
        StringConverter<DistanceToStringException> conv = test.findConverter(DistanceToStringException.class);
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> conv.convertToString(d))
                .withCauseExactlyInstanceOf(ParseException.class);
    }

    @Test
    void test_convert_annotationFromStringInvokeException() {
        StringConvert test = new StringConvert();
        StringConverter<DistanceFromStringException> conv = test.findConverter(DistanceFromStringException.class);
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> conv.convertFromString(DistanceFromStringException.class, "25m"))
                .withCauseExactlyInstanceOf(ParseException.class);
    }

    //-----------------------------------------------------------------------
    @Test
    void test_convert_annotationFactoryMethod() {
        StringConvert test = new StringConvert();
        DistanceWithFactory d = new DistanceWithFactory(25);
        assertThat(test.convertToString(d)).isEqualTo("25m");
        assertThat(test.convertFromString(DistanceWithFactory.class, "25m").amount).isEqualTo(d.amount);
        TypedStringConverter<DistanceWithFactory> conv = test.findTypedConverter(DistanceWithFactory.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertThat(conv.getEffectiveType()).isEqualTo(DistanceWithFactory.class);
        assertThat(test.findConverter(DistanceWithFactory.class)).isSameAs(conv);
        assertThat(conv.toString()).startsWith("RefectionStringConverter");
    }

    @Test
    void test_convert_annotation_ToStringOnInterface() {
        StringConvert test = new StringConvert();
        Test1Class d = new Test1Class(25);
        assertThat(test.convertToString(d)).isEqualTo("25g");
        assertThat(test.convertFromString(Test1Class.class, "25g").amount).isEqualTo(d.amount);
        TypedStringConverter<Test1Class> conv = test.findTypedConverter(Test1Class.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertThat(conv.getEffectiveType()).isEqualTo(Test1Class.class);
        assertThat(test.findConverter(Test1Class.class)).isSameAs(conv);
        assertThat(conv.toString()).startsWith("RefectionStringConverter");
    }

    @Test
    void test_convert_annotation_FactoryAndToStringOnInterface() {
        StringConvert test = new StringConvert();
        Test2Class d = new Test2Class(25);
        assertThat(test.convertToString(d)).isEqualTo("25g");
        assertThat(test.convertFromString(Test2Class.class, "25g").amount).isEqualTo(d.amount);
        TypedStringConverter<Test2Class> conv = test.findTypedConverter(Test2Class.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertThat(conv.getEffectiveType()).isEqualTo(Test2Interface.class);
        assertThat(test.findConverter(Test2Class.class)).isSameAs(conv);
        assertThat(conv.toString()).startsWith("RefectionStringConverter");
    }

    @Test
    void test_convert_annotation_FactoryAndToStringOnInterface_usingInterface() {
        StringConvert test = new StringConvert();
        Test2Class d = new Test2Class(25);
        assertThat(test.convertToString(d)).isEqualTo("25g");
        assertThat(test.convertFromString(Test2Interface.class, "25g").print()).isEqualTo("25g");
        TypedStringConverter<Test2Interface> conv = test.findTypedConverter(Test2Interface.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertThat(conv.getEffectiveType()).isEqualTo(Test2Interface.class);
        assertThat(test.findConverter(Test2Interface.class)).isSameAs(conv);
        assertThat(conv.toString()).startsWith("RefectionStringConverter");
    }

    @Test
    void test_convert_annotation_ToStringFromStringOnSuperClassBeatsInterface() {
        StringConvert test = new StringConvert();
        Test3Class d = new Test3Class(25);
        assertThat(test.convertToString(d)).isEqualTo("25g");
        assertThat(test.convertFromString(Test3Class.class, "25g").amount).isEqualTo(d.amount);
        TypedStringConverter<Test3Class> conv = test.findTypedConverter(Test3Class.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertThat(test.findConverter(Test3Class.class)).isSameAs(conv);
        assertThat(conv.getEffectiveType()).isEqualTo(Test3SuperClass.class);
        assertThat(conv.toString()).startsWith("RefectionStringConverter");
    }

    @Test
    void test_convert_annotation_FromStringFactoryClashingMethods_fromClass() {
        StringConvert test = new StringConvert();
        assertThatIllegalStateException()
                .isThrownBy(() -> test.findConverter(Test4Class.class));
    }

    @Test
    void test_convert_annotation_FromStringFactoryClashingMethods_fromInterface() {
        StringConvert test = new StringConvert();
        assertThatIllegalStateException()
                .isThrownBy(() -> test.findConverter(Test4Interface.class));
    }

    //-----------------------------------------------------------------------
    @Test
    void test_convert_annotationNoMethods() {
        StringConvert test = new StringConvert();
        assertThatIllegalStateException()
                .isThrownBy(() -> test.findConverter(DistanceNoAnnotations.class));
    }

    @Test
    void test_convert_annotatedMethodAndConstructor() {
        StringConvert test = new StringConvert();
        assertThatIllegalStateException()
                .isThrownBy(() -> test.findConverter(DistanceMethodAndConstructorAnnotations.class));
    }

    @Test
    void test_convert_annotatedTwoToString() {
        StringConvert test = new StringConvert();
        assertThatIllegalStateException()
                .isThrownBy(() -> test.findConverter(DistanceTwoToStringAnnotations.class));
    }

    @Test
    void test_convert_annotatedToStringInvalidReturnType() {
        StringConvert test = new StringConvert();
        assertThatIllegalStateException()
                .isThrownBy(() -> test.findConverter(DistanceToStringInvalidReturnType.class));
    }

    @Test
    void test_convert_annotatedToStringInvalidParameters() {
        StringConvert test = new StringConvert();
        assertThatIllegalStateException()
                .isThrownBy(() -> test.findConverter(DistanceToStringInvalidParameters.class));
    }

    @Test
    void test_convert_annotatedFromStringInvalidReturnType() {
        StringConvert test = new StringConvert();
        assertThatIllegalStateException()
                .isThrownBy(() -> test.findConverter(DistanceFromStringInvalidReturnType.class));
    }

    @Test
    void test_convert_annotatedFromStringInvalidParameter() {
        StringConvert test = new StringConvert();
        assertThatIllegalStateException()
                .isThrownBy(() -> test.findConverter(DistanceFromStringInvalidParameter.class));
    }

    @Test
    void test_convert_annotatedFromStringInvalidParameterCount() {
        StringConvert test = new StringConvert();
        assertThatIllegalStateException()
                .isThrownBy(() -> test.findConverter(DistanceFromStringInvalidParameterCount.class));
    }

    @Test
    void test_convert_annotatedFromStringConstructorInvalidParameter() {
        StringConvert test = new StringConvert();
        assertThatIllegalStateException()
                .isThrownBy(() -> test.findConverter(DistanceFromStringConstructorInvalidParameter.class));
    }

    @Test
    void test_convert_annotatedFromStringConstructorInvalidParameterCount() {
        StringConvert test = new StringConvert();
        assertThatIllegalStateException()
                .isThrownBy(() -> test.findConverter(DistanceFromStringConstructorInvalidParameterCount.class));
    }

    @Test
    void test_convert_annotatedToStringNoFromString() {
        StringConvert test = new StringConvert();
        assertThatIllegalStateException()
                .isThrownBy(() -> test.findConverter(DistanceToStringNoFromString.class));
    }

    @Test
    void test_convert_annotatedFromStringNoToString() {
        StringConvert test = new StringConvert();
        assertThatIllegalStateException()
                .isThrownBy(() -> test.findConverter(DistanceFromStringNoToString.class));
    }

    @Test
    void test_convert_annotatedTwoFromStringMethod() {
        StringConvert test = new StringConvert();
        assertThatIllegalStateException()
                .isThrownBy(() -> test.findConverter(DistanceTwoFromStringMethodAnnotations.class));
    }

    //-----------------------------------------------------------------------
    @Test
    void test_convertFromString_annotatedFromStringNoToString() {
        StringConvert test = new StringConvert();
        DistanceFromStringNoToString result = test.convertFromString(DistanceFromStringNoToString.class, "2m");
        assertThat(result).isEqualTo(new DistanceFromStringNoToString(2));
    }

    //-----------------------------------------------------------------------
    @Test
    void test_convert_Enum_overrideDefaultWithConverter() {
        StringConvert test = new StringConvert();
        test.register(Validity.class, ValidityStringConverter.INSTANCE);
        assertThat(test.convertToString(Validity.class, Validity.VALID)).isEqualTo("VALID");
        assertThat(test.convertToString(Validity.class, Validity.INVALID)).isEqualTo("INVALID");
        assertThat(test.convertFromString(Validity.class, "VALID")).isEqualTo(Validity.VALID);
        assertThat(test.convertFromString(Validity.class, "INVALID")).isEqualTo(Validity.INVALID);
        assertThat(test.convertFromString(Validity.class, "OK")).isEqualTo(Validity.VALID);
    }

    //-----------------------------------------------------------------------
    @Test
    void test_register_classNotNull() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> StringConvert.INSTANCE.register(null, MockIntegerStringConverter.INSTANCE));
    }

    @Test
    void test_register_converterNotNull() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> StringConvert.INSTANCE.register(Integer.class, null));
    }

    @Test
    void test_register_notOnShared() {
        assertThatIllegalStateException()
                .isThrownBy(() -> StringConvert.INSTANCE.register(Integer.class, MockIntegerStringConverter.INSTANCE));
    }

    @Test
    void test_register_classAlreadyRegistered() {
        new StringConvert().register(Integer.class, MockIntegerStringConverter.INSTANCE);
    }

    public void test_register_distance() {
        StringConvert test = new StringConvert();
        test.register(DistanceMethodMethod.class, MockDistanceStringConverter.INSTANCE);
        assertThat(test.findConverter(DistanceMethodMethod.class)).isSameAs(MockDistanceStringConverter.INSTANCE);
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
        assertThat(test.convertToString(d)).isEqualTo("Distance[25m]");
        assertThat(test.convertFromString(DistanceNoAnnotations.class, "25m").amount).isEqualTo(d.amount);
        StringConverter<DistanceNoAnnotations> conv = test.findConverter(DistanceNoAnnotations.class);
        assertThat(conv.getClass().getName()).contains("$");
        assertThat(test.findConverter(DistanceNoAnnotations.class)).isSameAs(conv);
    }

    @Test
    void test_register_FunctionalInterfaces_nullClass() {
        StringConvert test = new StringConvert();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> test.register(null, DISTANCE_TO_STRING_CONVERTER, DISTANCE_FROM_STRING_CONVERTER));
    }

    @Test
    void test_register_FunctionalInterfaces_nullToString() {
        StringConvert test = new StringConvert();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> test.register(DistanceNoAnnotations.class, null, DISTANCE_FROM_STRING_CONVERTER));
    }

    @Test
    void test_register_FunctionalInterfaces_nullFromString() {
        StringConvert test = new StringConvert();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> test.register(DistanceNoAnnotations.class, DISTANCE_TO_STRING_CONVERTER, null));
    }

    @Test
    void test_registerFactory_cannotChangeSingleton() {
        assertThatIllegalStateException()
                .isThrownBy(() -> StringConvert.INSTANCE.register(
                        DistanceNoAnnotations.class, DISTANCE_TO_STRING_CONVERTER, DISTANCE_FROM_STRING_CONVERTER));
    }

    //-------------------------------------------------------------------------
    @Test
    void test_registerMethods() {
        StringConvert test = new StringConvert();
        test.registerMethods(DistanceNoAnnotations.class, "toString", "parse");
        DistanceNoAnnotations d = new DistanceNoAnnotations(25);
        assertThat(test.convertToString(d)).isEqualTo("Distance[25m]");
        assertThat(test.convertFromString(DistanceNoAnnotations.class, "25m").amount).isEqualTo(d.amount);
        StringConverter<DistanceNoAnnotations> conv = test.findConverter(DistanceNoAnnotations.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertThat(test.findConverter(DistanceNoAnnotations.class)).isSameAs(conv);
    }

    @Test
    void test_registerMethodsCharSequence() {
        StringConvert test = new StringConvert();
        test.registerMethods(DistanceNoAnnotationsCharSequence.class, "toString", "parse");
        DistanceNoAnnotationsCharSequence d = new DistanceNoAnnotationsCharSequence(25);
        assertThat(test.convertToString(d)).isEqualTo("Distance[25m]");
        assertThat(test.convertFromString(DistanceNoAnnotationsCharSequence.class, "25m").amount).isEqualTo(d.amount);
        StringConverter<DistanceNoAnnotationsCharSequence> conv = test.findConverter(DistanceNoAnnotationsCharSequence.class);
        assertFromStringConverter(conv, MethodFromStringConverter.class);
        assertThat(test.findConverter(DistanceNoAnnotationsCharSequence.class)).isSameAs(conv);
    }

    @Test
    void test_registerMethods_nullClass() {
        StringConvert test = new StringConvert();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> test.registerMethods(null, "toString", "parse"));
    }

    @Test
    void test_registerMethods_nullToString() {
        StringConvert test = new StringConvert();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> test.registerMethods(DistanceNoAnnotations.class, null, "parse"));
    }

    @Test
    void test_registerMethods_nullFromString() {
        StringConvert test = new StringConvert();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> test.registerMethods(DistanceNoAnnotations.class, "toString", null));
    }

    @Test
    void test_registerMethods_noSuchToStringMethod() {
        StringConvert test = new StringConvert();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> test.registerMethods(DistanceNoAnnotations.class, "rubbishName", "parse"));
    }

    @Test
    void test_registerMethods_invalidToStringMethod() {
        StringConvert test = new StringConvert();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> test.registerMethods(Thread.class, "currentThread", "toString"));
    }

    @Test
    void test_registerMethods_noSuchFromStringMethod() {
        StringConvert test = new StringConvert();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> test.registerMethods(DistanceNoAnnotations.class, "toString", "rubbishName"));
    }

    @Test
    void ttest_registerMethods_cannotChangeSingleton() {
        assertThatIllegalStateException()
                .isThrownBy(() -> StringConvert.INSTANCE.registerMethods(
                        DistanceNoAnnotationsCharSequence.class, "toString", "parse"));
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
        assertThat(test.convertToString(d)).isEqualTo("Distance[25m]");
        assertThat(test.convertFromString(DistanceNoAnnotationsCharSequence.class, "25m").amount).isEqualTo(d.amount);
        StringConverter<DistanceNoAnnotationsCharSequence> conv = test.findConverter(DistanceNoAnnotationsCharSequence.class);
        assertFromStringConverter(conv, ConstructorFromStringConverter.class);
        assertThat(test.findConverter(DistanceNoAnnotationsCharSequence.class)).isSameAs(conv);
    }

    @Test
    void test_registerMethodConstructor() {
        StringConvert test = new StringConvert();
        test.registerMethodConstructor(DistanceNoAnnotations.class, "toString");
        DistanceNoAnnotations d = new DistanceNoAnnotations(25);
        assertThat(test.convertToString(d)).isEqualTo("Distance[25m]");
        assertThat(test.convertFromString(DistanceNoAnnotations.class, "25m").amount).isEqualTo(d.amount);
        StringConverter<DistanceNoAnnotations> conv = test.findConverter(DistanceNoAnnotations.class);
        assertFromStringConverter(conv, ConstructorFromStringConverter.class);
        assertThat(test.findConverter(DistanceNoAnnotations.class)).isSameAs(conv);
    }

    @Test
    void test_registerMethodConstructor_nullClass() {
        StringConvert test = new StringConvert();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> test.registerMethodConstructor(null, "toString"));
    }

    @Test
    void test_registerMethodConstructor_nullToString() {
        StringConvert test = new StringConvert();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> test.registerMethodConstructor(DistanceNoAnnotations.class, null));
    }

    @Test
    void test_registerMethodConstructor_noSuchConstructor() {
        StringConvert test = new StringConvert();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> test.registerMethodConstructor(Enum.class, "toString"));
    }

    @Test
    void ttest_registerMethodConstructor_cannotChangeSingleton() {
        assertThatIllegalStateException()
                .isThrownBy(() -> StringConvert.INSTANCE.registerMethodConstructor(
                        DistanceNoAnnotationsCharSequence.class, "toString"));
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
        assertThat(new StringConvert().toString()).isEqualTo("StringConvert");
    }

    private void assertFromStringConverter(StringConverter<?> conv, Class<?> expectedType) {
        assertThat(conv).isInstanceOf(ReflectionStringConverter.class);
        Object obj = ((ReflectionStringConverter<?>) conv).fromString;
        assertThat(obj.getClass()).isEqualTo(expectedType);
    }

}
