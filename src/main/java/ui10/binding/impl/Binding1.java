package ui10.binding.impl;

import ui10.binding.Binding;
import ui10.binding.ChangeEvent;
import ui10.binding.ObservableScalar;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Binding1<T, T1> implements Binding<T> {

    private final ObservableScalar<T1> o1;
    private final Function<T1, T> valueSupplier;

    private T prevValue, value;
    private ChangeEvent<?> lastChange;

    private T1 prevT1;

    public Binding1(ObservableScalar<T1> o1, Function<T1, T> valueSupplier) {
        this.o1 = Objects.requireNonNull(o1);
        this.valueSupplier = Objects.requireNonNull(valueSupplier);
        value = valueSupplier.apply(prevT1 = o1.get());
    }

    @Override
    public void refresh() {
        value = valueSupplier.apply(prevT1 = o1.get());
    }

    @Override
    public T get() {
        T1 t1 = o1.get();
        if (!Objects.equals(prevT1, t1))
            value = valueSupplier.apply(prevT1 = o1.get());
        return value;
    }

    @Override
    public void subscribe(Consumer<? super ChangeEvent<T>> subscriber) {
        o1.subscribe(new Subscriber(subscriber));
    }

    @Override
    public void unsubscribe(Consumer<? super ChangeEvent<T>> subscriber) {
        o1.unsubscribe(new Subscriber(subscriber));
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
                value = valueSupplier.apply(prevT1 = o1.get());
                lastChange = (ChangeEvent<?>) changeEvent;
            }
          // TODO  if (!Objects.equals(prevValue, value))
                delegate.accept(new ChangeEvent<>(null, prevValue, value));
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Binding1<?, ?>.Subscriber s && s.delegate.equals(delegate);
        }

        @Override
        public int hashCode() {
            return delegate.hashCode() + 1;
        }
    }
}
