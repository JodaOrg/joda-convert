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
/*
 * Copied from Guava and altered. Original copyright:
 * 
 * Copyright (C) 2011 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.joda.convert;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Utilities for working with {@link Type}.
 */
final class Types {

    /** Returns the array type of {@code componentType}. */
    static Type newArrayType(Type componentType) {
        if (componentType instanceof WildcardType) {
            WildcardType wildcard = (WildcardType) componentType;
            Type[] lowerBounds = wildcard.getLowerBounds();
            checkArgument(lowerBounds.length <= 1, "Wildcard cannot have more than one lower bounds.");
            if (lowerBounds.length == 1) {
                return supertypeOf(newArrayType(lowerBounds[0]));
            } else {
                Type[] upperBounds = wildcard.getUpperBounds();
                checkArgument(upperBounds.length == 1, "Wildcard should have only one upper bound.");
                return subtypeOf(newArrayType(upperBounds[0]));
            }
        }
        return JavaVersion.CURRENT.newArrayType(componentType);
    }

    /**
     * Returns a type where {@code rawType} is parameterized by {@code arguments} and is owned by
     * {@code ownerType}.
     */
    static ParameterizedType newParameterizedTypeWithOwner(
            Type ownerType, Class<?> rawType, Type... arguments) {
        if (ownerType == null) {
            return newParameterizedType(rawType, arguments);
        }
        // ParameterizedTypeImpl constructor already checks, but we want to throw NPE before IAE
        checkNotNull(arguments);
        checkArgument(rawType.getEnclosingClass() != null, "Owner type for unenclosed %s", rawType);
        return new ParameterizedTypeImpl(ownerType, rawType, arguments);
    }

    /**
     * Returns a type where {@code rawType} is parameterized by {@code arguments}.
     */
    static ParameterizedType newParameterizedType(Class<?> rawType, Type... arguments) {
        return new ParameterizedTypeImpl(
                ClassOwnership.JVM_BEHAVIOR.getOwnerType(rawType), rawType, arguments);
    }

    /** Decides what owner type to use for constructing {@link ParameterizedType} from a raw class. */
    private enum ClassOwnership {
        OWNED_BY_ENCLOSING_CLASS {

            @Override
            Class<?> getOwnerType(Class<?> rawType) {
                return rawType.getEnclosingClass();
            }
        },
        LOCAL_CLASS_HAS_NO_OWNER {

            @Override
            Class<?> getOwnerType(Class<?> rawType) {
                if (rawType.isLocalClass()) {
                    return null;
                } else {
                    return rawType.getEnclosingClass();
                }
            }
        };

        abstract Class<?> getOwnerType(Class<?> rawType);

        static final ClassOwnership JVM_BEHAVIOR = detectJvmBehavior();

        private static ClassOwnership detectJvmBehavior() {
            class LocalClass<T> {
            }
            LocalClass<String> localClass = new LocalClass<String>() {};  // CSIGNORE
            Class<?> subclass = localClass.getClass();
            ParameterizedType parameterizedType = (ParameterizedType) subclass.getGenericSuperclass();
            for (ClassOwnership behavior : ClassOwnership.values()) {
                if (behavior.getOwnerType(LocalClass.class) == parameterizedType.getOwnerType()) {
                    return behavior;
                }
            }
            throw new AssertionError();
        }
    }

    /**
     * Returns a new {@link TypeVariable} that belongs to {@code declaration} with {@code name} and
     * {@code bounds}.
     */
    static <D extends GenericDeclaration> TypeVariable<D> newArtificialTypeVariable(
            D declaration, String name, Type... bounds) {
        return newTypeVariableImpl(
                declaration, name, (bounds.length == 0) ? new Type[] {Object.class} : bounds);
    }

    /** Returns a new {@link WildcardType} with {@code upperBound}. */
    static WildcardType subtypeOf(Type upperBound) {
        return new WildcardTypeImpl(new Type[0], new Type[] {upperBound});
    }

    /** Returns a new {@link WildcardType} with {@code lowerBound}. */
    static WildcardType supertypeOf(Type lowerBound) {
        return new WildcardTypeImpl(new Type[] {lowerBound}, new Type[] {Object.class});
    }

    /**
     * Returns human readable string representation of {@code type}.
     * <ul>
     * <li>For array type {@code Foo[]}, {@code "com.mypackage.Foo[]"} are returned.
     * <li>For any class, {@code theClass.getName()} are returned.
     * <li>For all other types, {@code type.toString()} are returned.
     * </ul>
     */
    static String toString(Type type) {
        return (type instanceof Class) ? ((Class<?>) type).getName() : type.toString();
    }

    static Type getComponentType(Type type) {
        checkNotNull(type);
        final AtomicReference<Type> result = new AtomicReference<Type>();
        new TypeVisitor() {
            @Override
            void visitTypeVariable(TypeVariable<?> t) {
                result.set(subtypeOfComponentType(t.getBounds()));
            }

            @Override
            void visitWildcardType(WildcardType t) {
                result.set(subtypeOfComponentType(t.getUpperBounds()));
            }

            @Override
            void visitGenericArrayType(GenericArrayType t) {
                result.set(t.getGenericComponentType());
            }

            @Override
            void visitClass(Class<?> t) {
                result.set(t.getComponentType());
            }
        }.visit(type);
        return result.get();
    }

    /**
     * Visitor pattern to evaluate {@code Type}.
     */
    private abstract static class TypeVisitor {

        private final Set<Type> visited = new HashSet<Type>();

        /**
         * Visits the given types. Null types are ignored. This allows subclasses to call
         * {@code visit(parameterizedType.getOwnerType())} safely without having to check nulls.
         */
        public final void visit(Type... types) {
            for (Type type : types) {
                if (type == null || !visited.add(type)) {
                    // null owner type, or already visited;
                    continue;
                }
                boolean succeeded = false;
                try {
                    if (type instanceof TypeVariable) {
                        visitTypeVariable((TypeVariable<?>) type);
                    } else if (type instanceof WildcardType) {
                        visitWildcardType((WildcardType) type);
                    } else if (type instanceof ParameterizedType) {
                        visitParameterizedType((ParameterizedType) type);
                    } else if (type instanceof Class) {
                        visitClass((Class<?>) type);
                    } else if (type instanceof GenericArrayType) {
                        visitGenericArrayType((GenericArrayType) type);
                    } else {
                        throw new AssertionError("Unknown type: " + type);
                    }
                    succeeded = true;
                } finally {
                    if (!succeeded) { // When the visitation failed, we don't want to ignore the second.
                        visited.remove(type);
                    }
                }
            }
        }

        void visitClass(Class<?> t) {
        }

        void visitGenericArrayType(GenericArrayType t) {
        }

        void visitParameterizedType(ParameterizedType t) {
        }

        void visitTypeVariable(TypeVariable<?> t) {
        }

        void visitWildcardType(WildcardType t) {
        }
    }

    /**
     * Returns {@code ? extends X} if any of {@code bounds} is a subtype of {@code X[]}; or null
     * otherwise.
     */

    private static Type subtypeOfComponentType(Type[] bounds) {
        for (Type bound : bounds) {
            Type componentType = getComponentType(bound);
            if (componentType != null) {
                // Only the first bound can be a class or array.
                // Bounds after the first can only be interfaces.
                if (componentType instanceof Class) {
                    Class<?> componentClass = (Class<?>) componentType;
                    if (componentClass.isPrimitive()) {
                        return componentClass;
                    }
                }
                return subtypeOf(componentType);
            }
        }
        return null;
    }

    private static final class GenericArrayTypeImpl implements GenericArrayType, Serializable {

        private final Type componentType;

        GenericArrayTypeImpl(Type componentType) {
            this.componentType = JavaVersion.CURRENT.usedInGenericType(componentType);
        }

        @Override
        public Type getGenericComponentType() {
            return componentType;
        }

        @Override
        public String toString() {
            return Types.toString(componentType) + "[]";
        }

        @Override
        public int hashCode() {
            return componentType.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof GenericArrayType) {
                GenericArrayType that = (GenericArrayType) obj;
                return equal(getGenericComponentType(), that.getGenericComponentType());
            }
            return false;
        }

        private static final long serialVersionUID = 0;
    }

    private static final class ParameterizedTypeImpl implements ParameterizedType, Serializable {

        private final Type ownerType;
        private final List<Type> argumentsList;
        private final Class<?> rawType;

        ParameterizedTypeImpl(Type ownerType, Class<?> rawType, Type[] typeArguments) {
            checkNotNull(rawType);
            checkArgument(typeArguments.length == rawType.getTypeParameters().length);
            disallowPrimitiveType(typeArguments, "type parameter");
            this.ownerType = ownerType;
            this.rawType = rawType;
            this.argumentsList = JavaVersion.CURRENT.usedInGenericType(typeArguments);
        }

        @Override
        public Type[] getActualTypeArguments() {
            return toArray(argumentsList);
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return ownerType;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (ownerType != null && JavaVersion.CURRENT.jdkTypeDuplicatesOwnerName()) {
                builder.append(JavaVersion.CURRENT.typeName(ownerType)).append('.');
            }
            String str = "";
            if (argumentsList == null) {
                str = "null";
            } else {
                for (int i = 0; i < argumentsList.size(); i++) {
                    if (i > 0) {
                        str += ", ";
                    }
                    str += JavaVersion.CURRENT.typeName(argumentsList.get(i));
                }
            }

            return builder
                    .append(rawType.getName())
                    .append('<')
                    .append(str)
                    .append('>')
                    .toString();
        }

        @Override
        public int hashCode() {
            return (ownerType == null ? 0 : ownerType.hashCode()) ^ argumentsList.hashCode() ^ rawType.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof ParameterizedType)) {
                return false;
            }
            ParameterizedType that = (ParameterizedType) other;
            return getRawType().equals(that.getRawType()) && equal(getOwnerType(), that.getOwnerType()) &&
                    Arrays.equals(getActualTypeArguments(), that.getActualTypeArguments());
        }

        private static final long serialVersionUID = 0;
    }

    private static <D extends GenericDeclaration> TypeVariable<D> newTypeVariableImpl(
            D genericDeclaration, String name, Type[] bounds) {
        TypeVariableImpl<D> typeVariableImpl =
                new TypeVariableImpl<D>(genericDeclaration, name, bounds);
        @SuppressWarnings("unchecked")
        TypeVariable<D> typeVariable = newProxy(TypeVariable.class, new TypeVariableInvocationHandler(typeVariableImpl));
        return typeVariable;
    }

    // from Guava `Reflection` class
    private static <T> T newProxy(Class<T> interfaceType, InvocationHandler handler) {
        checkNotNull(handler);
        checkArgument(interfaceType.isInterface(), "%s is not an interface", interfaceType);
        Object object =
                Proxy.newProxyInstance(
                        interfaceType.getClassLoader(), new Class<?>[] {interfaceType}, handler);
        return interfaceType.cast(object);
    }

    /**
     * Invocation handler to work around a compatibility problem between Java 7 and Java 8.
     *
     * <p>Java 8 introduced a new method {@code getAnnotatedBounds()} in the {@link TypeVariable}
     * interface, whose return type {@code AnnotatedType[]} is also new in Java 8. That means that we
     * cannot implement that interface in source code in a way that will compile on both Java 7 and
     * Java 8. If we include the {@code getAnnotatedBounds()} method then its return type means it
     * won't compile on Java 7, while if we don't include the method then the compiler will complain
     * that an abstract method is unimplemented. So instead we use a dynamic proxy to get an
     * implementation. If the method being called on the {@code TypeVariable} instance has the same
     * name as one of the public methods of {@link TypeVariableImpl}, the proxy calls the same method
     * on its instance of {@code TypeVariableImpl}. Otherwise it throws
     * {@link UnsupportedOperationException}; this should only apply to {@code getAnnotatedBounds()}.
     * This does mean that users on Java 8 who obtain an instance of {@code TypeVariable} from
     * {@code TypeResolver#resolveType} will not be able to call {@code getAnnotatedBounds()} on it,
     * but that should hopefully be rare.
     *
     * <p>This workaround should be removed at a distant future time when we no longer support Java
     * versions earlier than 8.
     */
    private static final class TypeVariableInvocationHandler implements InvocationHandler {
        private static final Map<String, Method> typeVariableMethods;

        static {
            Map<String, Method> builder = new LinkedHashMap<String, Method>();
            for (Method method : TypeVariableImpl.class.getMethods()) {
                if (method.getDeclaringClass().equals(TypeVariableImpl.class)) {
                    try {
                        method.setAccessible(true);
                    } catch (AccessControlException e) {
                        // OK: the method is accessible to us anyway. The setAccessible call is only for
                        // unusual execution environments where that might not be true.
                    }
                    builder.put(method.getName(), method);
                }
            }
            typeVariableMethods = builder;
        }

        private final TypeVariableImpl<?> typeVariableImpl;

        TypeVariableInvocationHandler(TypeVariableImpl<?> typeVariableImpl) {
            this.typeVariableImpl = typeVariableImpl;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            Method typeVariableMethod = typeVariableMethods.get(methodName);
            if (typeVariableMethod == null) {
                throw new UnsupportedOperationException(methodName);
            } else {
                try {
                    return typeVariableMethod.invoke(typeVariableImpl, args);
                } catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            }
        }
    }

    private static final class TypeVariableImpl<D extends GenericDeclaration> {

        private final D genericDeclaration;
        private final String name;
        private final List<Type> bounds;

        TypeVariableImpl(D genericDeclaration, String name, Type[] bounds) {
            disallowPrimitiveType(bounds, "bound for type variable");
            this.genericDeclaration = checkNotNull(genericDeclaration);
            this.name = checkNotNull(name);
            this.bounds = new ArrayList<Type>(Arrays.asList(bounds));
        }

        public D getGenericDeclaration() {
            return genericDeclaration;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public int hashCode() {
            return genericDeclaration.hashCode() ^ name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (NativeTypeVariableEquals.NATIVE_TYPE_VARIABLE_ONLY) {
                // equal only to our TypeVariable implementation with identical bounds
                if (obj != null && Proxy.isProxyClass(obj.getClass()) &&
                        Proxy.getInvocationHandler(obj) instanceof TypeVariableInvocationHandler) {
                    TypeVariableInvocationHandler typeVariableInvocationHandler =
                            (TypeVariableInvocationHandler) Proxy.getInvocationHandler(obj);
                    TypeVariableImpl<?> that = typeVariableInvocationHandler.typeVariableImpl;
                    return name.equals(that.getName()) && genericDeclaration.equals(that.getGenericDeclaration()) &&
                            bounds.equals(that.bounds);
                }
                return false;
            } else {
                // equal to any TypeVariable implementation regardless of bounds
                if (obj instanceof TypeVariable) {
                    TypeVariable<?> that = (TypeVariable<?>) obj;
                    return name.equals(that.getName()) && genericDeclaration.equals(that.getGenericDeclaration());
                }
                return false;
            }
        }
    }

    static final class WildcardTypeImpl implements WildcardType, Serializable {

        private final List<Type> lowerBounds;
        private final List<Type> upperBounds;

        WildcardTypeImpl(Type[] lowerBounds, Type[] upperBounds) {
            disallowPrimitiveType(lowerBounds, "lower bound for wildcard");
            disallowPrimitiveType(upperBounds, "upper bound for wildcard");
            this.lowerBounds = JavaVersion.CURRENT.usedInGenericType(lowerBounds);
            this.upperBounds = JavaVersion.CURRENT.usedInGenericType(upperBounds);
        }

        @Override
        public Type[] getLowerBounds() {
            return toArray(lowerBounds);
        }

        @Override
        public Type[] getUpperBounds() {
            return toArray(upperBounds);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof WildcardType) {
                WildcardType that = (WildcardType) obj;
                return lowerBounds.equals(Arrays.asList(that.getLowerBounds())) &&
                        upperBounds.equals(Arrays.asList(that.getUpperBounds()));
            }
            return false;
        }

        @Override
        public int hashCode() {
            return lowerBounds.hashCode() ^ upperBounds.hashCode();
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder("?");
            for (Type lowerBound : lowerBounds) {
                builder.append(" super ").append(JavaVersion.CURRENT.typeName(lowerBound));
            }
            for (Type upperBound : filterUpperBounds(upperBounds)) {
                builder.append(" extends ").append(JavaVersion.CURRENT.typeName(upperBound));
            }
            return builder.toString();
        }

        private static final long serialVersionUID = 0;
    }

    private static Type[] toArray(Collection<Type> types) {
        return types.toArray(new Type[types.size()]);
    }

    private static List<Type> filterUpperBounds(List<Type> bounds) {
        List<Type> filtered = new ArrayList<Type>();
        for (Type type : bounds) {
            if (!type.equals(Object.class)) {
                filtered.add(type);
            }
        }
        return filtered;
    }

    private static void disallowPrimitiveType(Type[] types, String usedAs) {
        for (Type type : types) {
            if (type instanceof Class) {
                Class<?> cls = (Class<?>) type;
                checkArgument(!cls.isPrimitive(), "Primitive type '%s' used as %s", cls, usedAs);
            }
        }
    }

    /** Returns the {@code Class} object of arrays with {@code componentType}. */
    static Class<?> getArrayClass(Class<?> componentType) {
        // TODO(user): This is not the most efficient way to handle generic
        // arrays, but is there another way to extract the array class in a
        // non-hacky way (i.e. using String value class names- "[L...")?
        return Array.newInstance(componentType, 0).getClass();
    }

    // TODO(benyu): Once behavior is the same for all Java versions we support, delete this.
    enum JavaVersion {
        JAVA6 {
            @Override
            GenericArrayType newArrayType(Type componentType) {
                return new GenericArrayTypeImpl(componentType);
            }

            @Override
            Type usedInGenericType(Type type) {
                checkNotNull(type);
                if (type instanceof Class) {
                    Class<?> cls = (Class<?>) type;
                    if (cls.isArray()) {
                        return new GenericArrayTypeImpl(cls.getComponentType());
                    }
                }
                return type;
            }
        },
        JAVA7 {
            @Override
            Type newArrayType(Type componentType) {
                if (componentType instanceof Class) {
                    return getArrayClass((Class<?>) componentType);
                } else {
                    return new GenericArrayTypeImpl(componentType);
                }
            }

            @Override
            Type usedInGenericType(Type type) {
                return checkNotNull(type);
            }
        },
        JAVA8 {
            @Override
            Type newArrayType(Type componentType) {
                return JAVA7.newArrayType(componentType);
            }

            @Override
            Type usedInGenericType(Type type) {
                return JAVA7.usedInGenericType(type);
            }

            @Override
            String typeName(Type type) {
                try {
                    Method getTypeName = Type.class.getMethod("getTypeName");
                    return (String) getTypeName.invoke(type);
                } catch (NoSuchMethodException e) {
                    throw new AssertionError("Type.getTypeName should be available in Java 8");
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        },
        JAVA9 {
            @Override
            Type newArrayType(Type componentType) {
                return JAVA8.newArrayType(componentType);
            }

            @Override
            Type usedInGenericType(Type type) {
                return JAVA8.usedInGenericType(type);
            }

            @Override
            String typeName(Type type) {
                return JAVA8.typeName(type);
            }

            @Override
            boolean jdkTypeDuplicatesOwnerName() {
                return false;
            }
        };

        static final JavaVersion CURRENT;

        static {
            if (AnnotatedElement.class.isAssignableFrom(TypeVariable.class)) {
                if (new TypeCapture<Map.Entry<String, int[][]>>() {}.capture()  // CSIGNORE
                        .toString()
                        .contains("java.util.Map.java.util.Map")) {
                    CURRENT = JAVA8;
                } else {
                    CURRENT = JAVA9;
                }
            } else if (new TypeCapture<int[]>() {}.capture() instanceof Class) {  // CSIGNORE
                CURRENT = JAVA7;
            } else {
                CURRENT = JAVA6;
            }
        }

        abstract Type newArrayType(Type componentType);

        abstract Type usedInGenericType(Type type);

        String typeName(Type type) {
            return Types.toString(type);
        }

        boolean jdkTypeDuplicatesOwnerName() {
            return true;
        }

        List<Type> usedInGenericType(Type[] types) {
            List<Type> builder = new ArrayList<Type>();
            for (Type type : types) {
                builder.add(usedInGenericType(type));
            }
            return builder;
        }
    }

    /**
     * Per <a href="https://code.google.com/p/guava-libraries/issues/detail?id=1635">issue 1635</a>,
     * In JDK 1.7.0_51-b13, {@code TypeVariableImpl#equals(Object)} is changed to no longer be equal
     * to custom TypeVariable implementations. As a result, we need to make sure our TypeVariable
     * implementation respects symmetry. Moreover, we don't want to reconstruct a native type variable
     * {@code <A>} using our implementation unless some of its bounds have changed in resolution. This
     * avoids creating unequal TypeVariable implementation unnecessarily. When the bounds do change,
     * however, it's fine for the synthetic TypeVariable to be unequal to any native TypeVariable
     * anyway.
     */
    static final class NativeTypeVariableEquals<X> {
        static final boolean NATIVE_TYPE_VARIABLE_ONLY =
                !NativeTypeVariableEquals.class.getTypeParameters()[0]
                        .equals(newArtificialTypeVariable(NativeTypeVariableEquals.class, "X"));
    }

    //-----------------------------------------------------------------------
    // utilities
    private static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    private static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    private static void checkArgument(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    static void checkArgument(
            boolean expression,
            String errorMessageTemplate,
            Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalArgumentException(format(errorMessageTemplate, errorMessageArgs));
        }
    }

    private static String format(String template, Object... args) {
        template = String.valueOf(template); // null -> "null"

        args = args == null ? new Object[] {"(Object[])null"} : args;

        // start substituting the arguments into the '%s' placeholders
        StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
        int templateStart = 0;
        int i = 0;
        while (i < args.length) {
            int placeholderStart = template.indexOf("%s", templateStart);
            if (placeholderStart == -1) {
                break;
            }
            builder.append(template, templateStart, placeholderStart);
            builder.append(args[i++]);
            templateStart = placeholderStart + 2;
        }
        builder.append(template, templateStart, template.length());

        // if we run out of placeholders, append the extra args in square braces
        if (i < args.length) {
            builder.append(" [");
            builder.append(args[i++]);
            while (i < args.length) {
                builder.append(", ");
                builder.append(args[i++]);
            }
            builder.append(']');
        }

        return builder.toString();
    }

    private static boolean equal(Object obj1, Object obj2) {
        return obj1 == obj2 || (obj1 != null && obj1.equals(obj2));
    }

    private Types() {
    }
}
