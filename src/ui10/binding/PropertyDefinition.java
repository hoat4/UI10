package ui10.binding;


import java.util.function.BiConsumer;
import java.util.function.Function;

public interface PropertyDefinition<N, T> {

    T get(N container);
    void set(N container, T value);

    record SimplePropertyDefinition<N, T>(Function<N, T> getter,
                                          BiConsumer<N, T> setter) implements PropertyDefinition<N, T> {

        // name?

        @Override
        public T get(N container) {
            return getter.apply(container);
        }

        @Override
        public void set(N container, T value) {
            setter.accept(container, value);
        }
    }

//    record ExtendedPropertyDefinition<N, T>() implements PropertyDefinition<N, T> {
//    }
}
