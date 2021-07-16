package ui10.binding;

public class ExtendedPropertyDefinition<N extends PropertyHolder, T> implements PropertyDefinition<N, T> {
    @Override
    public T get(N container) {
        return (T) container.extendedProperties.get(this);
    }

    @Override
    public void set(N container, T value) {
        container.extendedProperties.put(this, value);
    }
}
