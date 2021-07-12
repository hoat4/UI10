package ui10.binding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

class SelfContainedScalarProperty<T> implements ScalarProperty<T> {

    // TODO mi legyen ChangeEvent.container értéke?

    private T value;

    private final List<Consumer<ChangeEvent<T>>> subscribers = new ArrayList<>();

    private ObservableScalar<T> boundTo;
    private final Consumer<ChangeEvent<T>> boundValueConsumer = evt->set(evt.newValue());

    @Override
    public void subscribe(Consumer<ChangeEvent<T>> subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void unsubscribe(Consumer<ChangeEvent<T>> subscriber) {
        if (!subscribers.remove(subscriber))
            throw new IllegalArgumentException("not subscribed: " + subscriber);
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T value) {
        T oldValue = this.value;
        this.value = value;
        subscribers.forEach(c -> c.accept(new ChangeEvent<>(null, oldValue, value)));
    }

    @Override
    public void bindTo(ObservableScalar<T> other) {
        Objects.requireNonNull(other);
        if (boundTo != null)
            boundTo.unsubscribe(boundValueConsumer);
        boundTo = other;
        boundTo.subscribe(boundValueConsumer);
        set(boundTo.get());
    }

    @Override
    public ObservableScalar<T> original() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObservableScalar<ObservableScalar<T>> replacement() {
        throw new UnsupportedOperationException();
    }
}
