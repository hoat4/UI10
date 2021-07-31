package ui10.binding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

class SelfContainedScalarProperty<T> implements ScalarProperty<T> {

    // TODO mi legyen ChangeEvent.container értéke?

    private T value;

    private final List<Consumer<? super ChangeEvent<T>>> subscribers = new ArrayList<>();

    private ObservableScalar<? extends T> boundTo;
    private final Consumer<ChangeEvent<? extends T>> boundValueConsumer = evt -> set(evt.newValue());

    private ObservableList<PropertyTransformation<T>> transformations;
    private boolean valid;
    private Scope transformScope;
    private T transformedValue;

    @Override
    public void subscribe(Consumer<? super ChangeEvent<T>> subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void unsubscribe(Consumer<? super ChangeEvent<T>> subscriber) {
        if (!subscribers.remove(subscriber))
            throw new IllegalArgumentException("not subscribed: " + subscriber);
    }

    @Override
    public T get() {
        if (transformations == null)
            return value;

        if (!valid) {
            computeTransformedValue();
        }
        return transformedValue;
    }

    private void computeTransformedValue() {
        if (transformScope != null)
            transformScope.close();
        transformScope = new Scope();
        T v = value;

        for (PropertyTransformation<T> t : transformations) {
            v = t.apply(v, transformScope);
        }
        transformedValue = v;
        valid = true;
    }

    @Override
    public ScalarProperty<T> set(T value) {
        if (!Objects.equals(this.value, value))
            setImpl(value);
        return this;
    }

    private void setImpl(T value) {
        T oldValue = get();
        this.value = value;

        if (transformations == null) {
            subscribers.forEach(c -> c.accept(new ChangeEvent<>(null, oldValue, value)));
            return;
        }

        if (subscribers.isEmpty())
            valid = false;
        else {
            computeTransformedValue();
            if (!Objects.equals(oldValue, transformedValue))
                subscribers.forEach(c -> c.accept(new ChangeEvent<>(null, oldValue, transformedValue)));
        }
    }

    @Override
    public void bindTo(ObservableScalar<? extends T> other, Scope scope) {
        Objects.requireNonNull(other);
        if (boundTo != null)
            boundTo.unsubscribe(boundValueConsumer);
        boundTo = other;
        boundTo.subscribe(boundValueConsumer);
        set(boundTo.get());

        if (scope != null)
            scope.onClose(() -> {
                if (boundTo != other)
                    throw new IllegalStateException();
                else {
                    boundTo = null;
                    other.unsubscribe(boundValueConsumer);
                }
            });
    }

    @Override
    public ObservableList<PropertyTransformation<T>> transformations() {
        if (transformations == null) {
            transformations = new ObservableListImpl<>();
            transformations.subscribe(c -> {
                setImpl(value);
            });
        }
        return transformations;
    }

}
