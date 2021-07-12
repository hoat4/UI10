package ui10.binding;

import java.util.Objects;
import java.util.function.Consumer;

record ScalarPropertyImpl<N extends PropertyHolder, T>(
        N container, PropertyDefinition<N, T> definition)
        implements ScalarProperty<T>,
        Consumer<ChangeEvent<T>> // bindhoz
{

    @Override
    public void subscribe(Consumer<ChangeEvent<T>> subscriber) {
        container.subscribe(subscriber, definition);
    }

    @Override
    public void unsubscribe(Consumer<ChangeEvent<T>> subscriber) {
        container.unsubscribe(subscriber, definition);
    }

    @Override
    public T get() {
        return definition.get(container);
    }

    @Override
    public void set(T value) {
        T oldValue = definition.get(container);
        definition.set(container, value);
        container.onChange(new ChangeEvent<>(definition, oldValue, value));
    }

    @Override
    public void bindTo(ObservableScalar<T> other) {
        Objects.requireNonNull(other);

        PropertyHolder.PropertyData propertyData = container().propertyData(definition);
        if (propertyData.boundTo != null)
            propertyData.boundTo.unsubscribe(this);
        propertyData.boundTo = other;
        propertyData.boundTo.subscribe(this);
    }

    @Override
    public ObservableScalar<T> original() {
        return null;
    }

    @Override
    public ObservableScalar<ObservableScalar<T>> replacement() {
        return null;
    }

    @Override
    public void accept(ChangeEvent<T> e) {
        set(e.newValue());
    }
}
