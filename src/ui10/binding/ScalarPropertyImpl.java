package ui10.binding;

import java.util.Objects;
import java.util.function.Consumer;

record ScalarPropertyImpl<N extends PropertyHolder, T>(
        N container, PropertyDefinition<N, T> definition)
        implements ScalarProperty<T>,
        Consumer<ChangeEvent<? extends T>> // bindhoz
{

    @Override
    public void subscribe(Consumer<? super ChangeEvent<T>> subscriber) {
        container.subscribe(subscriber, definition);
    }

    @Override
    public void unsubscribe(Consumer<? super ChangeEvent<T>> subscriber) {
        container.unsubscribe(subscriber, definition);
    }

    @Override
    public T get() {
        return definition.get(container);
    }

    @Override
    public ScalarProperty<T> set(T value) {
        PropertyHolder.PropertyData<T> propertyData = container().propertyDataOrNull(definition);
        if (propertyData != null && propertyData.boundTo != null) {
            propertyData.boundTo.unsubscribe(this);
            propertyData.boundTo = null;
        }

        setImpl(value);
        return this;
    }

    private void setImpl(T value) {
        T oldValue = definition.get(container);
        definition.set(container, value);
        container.onChange(new ChangeEvent<>(definition, oldValue, value));
    }

    @Override
    public void bindTo(ObservableScalar<? extends T> other, Scope scope) {
        Objects.requireNonNull(other);

        PropertyHolder.PropertyData propertyData = container().propertyData(definition);
        if (propertyData.boundTo != null)
            propertyData.boundTo.unsubscribe(this);
        set(other.get());
        propertyData.boundTo = other;
        propertyData.boundTo.subscribe(this);

        if (scope != null)
            scope.onClose(() -> {
                if (propertyData.boundTo != other)
                    throw new IllegalStateException();
                else {
                    propertyData.boundTo = null;
                    other.unsubscribe(ScalarPropertyImpl.this);
                }
            });
}

    @Override
    public void accept(ChangeEvent<? extends T> e) {
        setImpl(e.newValue());
    }

    @Override
    public ObservableList<PropertyTransformation<T>> transformations() {
        throw new UnsupportedOperationException();
    }
}
