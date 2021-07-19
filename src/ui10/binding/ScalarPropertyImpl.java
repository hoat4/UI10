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
    public ScalarProperty<T> set(T value) {
        PropertyHolder.PropertyData<T> propertyData = container().propertyDataOrNull(definition);
        if (propertyData != null && propertyData.boundTo != null)
            propertyData.boundTo.unsubscribe(this);

        T oldValue = definition.get(container);
        definition.set(container, value);
        container.onChange(new ChangeEvent<>(definition, oldValue, value));
        return this;
    }

    @Override
    public void bindTo(ObservableScalar<T> other) {
        Objects.requireNonNull(other);

        PropertyHolder.PropertyData propertyData = container().propertyData(definition);
        if (propertyData.boundTo != null)
            propertyData.boundTo.unsubscribe(this);
        set(other.get());
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
