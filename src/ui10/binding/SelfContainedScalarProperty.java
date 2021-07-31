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
        return transformedValue;
    }

    @Override
    public ScalarProperty<T> set(T value) {
        // TODO unbind?
        if (!Objects.equals(this.value, value))
            setImpl(value);
        return this;
    }

    private void setImpl(T value) {
        T oldValue = transformedValue;
        this.value = value;

        if (transformScope != null)
            transformScope.close();
        transformScope = new Scope();
        T v = value;

        if (transformations != null)
            for (PropertyTransformation<T> t : transformations) {
                v = t.apply(v, transformScope);
            }
        transformedValue = v;

        if (!Objects.equals(oldValue, transformedValue)) {
            int n = subscribers.size();
            for (int i = 0; i < n; i++)
                subscribers.get(i).accept(new ChangeEvent<>(null, oldValue, transformedValue));
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
