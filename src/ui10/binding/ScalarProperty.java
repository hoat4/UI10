package ui10.binding;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface ScalarProperty<T> extends ObservableScalar<T> {

    ScalarProperty<T> set(T value);

    // nonnull. vagy legyen nullable, ami kitörli az eddigi bindet?
    default void bindTo(ObservableScalar<? extends T> other) {
        bindTo(other, (Scope) null);
    }

    void bindTo(ObservableScalar<? extends T> other, Scope scope);

    default <T1> void bindTo(ObservableScalar<T1> other, Function<T1, T> f) {
        bindTo(ObservableScalar.binding(other, f));
    }

    default <T1, T2> void bindTo(ObservableScalar<T1> p1, ObservableScalar<T2> p2, BiFunction<T1, T2, T> f) {
        bindTo(ObservableScalar.binding(p1, p2, f));
    }

    // ...

    ObservableList<PropertyTransformation<T>> transformations();

    static <T> ScalarProperty<T> create() {
        return new SelfContainedScalarProperty<>();
    }

}
