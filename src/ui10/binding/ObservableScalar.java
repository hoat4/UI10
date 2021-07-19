package ui10.binding;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ObservableScalar<T> extends Observable<ChangeEvent<T>> {

    T get();

    static <T, T1> ObservableScalar<T> binding(ObservableScalar<T1> other, Function<T1, T> f) {
        ScalarProperty<T> o = ScalarProperty.create();
        other.getAndSubscribe(v -> o.set(f.apply(v)));
        return o;
    }

    static <T, T1, T2> ObservableScalar<T> binding(ObservableScalar<T1> p1, ObservableScalar<T2> p2,
                                                   BiFunction<T1, T2, T> f) {
        ScalarProperty<T> o = ScalarProperty.create();
        p1.subscribe(e -> o.set(f.apply(p1.get(), p2.get())));
        p2.subscribe(e -> o.set(f.apply(p1.get(), p2.get())));
        o.set(f.apply(p1.get(), p2.get()));
        return o;
    }

    default void getAndSubscribe(Consumer<T> c) {
        c.accept(get());
        subscribe(evt -> c.accept(evt.newValue()));
    }

    default <R> ObservableScalar<R> map(Function<T, R> f) {
        return binding(this, f);
    }

    default <R> ObservableScalar<R> flatMap(Function<T, ObservableScalar<R>> f) {
        ScalarProperty<R> o = ScalarProperty.create();
        getAndSubscribe(v->{
            if (v == null)
                o.set(null);
            else
                o.bindTo(f.apply(v));
        });
        return o;
    }
}
