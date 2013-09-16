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
package org.joda.convert.test1;

import org.joda.convert.FromString;

/**
 * Example class with annotated methods.
 */
public class Test1Class implements Test1Interface {

    /** Amount. */
    public final int amount;

    @FromString
    public static Test1Class parse(String amount) {
        amount = amount.substring(0, amount.length() - 1);
        return new Test1Class(Integer.parseInt(amount));
    }

    public Test1Class(int amount) {
        this.amount = amount;
    }

    public String print() {
        return amount + "g";
    }

    @Override
    public String toString() {
        return "Weight[" + amount + "g]";
    }

}
