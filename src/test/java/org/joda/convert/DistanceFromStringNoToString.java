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

/**
 * Example class with no annotations.
 */
public class DistanceFromStringNoToString {

    /** Amount. */
    final int amount;

    public DistanceFromStringNoToString(int amount) {
        this.amount = amount;
    }

    @FromString
    public DistanceFromStringNoToString(String amount) {
        this.amount = Integer.parseInt(amount.substring(0, amount.length() - 1));
    }

    public String print() {
        return amount + "m";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DistanceFromStringNoToString &&
                ((DistanceFromStringNoToString) obj).amount == amount;
    }

    @Override
    public int hashCode() {
        return amount;
    }

    @Override
    public String toString() {
        return "Distance[" + amount + "m]";
    }

}
