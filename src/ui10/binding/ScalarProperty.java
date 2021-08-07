package ui10.binding;

import ui10.binding.impl.SelfContainedScalarProperty;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface ScalarProperty<T> extends ObservableScalar<T> {

    ScalarProperty<T> set(T value);

    default ScalarProperty<T> set(T value, Scope scope) {
        // itt a visszaállítási érték null legyen vagy az előző?
        set(value);
        scope.onClose(() -> set(null));
        return this;
    }

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
        return new SelfContainedScalarProperty<>(null);
    }

    static <T> ScalarProperty<T> create(String name) {
        return new SelfContainedScalarProperty<>(name);
    }

    static <T> ScalarProperty<T> createWithDefault(T defaultValue) {
        SelfContainedScalarProperty<T> prop = new SelfContainedScalarProperty<>(null);
        prop.set(defaultValue);
        return prop;
    }

    static <T> ScalarProperty<T> createWithDefault(String name, T defaultValue) {
        SelfContainedScalarProperty<T> prop = new SelfContainedScalarProperty<>(name);
        prop.set(defaultValue);
        return prop;
    }
}
