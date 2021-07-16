package ui10.binding;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class PropertyHolder {

    private final Map<PropertyDefinition<?, ?>, PropertyData<?>> properties = new HashMap<>();
    final Map<Object, Object> extendedProperties = new HashMap<>();

    public <N extends PropertyHolder, T> ScalarProperty<T> property(PropertyDefinition<N, T> def) {
        return new ScalarPropertyImpl<>((N) this, def);
    }

    protected <N extends PropertyHolder, T> ScalarProperty<T> property(
            Function<N, T> getter, BiConsumer<N, T> setter) {
        return property(new PropertyDefinition.SimplePropertyDefinition<>(getter, setter));
    }

    public <T extends PropertyEvent> void onChange(T changeEvent) {
        // TODO ez a cast miért fordul le?
        List<? extends Consumer<T>> consumers = (List<? extends Consumer<T>>)
                properties.get(changeEvent.property());
        if (consumers != null)
            for (int i = 0; i < consumers.size(); i++)
                consumers.get(i).accept(changeEvent);
    }

    public <T> void subscribe(Consumer<? extends PropertyEvent> subscriber, PropertyDefinition<?, T> property) {
        propertyData(property).add(subscriber);
    }

    <T> PropertyData<?> propertyData(PropertyDefinition<?, T> property) {
        return properties.computeIfAbsent(property, __ -> new PropertyData<T>());
    }

    public void unsubscribe(Consumer<? extends PropertyEvent> subscriber, PropertyDefinition<?, ?> property) {
        List<Consumer<? extends PropertyEvent>> subscribers = properties.get(property);
        if (subscribers == null || !subscribers.remove(subscriber))
            throw new IllegalArgumentException("not subscribed to property " + property + ": " + subscriber);
    }

    // erre szükség van? esetleg ha lesz teljes Node-ra vonatkozó subscription, debug célból
    public void unsubscribe(Consumer<? extends PropertyEvent> subscriber) {
        properties.forEach((prop, subscriptions) -> {
            subscriptions.removeIf(s -> Objects.equals(subscriber, s));
        });
    }

    static class PropertyData<T> extends ArrayList<Consumer<? extends PropertyEvent>> {

        public ObservableScalar<T> boundTo;
    }
}
