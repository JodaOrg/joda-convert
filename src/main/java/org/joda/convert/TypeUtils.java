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

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parse the string format of Guava TypeToken.
 * <p>
 * This is loaded by reflection only when Guava is on the classpath.
 * It relies on internal methods in Guava that could change in any release.
 * <p>
 * This parser is incomplete, but handles common cases.
 * It does not handle union types or multi-dimensional arrays.
 */
final class TypeUtils {

    // extends
    private static final String EXTENDS = "? extends ";

    // super
    private static final String SUPER = "? super ";

    // primitive types
    private static final Map<String, Class<?>> PRIMITIVES;
    static {
        Map<String, Class<?>> map = new HashMap<String, Class<?>>();
        map.put("byte", byte.class);
        map.put("short", short.class);
        map.put("int", int.class);
        map.put("long", long.class);
        map.put("boolean", boolean.class);
        map.put("char", char.class);
        map.put("float", float.class);
        map.put("double", double.class);
        PRIMITIVES = Collections.unmodifiableMap(map);
    }

    //-----------------------------------------------------------------------
    // constructor
    private TypeUtils() {
    }

    //-----------------------------------------------------------------------
    /**
     * Parses the TypeToken string format.
     * 
     * @param str  the string
     * @return the token
     */
    static Type parse(String str) {
        try {
            return doParse(str);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // parse an element
    private static Type doParse(String str) throws Exception {
        Class<?> token = PRIMITIVES.get(str);
        if (token != null) {
            return token;
        }
        int first = str.indexOf('<');
        if (first < 0) {
            return StringConvert.loadType(str);
        }
        int last = str.lastIndexOf('>');
        String baseStr = str.substring(0, first);
        Class<?> base = StringConvert.loadType(baseStr);
        String argsStr = str.substring(first + 1, last);
        List<String> splitArgs = split(argsStr);
        List<Type> types = new ArrayList<Type>();
        for (String splitArg : splitArgs) {
            Type argType;
            if (splitArg.startsWith(EXTENDS)) {
                String remainder = splitArg.substring(EXTENDS.length());
                argType = wildExtendsType(doParse(remainder));
            } else if (splitArg.startsWith(SUPER)) {
                String remainder = splitArg.substring(SUPER.length());
                argType = wildSuperType(doParse(remainder));
            } else if (splitArg.equals("?")) {
                argType = wildExtendsType(Object.class);
            } else if (splitArg.endsWith("[]")) {
                String componentStr = splitArg.substring(0, splitArg.length() - 2);
                Class<?> componentCls = StringConvert.loadType(componentStr);
                argType = Array.newInstance(componentCls, 0).getClass();
            } else if (splitArg.startsWith("[L") && splitArg.endsWith(";")) {
                String componentStr = splitArg.substring(2, splitArg.length() - 1);
                Class<?> componentCls = StringConvert.loadType(componentStr);
                argType = Array.newInstance(componentCls, 0).getClass();
            } else {
                argType = doParse(splitArg);
            }
            types.add(argType);
        }
        return newParameterizedType(base, types.toArray(new Type[types.size()]));
    }

    // split by comma, handling nested generified types
    private static List<String> split(String str) {
        List<String> result = new ArrayList<String>();
        int genericCount = 0;
        int startPos = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ',' && genericCount == 0) {
                result.add(str.substring(startPos, i).trim());
                startPos = i + 1;
            } else if (str.charAt(i) == '<') {
                genericCount++;
            } else if (str.charAt(i) == '>') {
                genericCount--;
            }
        }
        result.add(str.substring(startPos).trim());
        return result;
    }

    // create a type representing "? extends X"
    private static Type wildExtendsType(Type bound) throws Exception {
        return Types.subtypeOf(bound);
    }

    // create a type representing "? super X"
    private static Type wildSuperType(Type bound) throws Exception {
        return Types.supertypeOf(bound);
    }

    // create a type representing "base<args...>"
    private static ParameterizedType newParameterizedType(final Class<?> base, Type... args) throws Exception {
        return Types.newParameterizedType(base, args);
    }

}
