/*
 *  Copyright 2010 Stephen Colebourne
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

/**
 * Conversion between an {@code Integer} and a {@code String}.
 */
public class Test {

    public static void main(String[] args) {
//        MockIntegerStringConverter mock = MockIntegerStringConverter.INSTANCE;
//        StringConvert.INSTANCE.register(Integer.class, mock);
        String str = StringConvert.INSTANCE.convertToString(new Integer(876));
        System.out.println(str);
        
        String str2 = StringConvert.INSTANCE.convertToString(new Float(12.6f));
        System.out.println(str2);
        
        Distance d = StringConvert.INSTANCE.convertFromString(Distance.class, "23m");
        System.out.println(d);
    }

}
