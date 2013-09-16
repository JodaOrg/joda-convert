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
package org.joda.convert.test3;

import org.joda.convert.FromString;
import org.joda.convert.ToString;

/**
 * Example class with annotated methods.
 */
public abstract class Test3SuperClass {

    @FromString
    public static Test3SuperClass parse(String amount) {
        amount = amount.substring(0, amount.length() - 1);
        return new Test3Class(Integer.parseInt(amount));
    }

    @ToString
    public abstract String print();

}
