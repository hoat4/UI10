package ui10.binding.impl;

import ui10.binding.Binding;
import ui10.binding.ChangeEvent;
import ui10.binding.ObservableScalar;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@SuppressWarnings("rawtypes")
public class Binding2<T, T1, T2> implements Binding<T> {

    private final ObservableScalar<T1> o1;
    private final ObservableScalar<T2> o2;
    private final BiFunction<T1, T2, T> valueSupplier;

    private T prevValue, value;
    private ChangeEvent<?> lastChange;

    private T1 prevT1;
    private T2 prevT2;

    public Binding2(ObservableScalar<T1> o1, ObservableScalar<T2> o2,
                    BiFunction<T1, T2, T> valueSupplier) {
        this.o1 = o1;
        this.o2 = o2;
        this.valueSupplier = valueSupplier;

        value = valueSupplier.apply(prevT1 = o1.get(), prevT2 = o2.get());
    }

    @Override
    public void refresh() {
        value = valueSupplier.apply(prevT1 = o1.get(), prevT2 = o2.get());
    }

    @Override
    public T get() {
        T1 t1 = o1.get();
        T2 t2 = o2.get();
        if (!Objects.equals(prevT1, t1) || !Objects.equals(prevT2, t2))
            value = valueSupplier.apply(prevT1 = o1.get(), prevT2 = o2.get());
        return value;
    }

    @Override
    public void subscribe(Consumer<? super ChangeEvent<T>> subscriber) {
        Subscriber s = new Subscriber(subscriber);
        o1.subscribe(s);
        o2.subscribe(s);
    }

    @Override
    public void unsubscribe(Consumer<? super ChangeEvent<T>> subscriber) {
        Subscriber s = new Subscriber(subscriber);
        o1.unsubscribe(s);
        o2.unsubscribe(s);
    }

    // raw type, hogy ne rondítsa el az accept() bridge methodja a stack trace-t
    private class Subscriber implements Consumer {

        private final Consumer<? super ChangeEvent<T>> delegate;

        public Subscriber(Consumer<? super ChangeEvent<T>> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void accept(Object changeEvent) {
            if (changeEvent != lastChange) {
                prevValue = value;
                value = valueSupplier.apply(prevT1 = o1.get(), prevT2 = o2.get());
                lastChange = (ChangeEvent<?>) changeEvent;
            }
         // TODO   if (!Objects.equals(prevValue, value))
                delegate.accept(new ChangeEvent<>(null, prevValue, value));
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Binding2<?, ?, ?>.Subscriber s && s.delegate.equals(delegate);
        }

        @Override
        public int hashCode() {
            return delegate.hashCode() + 1;
        }
    }
}
