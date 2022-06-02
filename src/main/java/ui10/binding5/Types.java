// com.flyordie.reflect.Types

package ui10.binding5;

// import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

// import static com.flyordie.reflect.ReflectionUtil.typeToString;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static ui10.binding5.ReflectionUtil.typeToString;

public class Types {

    private static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS;

    static {
        Map<Class<?>, Class<?>> m = new HashMap<>();
        m.put(boolean.class, Boolean.class);
        m.put(byte.class, Byte.class);
        m.put(char.class, Character.class);
        m.put(double.class, Double.class);
        m.put(float.class, Float.class);
        m.put(int.class, Integer.class);
        m.put(long.class, Long.class);
        m.put(short.class, Short.class);
        m.put(void.class, Void.class);
        PRIMITIVES_TO_WRAPPERS = m;
    }

    public interface TypeVisitor<R> {

        R visitDeclaredClass(Class<?> clazz);

        R visitArray(Class<?> arrayClass);

        R visitGenericArrayType(GenericArrayType genericArrayType);

        R visitParameterizedType(ParameterizedType parameterizedType);

        R visitWildcardType(WildcardType wildcardType);

        R visitTypeVariable(TypeVariable<?> typeVariable); // legyen inkább method type var a D?

        static <R> R visit(Type type, TypeVisitor<R> visitor) {
            if (type instanceof Class) {
                Class<?> c = (Class<?>) type;
                if (c.isArray())
                    return visitor.visitArray(c);
                else
                    return visitor.visitDeclaredClass(c);
            }

            if (type instanceof GenericArrayType)
                return visitor.visitGenericArrayType((GenericArrayType) type);

            if (type instanceof ParameterizedType)
                return visitor.visitParameterizedType((ParameterizedType) type);

            if (type instanceof WildcardType)
                return visitor.visitWildcardType((WildcardType) type);

            if (type instanceof TypeVariable<?>)
                return visitor.visitTypeVariable((TypeVariable<?>) type);

            throw new IllegalArgumentException("unknown type: " + type);
        }
    }

    public static abstract class AbstractType implements Type {
    }

    public static class GenericArrayTypeImpl extends AbstractType implements GenericArrayType {

        private final Type componentType;

        public GenericArrayTypeImpl(Type componentType) {
            this.componentType = componentType;
        }

        @Override
        public Type getGenericComponentType() {
            return componentType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof GenericArrayType)) return false;
            return getGenericComponentType().equals(((GenericArrayType) o).getGenericComponentType());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getGenericComponentType());
        }

        @Override
        public String toString() {
            return componentType.getTypeName() + "[]";
        }
    }

    public static class ParameterizedTypeImpl extends AbstractType implements ParameterizedType {

        private final Type[] actualTypeArguments;
        private final Class<?> rawType;
        private final Type ownerType;

        public ParameterizedTypeImpl(Type[] actualTypeArguments, Class<?> rawType, Type ownerType) {
            if (actualTypeArguments.length != rawType.getTypeParameters().length)
                throw new IllegalArgumentException("type param count mismatch: " +
                        Arrays.toString(actualTypeArguments) + " for " + rawType.toGenericString());

            this.actualTypeArguments = actualTypeArguments;
            this.rawType = rawType;
            this.ownerType = ownerType;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return Arrays.copyOf(actualTypeArguments, actualTypeArguments.length);
        }

        @Override
        public Class<?> getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return ownerType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ParameterizedType)) return false;
            ParameterizedType that = (ParameterizedType) o;
            return Arrays.equals(getActualTypeArguments(), that.getActualTypeArguments())
                    && getRawType().equals(that.getRawType())
                    && Objects.equals(getOwnerType(), that.getOwnerType());
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(getRawType(), getOwnerType());
            result = 31 * result + Arrays.hashCode(getActualTypeArguments());
            return result;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            if (ownerType != null)
                sb.append(ownerType.getTypeName()).append('$').append(rawType.getSimpleName());
            else
                sb.append(rawType.getTypeName());

            StringJoiner sj = new StringJoiner(", ", "<", ">");
            sj.setEmptyValue("");
            for (Type t : actualTypeArguments)
                sj.add(t.getTypeName());
            sb.append(sj);

            return sb.toString();
        }
    }

    public static class WildcardTypeImpl extends AbstractType implements WildcardType {

        private final Type[] upperBounds;
        private final Type[] lowerBounds;

        // lowerBounds legyen Type[] helyett Type?
        public WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
            if (upperBounds.length == 0)
                throw new IllegalArgumentException("can't have no upper bounds: " +
                        Arrays.toString(upperBounds)
                        + ", " + Arrays.toString(lowerBounds));

            if (lowerBounds.length > 0 && (upperBounds.length != 1 || upperBounds[0] != Object.class))
                throw new IllegalArgumentException("can't have both lower and upper bounds: "
                        + Arrays.toString(upperBounds)
                        + ", " + Arrays.toString(lowerBounds));

            if (lowerBounds.length > 1)
                throw new IllegalArgumentException("more than one lower bounds: "
                        + Arrays.toString(upperBounds)
                        + ", " + Arrays.toString(lowerBounds));

            this.upperBounds = upperBounds;
            this.lowerBounds = lowerBounds;
        }

        @Override
        public Type[] getUpperBounds() {
            return Arrays.copyOf(upperBounds, upperBounds.length);
        }

        @Override
        public Type[] getLowerBounds() {
            return Arrays.copyOf(lowerBounds, lowerBounds.length);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof WildcardType)) return false;
            WildcardType that = (WildcardType) o;
            return Arrays.equals(getUpperBounds(), that.getUpperBounds())
                    && Arrays.equals(getLowerBounds(), that.getLowerBounds());
        }

        @Override
        public int hashCode() {
            int result = Arrays.hashCode(getUpperBounds());
            result = 31 * result + Arrays.hashCode(getLowerBounds());
            return result;
        }

        @Override
        public String toString() {
            Type[] bounds = lowerBounds;
            StringBuilder sb = new StringBuilder();

            if (lowerBounds.length > 0)
                sb.append("? super ");
            else {
                if (upperBounds.length > 0 && !upperBounds[0].equals(Object.class)) {
                    bounds = upperBounds;
                    sb.append("? extends ");
                } else
                    return "?";
            }

            StringJoiner sj = new StringJoiner(" & ");
            for (Type bound : bounds)
                sj.add(bound.getTypeName());
            sb.append(sj);

            return sb.toString();
        }
    }

    public static AnnotatedType getAnnotatedOwnerType(AnnotatedType annotatedType) {
        // Java 9-től van csak getAnnotatedOwnerType
        // de amúgy már Java 11-en vagyunk, tehát már használhatnánk

        if (annotatedType instanceof AbstractAnnotatedType)
            return ((AbstractAnnotatedType) annotatedType).getAnnotatedOwnerType();

        return AnnotatedTypeVisitor.visit(annotatedType, new AnnotatedTypeVisitor<AnnotatedType, RuntimeException>() {
            @Override
            public AnnotatedType visitClass(AnnotatedType type, Class<?> clazz)  {
                return AnnotatedClassImpl.ownerType(clazz);
            }

            @Override
            public AnnotatedType visitParameterizedType(AnnotatedParameterizedType type, Class<?> rawType)  {
                return AnnotatedClassImpl.ownerType(rawType);
            }

            @Override
            public AnnotatedType visitTypeVariable(AnnotatedTypeVariable type)  {
                return null;
            }

            @Override
            public AnnotatedType visitArrayType(AnnotatedArrayType type)  {
                return null;
            }

            @Override
            public AnnotatedType visitWildcardType(AnnotatedWildcardType type)  {
                return null;
            }
        });
    }

    public interface AnnotatedTypeVisitor<R, E extends Throwable> {

        R visitClass(AnnotatedType type, Class<?> clazz) throws E;

        R visitParameterizedType(AnnotatedParameterizedType type, Class<?> rawType) throws E;

        R visitTypeVariable(AnnotatedTypeVariable type) throws E;

        R visitArrayType(AnnotatedArrayType type) throws E;

        R visitWildcardType(AnnotatedWildcardType type) throws E;

        static <R, E extends Throwable> R visit(AnnotatedType annotatedType, AnnotatedTypeVisitor<R, E> visitor) throws E {
            if (annotatedType instanceof AnnotatedParameterizedType)
                return visitor.visitParameterizedType((AnnotatedParameterizedType) annotatedType,
                        (Class<?>) ((ParameterizedType) annotatedType.getType()).getRawType());
            else if (annotatedType instanceof AnnotatedWildcardType)
                return visitor.visitWildcardType((AnnotatedWildcardType) annotatedType);
            else if (annotatedType instanceof AnnotatedTypeVariable)
                return visitor.visitTypeVariable((AnnotatedTypeVariable) annotatedType);
            else if (annotatedType instanceof AnnotatedArrayType)
                return visitor.visitArrayType((AnnotatedArrayType) annotatedType);
            else if (annotatedType.getType() instanceof Class)
                return visitor.visitClass(annotatedType, (Class<?>) annotatedType.getType());
            else
                throw new IllegalArgumentException("unknown annotated type: " + annotatedType);
        }
    }

    public static abstract class AbstractAnnotatedType implements AnnotatedType {

        protected final Annotation[] annotations;

        public AbstractAnnotatedType(Annotation[] annotations) {
            this.annotations = annotations;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
            for (Annotation ann : annotations)
                if (ann.annotationType() == annotationClass)
                    return (A) ann;
            return null;
        }

        @Override
        public Annotation[] getAnnotations() {
            return getDeclaredAnnotations();
        }

        @Override
        public Annotation[] getDeclaredAnnotations() {
            return Arrays.copyOf(annotations, annotations.length);
        }

        // Java 8-ban nincs ilyen még, csak 9-től, ezért nem tudunk @Override-ot tenni ide
        public AnnotatedType getAnnotatedOwnerType() { // Java 9-től
            return null;
        }

        protected String annotationsToString(boolean leadingSpace) {
            if (annotations != null && annotations.length > 0) {
                StringBuilder sb = new StringBuilder();

                sb.append(Stream.of(annotations).
                        map(Annotation::toString).
                        collect(joining(" ")));

                if (leadingSpace)
                    sb.insert(0, " ");
                else
                    sb.append(" ");

                return sb.toString();
            } else {
                return "";
            }
        }

        protected boolean annotationsEquals(Object other) {
            // getDeclaredAnnotations kéne inkább?
            return Arrays.equals(annotations, ((AnnotatedType) other).getAnnotations());
        }

        protected int annotationsHashCode() {
            return Arrays.hashCode(annotations);
        }
    }

    // reméljük, hogy az AnnotatedType::getAnnotations és az AnnotatedType::getDeclaredAnnotations
    // ugyanaz marad hosszabb távon is
    public static class AnnotatedClassImpl extends AbstractAnnotatedType {

        private final AnnotatedType annotatedOwnerType;
        private final Class<?> clazz;

        public AnnotatedClassImpl(AnnotatedType annotatedOwnerType, Class<?> clazz, Annotation[] annotations) {
            super(annotations);
            this.annotatedOwnerType = annotatedOwnerType;
            this.clazz = clazz;
        }

        // ez nem tudja kezelni a tömböket!
        public static AnnotatedClassImpl of(Class<?> clazz) {
            return new AnnotatedClassImpl(ownerType(clazz), clazz, new Annotation[0]);
        }

        public static AnnotatedClassImpl ownerType(Class<?> clazz) {
            if (clazz.getDeclaringClass() == null)
                return null;
            else
                return AnnotatedClassImpl.of(clazz.getDeclaringClass());
        }

        @Override
        public Type getType() {
            return clazz;
        }

        @Override
        public AnnotatedType getAnnotatedOwnerType() {
            return annotatedOwnerType;
        }

        @Override
        public String toString() {
            return annotationsToString(true) + clazz.getName();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AnnotatedType)) return false;
            return clazz == ((AnnotatedType) o).getType() && annotationsEquals(o);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clazz, annotationsHashCode());
        }
    }

    public static class AnnotatedParameterizedTypeImpl extends AbstractAnnotatedType
            implements AnnotatedParameterizedType {

        private final AnnotatedType[] typeArgs;
        private final AnnotatedType ownerType;
        private final Class<?> rawType;

        public AnnotatedParameterizedTypeImpl(AnnotatedType ownerType, Class<?> rawType,
                                              AnnotatedType[] typeArgs, Annotation[] annotations) {
            super(annotations);

            Objects.requireNonNull(typeArgs);
            Objects.requireNonNull(annotations);

            for (AnnotatedType t : typeArgs)
                Objects.requireNonNull(t);
            for (Annotation a : annotations)
                Objects.requireNonNull(a);

            this.ownerType = ownerType;
            this.rawType = rawType;
            this.typeArgs = typeArgs;
        }

        public static AnnotatedParameterizedTypeImpl of(Class<?> rawType, AnnotatedType... params) {
            return new AnnotatedParameterizedTypeImpl(AnnotatedClassImpl.ownerType(rawType), rawType, params, NO_ANNOTATIONS);
        }

        @Override
        public AnnotatedType[] getAnnotatedActualTypeArguments() {
            return Arrays.copyOf(typeArgs, typeArgs.length);
        }

        @Override
        public AnnotatedType getAnnotatedOwnerType() {
            return ownerType;
        }

        @Override
        public ParameterizedType getType() {
            Type[] t = new Type[typeArgs.length];
            for (int i = 0; i < t.length; i++)
                t[i] = typeArgs[i].getType();
            return new ParameterizedTypeImpl(t, rawType, ownerType == null ? null : ownerType.getType());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AnnotatedParameterizedType)) return false;
            AnnotatedParameterizedType that = (AnnotatedParameterizedType) o;
            return Arrays.equals(typeArgs, that.getAnnotatedActualTypeArguments())
                    && Objects.equals(ownerType, Types.getAnnotatedOwnerType(that))
                    && rawType == ((ParameterizedType) that.getType()).getRawType();
        }

        @Override
        public int hashCode() {
            return Objects.hash(ownerType, rawType, Arrays.hashCode(typeArgs));
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append(annotationsToString(false));
            sb.append(rawType.getTypeName());

            AnnotatedType[] typeArgs = getAnnotatedActualTypeArguments();
            if (typeArgs.length > 0) {
                sb.append(Stream.of(typeArgs).map(AnnotatedType::toString).
                        collect(joining(", ", "<", ">")));
            }

            return sb.toString();
        }
    }

    public static class AnnotatedTypeVariableImpl extends AbstractAnnotatedType implements AnnotatedTypeVariable {

        private final TypeVariable<?> typeVariable;

        public AnnotatedTypeVariableImpl(TypeVariable<?> typeVariable, Annotation[] annotations) {
            super(annotations);
            this.typeVariable = requireNonNull(typeVariable);
        }

        // nem értem, hogy az itt override-olt metódusnak mi értelme van
        @Override
        public AnnotatedType[] getAnnotatedBounds() {
            return typeVariable.getAnnotatedBounds();
        }

        @Override
        public Type getType() {
            return typeVariable;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AnnotatedTypeVariable)) return false;
            AnnotatedTypeVariable that = (AnnotatedTypeVariable) o;
            return typeVariable.equals(that.getType()) && annotationsEquals(o);
        }

        @Override
        public int hashCode() {
            return Objects.hash(typeVariable, annotationsHashCode());
        }

        @Override
        public String toString() {
            return annotationsToString(true) + typeVariable.getName();
        }
    }

    public static class AnnotatedArrayTypeImpl extends AbstractAnnotatedType implements AnnotatedArrayType {

        private final AnnotatedType componentType;

        public AnnotatedArrayTypeImpl(AnnotatedType componentType, Annotation[] annotations) {
            super(annotations);
            this.componentType = requireNonNull(componentType, "componentType");
        }

        @Override
        public AnnotatedType getAnnotatedGenericComponentType() {
            return componentType;
        }

        @Override
        public Type getType() {
            Type genericComponentType = componentType.getType();
            if (genericComponentType instanceof Class)
                return Array.newInstance((Class<?>) genericComponentType, 0).getClass();
            else
                return new GenericArrayTypeImpl(genericComponentType);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AnnotatedArrayType)) return false;
            return componentType.equals(((AnnotatedArrayType) o).getAnnotatedGenericComponentType())
                    && annotationsEquals(o);
        }

        @Override
        public int hashCode() {
            return Objects.hash(componentType, annotationsHashCode());
        }

        @Override
        public String toString() {
            return componentType + (annotations.length == 0 ? "" : " " + annotationsToString(true)) + "[]";
        }
    }

    public static class AnnotatedWildcardTypeImpl extends AbstractAnnotatedType implements AnnotatedWildcardType {

        private final AnnotatedType[] upperBounds;
        private final AnnotatedType[] lowerBounds;

        public AnnotatedWildcardTypeImpl(AnnotatedType[] upperBounds, AnnotatedType[] lowerBounds,
                                         Annotation[] annotations) {
            super(annotations);
            this.upperBounds = upperBounds;
            this.lowerBounds = lowerBounds;

            for (AnnotatedType t : upperBounds)
                assert t != null;
            for (AnnotatedType t : lowerBounds)
                assert t != null;

            if (lowerBounds.length > 0 && (upperBounds.length != 1 || upperBounds[0].getType() != Object.class))
                throw new IllegalArgumentException("can't have both lower and upper bounds: "
                        + Stream.of(upperBounds).map(ReflectionUtil::typeToString).collect(joining(", ", "[", "]"))
                        + ", " + Stream.of(lowerBounds).map(ReflectionUtil::typeToString).collect(joining(", ", "[", "]"))
                        + ", " + Arrays.toString(annotations));
        }

        @Override
        public AnnotatedType[] getAnnotatedLowerBounds() {
            return Arrays.copyOf(lowerBounds, lowerBounds.length);
        }

        @Override
        public AnnotatedType[] getAnnotatedUpperBounds() {
            return Arrays.copyOf(upperBounds, upperBounds.length);
        }

        @Override
        public Type getType() {
            Type[] u = new Type[upperBounds.length];
            for (int i = 0; i < u.length; i++)
                u[i] = upperBounds[i].getType();

            Type[] l = new Type[lowerBounds.length];
            for (int i = 0; i < l.length; i++)
                l[i] = lowerBounds[i].getType();

            return new WildcardTypeImpl(u, l);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AnnotatedWildcardType)) return false;
            AnnotatedWildcardType that = (AnnotatedWildcardType) o;
            return Arrays.equals(upperBounds, that.getAnnotatedUpperBounds())
                    && Arrays.equals(lowerBounds, that.getAnnotatedLowerBounds())
                    && annotationsEquals(o);
        }

        @Override
        public int hashCode() {
            return Objects.hash(Arrays.hashCode(upperBounds), Arrays.hashCode(lowerBounds), annotationsHashCode());
        }
    }

    public static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

    public static AnnotatedType asAnnotatedType(Type type) {
        if (type instanceof Class && !((Class<?>) type).isArray())
            return new AnnotatedClassImpl(null, (Class<?>) type, NO_ANNOTATIONS);

        return asAnnotatedType(type, NO_ANNOTATIONS);
    }

    public static AnnotatedType asAnnotatedType(Type type, Annotation[] annotations) {
        return TypeVisitor.visit(type, new TypeVisitor<AnnotatedType>() {
            @Override
            public AnnotatedType visitDeclaredClass(Class<?> clazz) {
                AnnotatedType annotatedOwnerType = clazz.getEnclosingClass() == null ? null :
                        asAnnotatedType(clazz.getEnclosingClass(), NO_ANNOTATIONS);
                return new AnnotatedClassImpl(annotatedOwnerType, clazz, annotations);
            }

            @Override
            public AnnotatedType visitArray(Class<?> arrayClass) {
                return new AnnotatedArrayTypeImpl(
                        asAnnotatedType(arrayClass.getComponentType(), NO_ANNOTATIONS),
                        annotations
                );
            }

            @Override
            public AnnotatedType visitGenericArrayType(GenericArrayType genericArrayType) {
                return new AnnotatedArrayTypeImpl(
                        asAnnotatedType(genericArrayType.getGenericComponentType(), NO_ANNOTATIONS),
                        annotations
                );
            }

            @Override
            public AnnotatedType visitParameterizedType(ParameterizedType parameterizedType) {
                Type ownerType = parameterizedType.getOwnerType();

                Type[] a = parameterizedType.getActualTypeArguments();
                AnnotatedType[] typeArgs = new AnnotatedType[a.length];
                for (int i = 0; i < a.length; i++)
                    typeArgs[i] = asAnnotatedType(a[i], NO_ANNOTATIONS);

                return new AnnotatedParameterizedTypeImpl(
                        ownerType == null ? null : asAnnotatedType(ownerType, NO_ANNOTATIONS),
                        (Class<?>) parameterizedType.getRawType(),
                        typeArgs,
                        annotations
                );
            }

            @Override
            public AnnotatedType visitWildcardType(WildcardType wildcardType) {
                Type[] u = wildcardType.getUpperBounds();
                AnnotatedType[] ua = new AnnotatedType[u.length];
                for (int i = 0; i < ua.length; i++)
                    ua[i] = asAnnotatedType(u[i], NO_ANNOTATIONS);

                Type[] l = wildcardType.getUpperBounds();
                AnnotatedType[] la = new AnnotatedType[l.length];
                for (int i = 0; i < la.length; i++)
                    la[i] = asAnnotatedType(l[i], NO_ANNOTATIONS);

                return new AnnotatedWildcardTypeImpl(ua, la, annotations);
            }

            @Override
            public AnnotatedType visitTypeVariable(TypeVariable<?> typeVariable) {
                return new AnnotatedTypeVariableImpl(typeVariable, annotations);
            }
        });
    }

    public static AnnotatedType withoutAnnotation(AnnotatedType atype, Class<? extends Annotation>... annotationTypes) {
        if (annotationTypes.length == 0)
            throw new IllegalArgumentException("no annotations specified");

        Annotation[] a = atype.getAnnotations();

        int j = 0;
        loop:
        for (int i = 0; i < a.length; i++) {
            for (int k = 0; k < annotationTypes.length; k++)
                if (a[i].annotationType() == annotationTypes[k])
                    continue loop;
            a[j++] = a[i];
        }

        Annotation[] annotations = Arrays.copyOf(a, j);

        return withAnnotations(atype, annotations);
    }

    public static AnnotatedType withAdditionalAnnotations(AnnotatedType atype, Annotation... annotations) {
        int annotationCount = 0;
        for (int i = 0; i < annotations.length; i++)
            if (annotations[i] != null)
                annotationCount++;

        Annotation[] a = atype.getAnnotations();
        Annotation[] b = Arrays.copyOf(a, a.length + annotationCount);
        int j = a.length;
        for (int i = 0; i < annotations.length; i++)
            if (annotations[i] != null)
                b[j++] = annotations[i];
        return withAnnotations(atype, b);
    }

    private static AnnotatedType withAnnotations(AnnotatedType atype, Annotation[] annotations) {
        return AnnotatedTypeVisitor.visit(atype, new AnnotatedTypeVisitor<AnnotatedType, RuntimeException>() {
            @Override
            public AnnotatedType visitClass(AnnotatedType type, Class<?> clazz) {
                return new AnnotatedClassImpl(Types.getAnnotatedOwnerType(type), clazz, annotations);
            }

            @Override
            public AnnotatedType visitParameterizedType(AnnotatedParameterizedType type, Class<?> rawType) {
                return new AnnotatedParameterizedTypeImpl(Types.getAnnotatedOwnerType(type),
                        (Class<?>) ((ParameterizedType) type.getType()).getRawType(),
                        type.getAnnotatedActualTypeArguments(), annotations);
            }

            @Override
            public AnnotatedType visitTypeVariable(AnnotatedTypeVariable type) {
                return new AnnotatedTypeVariableImpl((TypeVariable<?>) type.getType(), annotations);
            }

            @Override
            public AnnotatedType visitArrayType(AnnotatedArrayType type) {
                return new AnnotatedArrayTypeImpl(type.getAnnotatedGenericComponentType(), annotations);
            }

            @Override
            public AnnotatedType visitWildcardType(AnnotatedWildcardType type) {
                return new AnnotatedWildcardTypeImpl(type.getAnnotatedUpperBounds(), type.getAnnotatedLowerBounds(), annotations);
            }
        });
    }

    public static AnnotatedParameterizedType withRawType(AnnotatedParameterizedType ptype, Class<?> newRawType) {
        return new AnnotatedParameterizedTypeImpl(getAnnotatedOwnerType(ptype), newRawType,
                ptype.getAnnotatedActualTypeArguments(), ptype.getAnnotations());
    }

    public static AnnotatedType replacePrimitiveWithWrapper(AnnotatedType t) {
        return AnnotatedTypeVisitor.visit(t, new AnnotatedTypeVisitor<AnnotatedType, RuntimeException>() {
            @Override
            public AnnotatedType visitClass(AnnotatedType type, Class<?> clazz) {
                if (clazz.isPrimitive())
                    return new AnnotatedClassImpl(getAnnotatedOwnerType(type),
                            PRIMITIVES_TO_WRAPPERS.get(clazz), type.getAnnotations());
                else
                    return type;
            }

            @Override
            public AnnotatedType visitParameterizedType(AnnotatedParameterizedType type, Class<?> rawType) {
                return type;
            }

            @Override
            public AnnotatedType visitTypeVariable(AnnotatedTypeVariable type) {
                return type;
            }

            @Override
            public AnnotatedType visitArrayType(AnnotatedArrayType type) {
                return type;
            }

            @Override
            public AnnotatedType visitWildcardType(AnnotatedWildcardType type) {
                return type;
            }
        });
    }

    private static String annotationsToString(AnnotatedType t) {
        Annotation[] a = t.getAnnotations();
        if (a.length == 0)
            return "";

        StringBuilder sb = new StringBuilder();
        for (Annotation ann : a)
            sb.append(ann + " ");
        return sb.toString();
    }

    public static String typeToShortString(AnnotatedType atype) {
        return annotationsToString(atype) + AnnotatedTypeVisitor.visit(atype, new AnnotatedTypeVisitor<String, RuntimeException>() {
            @Override
            public String visitClass(AnnotatedType type, Class<?> clazz) {
                return clazz.getSimpleName();
            }

            @Override
            public String visitParameterizedType(AnnotatedParameterizedType type, Class<?> rawType) {
                StringBuilder sb = new StringBuilder(rawType.getSimpleName() + "<");

                boolean first = true;
                for (AnnotatedType t : type.getAnnotatedActualTypeArguments()) {
                    if (first)
                        first = false;
                    else
                        sb.append(", ");

                    sb.append(typeToShortString(t));
                }
                return sb.toString();
            }

            @Override
            public String visitTypeVariable(AnnotatedTypeVariable type) {
                return type.getType().getTypeName();
            }

            @Override
            public String visitArrayType(AnnotatedArrayType type) {
                return typeToShortString(type.getAnnotatedGenericComponentType()) + "[]";
            }

            @Override
            public String visitWildcardType(AnnotatedWildcardType type) {
                AnnotatedType[] upperBounds = type.getAnnotatedUpperBounds();
                AnnotatedType[] lowerBounds = type.getAnnotatedLowerBounds();

                StringBuilder sb = new StringBuilder("?");
                if (upperBounds.length != 0) {
                    sb.append(" extends ");
                    boolean first = true;
                    for (AnnotatedType t : upperBounds) {
                        if (first)
                            first = false;
                        else
                            sb.append(" & ");

                        sb.append(typeToShortString(t));
                    }
                }
                if (lowerBounds.length != 0) {
                    sb.append(" super ");
                    boolean first = true;
                    for (AnnotatedType t : upperBounds) {
                        if (first)
                            first = false;
                        else
                            sb.append(" & ");

                        sb.append(typeToShortString(t));
                    }
                }
                return sb.toString();
            }
        });
    }

    public static AnnotatedType stripAllAnnotations(AnnotatedType annotatedType) {
        return asAnnotatedType(annotatedType.getType(), NO_ANNOTATIONS);
    }

    public static <E extends Throwable> void walk(AnnotatedType annotatedType, AnnotatedTypeVisitor<Void, E> visitor) throws E {
        AnnotatedTypeVisitor.visit(annotatedType, new AnnotatedTypeVisitor<Void, E>() {
            @Override
            public Void visitClass(AnnotatedType type, Class<?> clazz) throws E {
                visitor.visitClass(type, clazz);
                return null;
            }

            @Override
            public Void visitParameterizedType(AnnotatedParameterizedType type, Class<?> rawType) throws E {
                visitor.visitParameterizedType(type, rawType);

                for (AnnotatedType t : type.getAnnotatedActualTypeArguments())
                    AnnotatedTypeVisitor.visit(t, this);

                return null;
            }

            @Override
            public Void visitTypeVariable(AnnotatedTypeVariable type) throws E {
                visitor.visitTypeVariable(type);
                return null;
            }

            @Override
            public Void visitArrayType(AnnotatedArrayType type) throws E {
                visitor.visitArrayType(type);
                AnnotatedTypeVisitor.visit(type.getAnnotatedGenericComponentType(), this);
                return null;
            }

            @Override
            public Void visitWildcardType(AnnotatedWildcardType type) throws E {
                visitor.visitWildcardType(type);
                for (AnnotatedType upperBound : type.getAnnotatedUpperBounds())
                    AnnotatedTypeVisitor.visit(upperBound, this);
                for (AnnotatedType lowerBound : type.getAnnotatedUpperBounds())
                    AnnotatedTypeVisitor.visit(lowerBound, this);
                return null;
            }
        });
    }

    public static void collectReferencedClasses(AnnotatedType t, Consumer<Class<?>> c) {
        walk(t, new AnnotatedTypeVisitor<Void, RuntimeException>() {
            @Override
            public Void visitClass(AnnotatedType type, Class<?> clazz) {
                c.accept(clazz);
                return null;
            }

            @Override
            public Void visitParameterizedType(AnnotatedParameterizedType type, Class<?> rawType) {
                c.accept(rawType);
                return null;
            }

            @Override
            public Void visitTypeVariable(AnnotatedTypeVariable type) {
                return null;
            }

            @Override
            public Void visitArrayType(AnnotatedArrayType type) {
                return null;
            }

            @Override
            public Void visitWildcardType(AnnotatedWildcardType type) {
                return null;
            }
        });
    }

    // AnnotatedType subclassoknak nincs equals-e, ezért saját magunknak kell csinálni.
    // Majd kéne nekik szólni.
    public static boolean equals(AnnotatedType a, AnnotatedType b) {
        if (a == null || b == null)
            return a == b;
        if (!Arrays.equals(a.getAnnotations(), b.getAnnotations()))
            return false;
        if (!Types.equals(Types.getAnnotatedOwnerType(a), Types.getAnnotatedOwnerType(b)))
            return false;

        return AnnotatedTypeVisitor.visit(a, new AnnotatedTypeVisitor<Boolean, RuntimeException>() {
            @Override
            public Boolean visitClass(AnnotatedType type, Class<?> clazz) {
                return b.getType() == clazz;
            }

            @Override
            public Boolean visitParameterizedType(AnnotatedParameterizedType type, Class<?> rawType) {
                if (b instanceof AnnotatedParameterizedType) {
                    AnnotatedParameterizedType p = (AnnotatedParameterizedType) b;
                    if (!Objects.equals(((ParameterizedType) p.getType()).getRawType(), rawType))
                        return false;

                    AnnotatedType[] a1 = type.getAnnotatedActualTypeArguments();
                    AnnotatedType[] a2 = p.getAnnotatedActualTypeArguments();
                    return arrayEquals(a1, a2, a, b);
                }
                return false;
            }

            @Override
            public Boolean visitTypeVariable(AnnotatedTypeVariable type) {
                return type.getType().equals(b.getType());
            }

            @Override
            public Boolean visitArrayType(AnnotatedArrayType type) {
                if (b instanceof AnnotatedArrayType)
                    return Types.equals(type.getAnnotatedGenericComponentType(),
                            ((AnnotatedArrayType) b).getAnnotatedGenericComponentType());

                return false;
            }

            @Override
            public Boolean visitWildcardType(AnnotatedWildcardType type) {
                if (b instanceof AnnotatedWildcardType) {
                    AnnotatedWildcardType w = (AnnotatedWildcardType) b;
                    return arrayEquals(type.getAnnotatedUpperBounds(), w.getAnnotatedUpperBounds(), a, b) &&
                            arrayEquals(type.getAnnotatedLowerBounds(), w.getAnnotatedLowerBounds(), a, b);
                }
                return false;
            }

            // @Nonnull
            private boolean arrayEquals(AnnotatedType[] a1, AnnotatedType[] a2, AnnotatedType a, AnnotatedType b) {
                if (a1.length != a2.length)
                    throw new RuntimeException("different type argument count: " + typeToString(a) + ", " + typeToString(b));
                for (int i = 0; i < a1.length; i++)
                    if (!Types.equals(a1[i], a2[i]))
                        return false;

                return true;
            }
        });
    }
}
