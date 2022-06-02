// com.flyordie.reflect.Parameterization

package ui10.binding5;

import ui10.binding5.Types.*;

import java.lang.reflect.*;
import java.util.*;

import static ui10.binding5.ReflectionUtil.rawType;

public class Parameterization {

    public final Class<?> rawType;
    public final Map<Class<?>, Map<TypeVariable<? extends Class<?>>, AnnotatedType>> params;

    public Parameterization(Class<?> rawType, Map<TypeVariable<? extends Class<?>>, AnnotatedType> params) {
        this.rawType = rawType;

        this.params = new HashMap<>();
        this.params.put(rawType, params);

        if (rawType.getSuperclass() != null && rawType.getSuperclass() != Object.class)
            extractTypeArgs(rawType.getAnnotatedSuperclass(), rawType);
        for (AnnotatedType iface : rawType.getAnnotatedInterfaces())
            extractTypeArgs(iface, rawType);

        // ez így nem thread-safe, mert nincs a végén final field write
    }

    @Override
    public String toString() {
        return "Parameterization{" +
                "rawType=" + rawType +
                ", params=" + params +
                '}';
    }

    private void extractTypeArgs(AnnotatedType atype, Class<?> usedIn) {
        Objects.requireNonNull(atype, usedIn::toString);
        Class<?> clazz = rawType(atype);
        if (atype instanceof AnnotatedParameterizedType) {
            Map<TypeVariable<? extends Class<?>>, AnnotatedType> p = new HashMap<>();
            AnnotatedParameterizedType parameterizedSupertype = (AnnotatedParameterizedType) atype;
            AnnotatedType[] typeArgs = parameterizedSupertype.getAnnotatedActualTypeArguments();
            TypeVariable<? extends Class<?>>[] typeParams = clazz.getTypeParameters();
            if (typeArgs.length != typeParams.length)
                throw new RuntimeException("mismatched type variable count in " + usedIn + ": " + Arrays.toString(typeArgs) +
                        " vs " + Arrays.toString(typeParams));

            for (int i = 0; i < typeArgs.length; i++) {
                p.put(typeParams[i], parameterize(typeArgs[i], this));
            }

            this.params.put(clazz, p);
        } else {
            this.params.put(clazz, Collections.emptyMap());
        }

        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class)
            extractTypeArgs(clazz.getAnnotatedSuperclass(), clazz);
        for (AnnotatedType iface : clazz.getAnnotatedInterfaces())
            extractTypeArgs(iface, clazz);
    }

    public static Parameterization of(AnnotatedType t) {
        if (t.getType() instanceof Class)
            return ofRawType((Class<?>) t.getType());

        if (!(t instanceof AnnotatedParameterizedType))
            throw new IllegalArgumentException("expected raw or parameterized type: " + t);

        ParameterizedType pt = (ParameterizedType) t.getType();
        Class<?> clazz = (Class<?>) pt.getRawType();

        AnnotatedType[] a = ((AnnotatedParameterizedType) t).getAnnotatedActualTypeArguments();
        TypeVariable<? extends Class<?>>[] typeVars = clazz.getTypeParameters();
        if (a.length != typeVars.length)
            throw new RuntimeException("invalid type parameterization: " + t);

        Map<TypeVariable<? extends Class<?>>, AnnotatedType> args = new LinkedHashMap<>();
        for (int i = 0; i < a.length; i++)
            args.put(typeVars[i], a[i]);

        return new Parameterization(clazz, args);
    }

    // @Nullable
    public static Parameterization ofRawType(Class<?> type) {
        return type.getTypeParameters().length == 0 ? new Parameterization(type, Collections.emptyMap()) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parameterization that = (Parameterization) o;
        return params.equals(that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(params);
    }

    public AnnotatedType resolve(AnnotatedTypeVariable v) {
        TypeVariable<?> typeVar = (TypeVariable<?>) v.getType();
        return resolve(typeVar);
    }

    public AnnotatedType resolve(TypeVariable<?> typeVar) {
        if (!(typeVar.getGenericDeclaration() instanceof Class))
            // throw new UnsupportedOperationException("method type parameters are not supported: " + v);
            // egyelőre tegyük fel, hogy LocalizedRichText-hez van használva a típusparaméter.
            // majd lehet hogy szükség lesz rá, hogy ténylegesen parameterizáljunk
            // interface metódust paraméter alapján (pl. Class<T>).
            return AnnotatedClassImpl.of(Object.class);

        Class<?> typeVarClass = (Class<?>) typeVar.getGenericDeclaration();
        Map<TypeVariable<? extends Class<?>>, AnnotatedType> p = params.get(typeVarClass);
        if (p == null)
            throw new IllegalArgumentException("no type argument map found for " + typeVarClass +
                    " (needed to resolve type variable " + typeVar.getName() + ")");

        @SuppressWarnings("unchecked")
        AnnotatedType t = p.
                get((TypeVariable<? extends Class<?>>) typeVar);

        if (t == null)
            throw new IllegalArgumentException("no value found for type variable " + typeVar.getName() +
                    " (which is declared in " + typeVarClass + ")");
        return t;
    }

    public static AnnotatedType parameterize(AnnotatedType annotatedType, Parameterization parameterization) {
        try {
            return parameterizeImpl(annotatedType, parameterization);
        } catch (Exception e) {
            throw new RuntimeException("can't parameterize " +
                    ReflectionUtil.typeToString(annotatedType) + " using " + parameterization+": "+e, e);
        }
    }

    private static AnnotatedType parameterizeImpl(AnnotatedType annotatedType, Parameterization parameterization) {
        return AnnotatedTypeVisitor.visit(annotatedType, new AnnotatedTypeVisitor<AnnotatedType, RuntimeException>() {

            @Override
            public AnnotatedType visitTypeVariable(AnnotatedTypeVariable type) {
                if (parameterization == null)
                    throw new IllegalArgumentException("found type variable, but no parameterization: " +
                            type.getType() + ", " + parameterization); // TODO itt valójában az AnnotatedType-nak kéne a toStringje, de az Java 8-ban értelmetlenséget ad vissza

                AnnotatedType t = parameterization.resolve(type);
                return Types.withAdditionalAnnotations(t, type.getAnnotations()); // sorrend itt fordítva kéne
            }

            @Override
            public AnnotatedType visitClass(AnnotatedType type, Class<?> clazz) {
                return type;
            }

            @Override
            public AnnotatedType visitParameterizedType(AnnotatedParameterizedType type, Class<?> rawType) {
                AnnotatedType[] a = type.getAnnotatedActualTypeArguments();
                AnnotatedType[] p = new AnnotatedType[a.length];
                for (int i = 0; i < a.length; i++)
                    p[i] = parameterizeImpl(a[i], parameterization);

                return new AnnotatedParameterizedTypeImpl(
                        Types.getAnnotatedOwnerType(type),
                        (Class<?>) ((ParameterizedType) type.getType()).getRawType(),
                        p,
                        type.getAnnotations()
                );
            }

            @Override
            public AnnotatedType visitArrayType(AnnotatedArrayType type) {
                return new AnnotatedArrayTypeImpl(
                        parameterizeImpl(type.getAnnotatedGenericComponentType(), parameterization),
                        type.getAnnotations()
                );
            }

            @Override
            public AnnotatedType visitWildcardType(AnnotatedWildcardType type) {
                AnnotatedType[] ua = type.getAnnotatedUpperBounds(), up = new AnnotatedType[ua.length];
                for (int i = 0; i < ua.length; i++)
                    up[i] = parameterizeImpl(ua[i], parameterization);

                AnnotatedType[] la = type.getAnnotatedLowerBounds(), lp = new AnnotatedType[la.length];
                for (int i = 0; i < la.length; i++)
                    lp[i] = parameterizeImpl(la[i], parameterization);

                return new AnnotatedWildcardTypeImpl(up, lp, type.getAnnotations());
            }
        });
    }

    public static Class<?> parameterizeFieldType(Field field, Parameterization parameterization) {
        return parameterization == null ? field.getType() : rawType(parameterize(field.getAnnotatedType(), parameterization));
    }

    public static Class<?> parameterizeReturnType(Method method, Parameterization parameterization) {
        return parameterization == null ? method.getReturnType() : rawType(parameterize(method.getAnnotatedReturnType(), parameterization));
    }

    public static Class<?> parameterizeParamType(Parameter parameter, Parameterization parameterization) {
        return parameterization == null ? parameter.getType() : rawType(parameterize(parameter.getAnnotatedType(), parameterization));
    }

}
