package ui10.binding;

import ui10.binding.impl.Binding1;
import ui10.binding.impl.Binding2;
import ui10.binding.impl.Binding3;
import ui10.binding.impl.GenericBinding;

import java.util.List;
import java.util.function.*;

public interface ObservableScalar<T> extends Observable<ChangeEvent<T>> {

    T get();

    static <T> ObservableScalar<T> ofConstant(T value) {
        return ScalarProperty.createWithDefault(value); // TODO
    }

    static <T> ObservableScalar<T> val(T value) {
        return ScalarProperty.createWithDefault(value); // TODO
    }

    static <T, T1> Binding<T> binding(ObservableScalar<T1> other, Function<T1, T> f) {
        return new Binding1<>(other, f);
    }

    static <T, T1, T2> Binding<T> binding(ObservableScalar<T1> p1, ObservableScalar<T2> p2,
                                          BiFunction<T1, T2, T> f) {
        return new Binding2<>(p1, p2, f);
    }

    static <T, T1, T2, T3> Binding<T> binding(ObservableScalar<T1> p1, ObservableScalar<T2> p2,
                                              ObservableScalar<T3> p3, BindingValueSupplier3<T, T1, T2, T3> f) {
        return new Binding3<>(p1, p2, p3, f);
    }

    static <T, T1, T2, T3, T4> Binding<T> binding(ObservableScalar<T1> p1, ObservableScalar<T2> p2,
                                                  ObservableScalar<T3> p3, ObservableScalar<T4> p4,
                                                  BindingValueSupplier4<T, T1, T2, T3, T4> f) {
        return binding(List.of(p1, p2, p3, p4), () -> f.computeValue(p1.get(), p2.get(), p3.get(), p4.get()));
    }

    static <T> Binding<T> binding(List<Observable<?>> observables, Supplier<T> supplier) {
        return new GenericBinding<>(observables, supplier);
    }

    @FunctionalInterface
    interface BindingValueSupplier3<T, T1, T2, T3> {
        T computeValue(T1 t1, T2 t2, T3 t3);
    }

    @FunctionalInterface
    interface BindingValueSupplier4<T, T1, T2, T3, T4> {
        T computeValue(T1 t1, T2 t2, T3 t3, T4 t4);
    }

    // TODO ez a kettő így nem használható, mert nem lehet unsubscribeolni
    default void getAndSubscribe(Consumer<T> c) {
        c.accept(get());
        subscribe(evt -> c.accept(evt.newValue()));
    }

    default void getAndSubscribe(Scope scope, Consumer<T> c) {
        c.accept(get());
        subscribe(evt -> c.accept(evt.newValue()));
    }

    default void scopedGetAndSubscribe(BiConsumer<T, Scope> c) {
        getAndSubscribe(new Consumer<T>() {

            private Scope scope;

            @Override
            public void accept(T t) {
                if (scope != null) {
                    scope.close();
                    scope = null;
                }

                if (t != null) {
                    scope = new Scope();
                    c.accept(t, scope);
                }
            }
        });
    }

//    default void scopedGetAndSubscribe(Scope scope, BiConsumer<T, Scope> c) {
//        c.accept(get());
//        subscribe(evt -> c.accept(evt.newValue()));
//    }

    default <R> ObservableScalar<R> map(Function<T, R> f) {
        return binding(this, f);
    }

    default <R> ObservableScalar<R> nullsafeMap(Function<T, R> f) {
        return binding(this, x -> x == null ? null : f.apply(x));
    }

//    default <R> ObservableScalar<R> mapCloseable(Function<T, CloseableResult<R>> f) {
//        ScalarProperty<R> o = ScalarProperty.create();
//        getAndSubscribe(e -> new Consumer<T>() {
//
//            private Runnable lastCloser;
//
//            @Override
//            public void accept(T t) {
//                if (lastCloser != null)
//                    lastCloser.run();
//
//                CloseableResult<R> r = f.apply(t);
//                lastCloser = r.close;
//                o.set(r.result);
//            }
//        });
//        return o;
//    }
//
//    record CloseableResult<R>(R result, Runnable close) {
//    }

    default <R> ObservableScalar<R> flatMap(Function<T, ObservableScalar<R>> f) {
        ScalarProperty<R> o = ScalarProperty.create();
        getAndSubscribe(v -> {
            if (v == null)
                o.set(null);
            else
                o.bindTo(f.apply(v));
        });
        return o;
    }

    // default <R> ScalarProperty<R> flatMapProp(Function<T, ScalarProperty<R>> f) {

    //}
}
