// com.flyordie.reflect.ReflectionUtil

package ui10.binding5;

// import org.atteo.classindex.ClassIndex;

// import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.invoke.MethodHandles.lookup;
import static java.util.Arrays.asList;

/**
 *
 */
public class ReflectionUtil {

    private ReflectionUtil() {
        throw new AssertionError();
    }

    private static final ClassValue<List<Field>> FIELD_LIST_CV = new ClassValue<List<Field>>() {
        @Override
        protected List<Field> computeValue(Class<?> clazz) {
            Objects.requireNonNull(clazz, "null class");
            List<Field> result = new ArrayList<>();
            if (clazz.getSuperclass() != null)
                result.addAll(fieldsIn(clazz.getSuperclass()));
            result.addAll(asList(clazz.getDeclaredFields()));
            return Collections.unmodifiableList(result);
        }
    };

    public static List<Field> fieldsIn(Class<?> clazz) {
        return FIELD_LIST_CV.get(clazz);
    }

    public static List<Method> methodsIn(Class<?> clazz) {
        List<Method> result = new ArrayList<>();
        result.addAll(asList(clazz.getDeclaredMethods()));
        for (Class<?> iface : clazz.getInterfaces())
            result.addAll(methodsIn(iface));
        if (clazz.getSuperclass() != null)
            result.addAll(methodsIn(clazz.getSuperclass()));
        return result;
    }

    public static List<Method> methodsIn2(Class<?> clazz) {
        List<Method> result = new ArrayList<>();
        if (clazz.getSuperclass() != null)
            result.addAll(methodsIn2(clazz.getSuperclass()));
        for (Class<?> iface : clazz.getInterfaces())
            result.addAll(methodsIn2(iface));
        result.addAll(asList(clazz.getDeclaredMethods()));
        return result;
    }

    public static /*@Nonnull*/
    Class<?> rawType(AnnotatedType type) {
        Type genericType = type.getType();
        return rawType(genericType);
    }

    private static Class<?> rawType(Type genericType) {
        if (!(genericType instanceof Class)) {
            if (genericType instanceof TypeVariable) {
                Type[] bounds = ((TypeVariable<?>) genericType).getBounds();
                if (bounds.length != 1)
                    return Object.class; // lub kéne különben
                else
                    return rawType(bounds[0]);
            }
            genericType = ((ParameterizedType) genericType).getRawType();
        }
        Objects.requireNonNull(genericType);
        return (Class<?>) genericType;
    }

    public static String typeToString(AnnotatedType annotatedType) {
        // TODO support AnnotatedParameterizedType
        String result = "";
        for (Annotation annotation : annotatedType.getAnnotations()) {
            result += annotation + " ";
        }

        return result + annotatedType.getType().getTypeName();
    }

    public static void ensureClassInitialized(Class<?> clazz) {
        if (clazz.isPrimitive())
            return;

        try {
            Class.forName(clazz.getName(), true, clazz.getClassLoader());
        } catch (ClassNotFoundException ex) {
            throw new AssertionError(ex); // shouldn't happen
        }
    }

    public static String className(Class clazz) {
        return clazz.getName().replace('.', '/');
    }
/*
    public static Stream<Class<?>> annotatedClassesFromIndex(Class<? extends Annotation> annotation) {
        Iterator<Class<?>> iterator = ClassIndex.getAnnotated(annotation, ReflectionUtil.class.getClassLoader()).iterator();
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
    }
*/
    /**
     * Ha a megadott elem egy osztály, akkor ez a visszatérési érték az lesz, nem pedig egy
     * bennfoglaló osztály.
     */
    public static Class<?> declaringClass(AnnotatedElement element) {
        if (element instanceof Class)
            return (Class<?>) element;

        if (element instanceof Member)
            return ((Member) element).getDeclaringClass();

        if (element instanceof Parameter)
            return declaringClass(((Parameter) element).getDeclaringExecutable());

        throw new IllegalArgumentException(element + ", " + element.getClass());
    }

    /**
     * Ez a declaring class simple name-jét használja, míg a másik a teljeset
     */
    // Itt az elnevezés annyiból szerencsétlen, hogy a declaring class teljes nevét
    // adjuk vissza. De az meg hülyén nézett volna ki, hogy memberToShortStringWithFullDeclaringClassName. 
    // CBLocalizedText-nek kellett ez a működés, viszont configbinderben meg sok helyen 
    // simplename-mel kéne a declaringclass, ezért majd kéne csinálni egy olyan változatot is, 
    // csak nem tudom, minek kéne hívni. 
    public static String memberToShortString(AnnotatedElement element) {
        if (element instanceof Class)
            return ((Class) element).getName();

        if (element instanceof Method) {
            Method m = (Method) element;
            return m.getDeclaringClass().getName() + "::" + m.getName();
        }

        if (element instanceof Field) {
            Field f = (Field) element;
            return f.getDeclaringClass().getName() + "." + f.getName();
        }

        if (element instanceof Parameter)
            return "parameter " + element.toString() + " of "
                    + memberToShortString(((Parameter) element).getDeclaringExecutable());

        throw new IllegalArgumentException(element + ", " + element.getClass());
    }

    /**
     * Ez a declaring class simple name-jét használja, míg a másik a teljeset
     */
    public static String memberToShortString2(AnnotatedElement element) {
        if (element instanceof Class)
            return ((Class) element).getName();

        if (element instanceof Method) {
            Method m = (Method) element;
            return m.getDeclaringClass().getSimpleName() + "::" + m.getName();
        }

        if (element instanceof Field) {
            Field f = (Field) element;
            return f.getDeclaringClass().getSimpleName() + "." + f.getName();
        }

        if (element instanceof Parameter)
            return "parameter " + element.toString() + " of "
                    + memberToShortString2(((Parameter) element).getDeclaringExecutable());

        throw new IllegalArgumentException(element + ", " + element.getClass());
    }

	// TODO ezt a configbinderbe kéne mozgatni, mert az method esetén CB-specifikus a működése, nem pedig valami általános elfogadott
    public static AnnotatedType typeOf(AnnotatedElement e) {
        if (e instanceof Method) {
            Method m = (Method) e;
            boolean ifaceMethod = m.getDeclaringClass().isInterface();
            if (ifaceMethod)
                return m.getAnnotatedReturnType();
            else {
                switch (m.getParameterCount()) {
                    case 0: // getter
                        if (m.getReturnType() == void.class)
                            throw new IllegalArgumentException("unknown method: " + memberToShortString(e));
                        return m.getAnnotatedReturnType();
                    case 1: // setter
                        return m.getAnnotatedParameterTypes()[0];
                    default:
                        throw new IllegalArgumentException("unknown method: " + memberToShortString(e));
                }
            }
        }

        if (e instanceof Field)
            return ((Field) e).getAnnotatedType();

        if (e instanceof Parameter)
            return ((Parameter) e).getAnnotatedType();

        throw new IllegalArgumentException(e + ", " + e.getClass());
    }

    public static MethodHandle getterHandle(Field f) {
        f.setAccessible(true);
        MethodHandle mh;
        try {
            return lookup().unreflectGetter(f);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("should not happen", ex);
        }
    }

    public static MethodHandle handle(Method m) {
        m.setAccessible(true);
        MethodHandle mh;
        try {
            return lookup().unreflect(m);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("should not happen", ex);
        }
    }

    public static Field lookupField(Class<?> owner, String name) {
        // TODO kéne access check

        List<Field> fields = fieldsIn(owner);
        for (int i = fields.size() - 1; i >= 0; i--) {
            Field f = fields.get(i);
            if (f.getName().equals(name))
                return f;
        }

        throw new RuntimeException("no field named '" + name + "' found in " + owner);
    }

    public static int depth(Class<?> c) {
        if (c.isInterface())
            throw new IllegalArgumentException("interfaces not supported here");

        int d = 0;
        while (c != null) {
            d++;
            c = c.getSuperclass();
        }
        return d;
    }
}
