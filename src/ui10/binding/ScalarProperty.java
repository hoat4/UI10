package ui10.binding;

import ui10.node.Node;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface ScalarProperty<T> extends ObservableScalar<T> {

    ScalarProperty<T> set(T value);

    // nonnull. vagy legyen nullable, ami kitörli az eddigi bindet?
    void bindTo(ObservableScalar<T> other) ;

    default <T1> void bindTo(ObservableScalar<T1> other, Function<T1, T> f) {
        bindTo(ObservableScalar.binding(other, f));
    }

    default <T1, T2> void bindTo(ObservableScalar<T1> p1, ObservableScalar<T2> p2, BiFunction<T1, T2, T> f) {
        bindTo(ObservableScalar.binding(p1, p2, f));
    }

    // ...

    ObservableScalar<T> original();

    ObservableScalar<ObservableScalar<T>> replacement();

    static <T> ScalarProperty<T> create() {
        return new SelfContainedScalarProperty<>();
    }
}
