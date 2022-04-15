package ui10.binding.impl;

import ui10.binding.Binding;
import ui10.binding.ChangeEvent;
import ui10.binding.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericBinding<T> implements Binding<T> {
    private final List<Observable<?>> observables;
    private final Supplier<T> supplier;

    private T value;
    private final List<Consumer<? super ChangeEvent<T>>> subscriptions = new ArrayList<>();

    public GenericBinding(List<Observable<?>> observables, Supplier<T> supplier) {
        this.observables = observables;
        this.supplier = supplier;
        refresh();
        for (Observable<?> o : observables)
            o.subscribe(e -> refresh());
    }

    @Override
    public void refresh() {
        T oldValue = value;
        value = supplier.get();

        if (!Objects.equals(oldValue, value)) {
            var changeEvent = new ChangeEvent<>(null, oldValue, value);
            for (var subscription : subscriptions) {
                subscription.accept(changeEvent);
            }
        }
    }

    @Override
    public void subscribe(Consumer<? super ChangeEvent<T>> subscriber) {
        subscriptions.add(subscriber);
    }

    @Override
    public void unsubscribe(Consumer<? super ChangeEvent<T>> subscriber) {
        if (!subscriptions.remove(subscriber))
            throw new IllegalArgumentException("not subscribed: " + subscriber);
    }

    @Override
    public T get() {
        return value;
    }
}
