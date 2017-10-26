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

/**
 * Joda-Convert provides a small set of classes to aid conversion between Objects and Strings.
 * <p>
 * The {@code StringConvert} class is the main entry point.
 * <pre>
 * // conversion to String
 * String str = StringConvert.INSTANCE.convertToString(foo);
 * 
 * // conversion from String
 * Foo bar = StringConvert.INSTANCE.convertFromString(Foo.class, str);
 * </pre>
 */
module org.joda.convert {

    // no direct dependency on Guava
    // the code will adapt and add a read edge dynamically if Guava is visible
    // from the module layer that Joda-Convert is loaded in

    // export all packages
    exports org.joda.convert;
    exports org.joda.convert.factory;

}
