package ui10.di;

import ui10.binding5.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.function.Consumer;

public class ProvideHandler {

    @SuppressWarnings("unchecked")
    public static <T> void collectProvidedObjects(Object obj, Class<T> type, Consumer<T> consumer) {
        for (Field field : ReflectionUtil.fieldsIn(obj.getClass()))
            if (field.isAnnotationPresent(Provide.class))
                if (field.getType() == type) {
                    field.setAccessible(true);
                    try {
                        consumer.accept((T) field.get(obj));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("shouldn't reach here", e);
                    }
                }
    }
}
