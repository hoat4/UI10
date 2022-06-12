package ui10.base;

import ui10.input.InputEvent;
import ui10.input.keyboard.Keyboard;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public interface InputHandler {

    default void onFocusGain() {}

    default void onFocusLost() {}

    static void dispatchInputEvent(InputEvent event, InputHandler ih, EventContext context, boolean capture) {
        // reportolni kéne, hogy félrevezető az iterate-ben a hasNext elnevezése

        List<Class<?>> hierarchy = Stream.<Class<?>>iterate(ih.getClass(), Objects::nonNull, Class::getSuperclass).toList();
        for (Class<?> c : hierarchy)
            for (Method m : c.getDeclaredMethods()) {
                EventHandler h = m.getAnnotation(EventHandler.class);
                if (h != null && h.capture() == capture) {
                    Class<?>[] paramTypes = m.getParameterTypes();
                    if (paramTypes[0].isAssignableFrom(event.getClass())) {
                        m.setAccessible(true);
                        try {
                            m.invoke(ih, event, context);
                        } catch (ReflectiveOperationException e) {
                            throw new RuntimeException("can't invoke event handler " +
                                    m.getDeclaringClass().getSimpleName() + "." + m.getName() + ": " + e, e);
                        }
                    }
                }
            }
    }

    @Target(METHOD)
    @Retention(RUNTIME)
    public @interface EventHandler {

        boolean capture() default false;
    }

    @Target(METHOD)
    @Retention(RUNTIME)
    public @interface OnFunctionKey {

        Keyboard.StandardFunctionSymbol value();
    }
}
