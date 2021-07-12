package ui10.binding;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface ObservableScalar<T> extends Observable<ChangeEvent<T>> {

    T get();

    static <T, T1> ObservableScalar<T> binding(ObservableScalar<T1> other, Function<T1, T> f) {
        return null;
    }

    static <T, T1, T2> ObservableScalar<T> binding(ObservableScalar<T1> p1, ObservableScalar<T2> p2,
                                                   BiFunction<T1, T2, T> f) {
        return null;
    }
}
