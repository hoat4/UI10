package ui10.binding;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class PropertyHolder {

    private final Map<PropertyDefinition<?, ?>, PropertyData<?>> properties = new HashMap<>();
    protected final Map<Object, Object> extendedProperties = new HashMap<>();

    public <N extends PropertyHolder, T> ScalarProperty<T> property(PropertyDefinition<N, T> def) {
        return new ScalarPropertyImpl<>((N) this, def);
    }

    protected <N extends PropertyHolder, T> ScalarProperty<T> property(
            Function<N, T> getter, BiConsumer<N, T> setter) {
        return property(new PropertyDefinition.SimplePropertyDefinition<>(getter, setter));
    }

    protected <N extends PropertyHolder, T> ScalarProperty<T> inheritableProperty(Function<N, ObservableScalar<N>> parentFunction,
                                                                                  PropertyDefinition<N, T> def) {
        ScalarProperty<T> prop = property(def);
        PropertyData<?> propData = properties.get(def);
        if (propData == null || !propData.inheritanceInitialized) {
            prop.bindTo(parentFunction.apply((N) this).flatMap(parent -> {
                return parent.inheritableProperty(parentFunction, def);
            }));

            if (propData == null)
                properties.put(def, propData = new PropertyData<>());
            propData.inheritanceInitialized = true; // TODO ezt előtte kéne, vagy így, utána?
        }
        return prop;
    }

    protected <N extends PropertyHolder, T> ScalarProperty<T> inheritableProperty(Function<N, ObservableScalar<N>> parentFunction,
                                                                                  Function<N, T> getter, BiConsumer<N, T> setter) {
        return inheritableProperty(parentFunction, new PropertyDefinition.SimplePropertyDefinition<>(getter, setter));
    }


    public <T extends PropertyEvent> void onChange(T changeEvent) {
        // TODO ez a cast hibásan van jelezve az IDE-ben, ott akkor is lefordul ha kiveszem a belső castot,
        //      pedig nem kéne
        List<? extends Consumer<T>> consumers = (List<? extends Consumer<T>>) (List<? extends Consumer<?>>)
                properties.get(changeEvent.property());
        if (consumers != null) {
            // szándékosan kiemelve változóba, hogy az újonnan bejegyzett consumereket ne vegyük figyelembe
            int consumerCount = consumers.size();
            for (int i = 0; i < consumerCount; i++)
                consumers.get(i).accept(changeEvent);
        }
    }

    // itt a consumer inkább Consumer<? super ? extends PropertyEvent> lenne, de olyat nem lehet
    public <T> void subscribe(Consumer<?> subscriber, PropertyDefinition<?, T> property) {
        propertyData(property).add(subscriber);
    }

    @SuppressWarnings("unchecked")
    <T> PropertyData<T> propertyDataOrNull(PropertyDefinition<?, T> property) {
        return (PropertyData<T>) properties.get(property);
    }

    @SuppressWarnings("unchecked")
    <T> PropertyData<T> propertyData(PropertyDefinition<?, T> property) {
        return (PropertyData<T>) properties.computeIfAbsent(property, __ -> new PropertyData<T>());
    }

    public void unsubscribe(Consumer<?> subscriber, PropertyDefinition<?, ?> property) {
        List<Consumer<?>> subscribers = properties.get(property);
        if (subscribers == null || !subscribers.remove(subscriber))
            throw new IllegalArgumentException("not subscribed to property " + property + ": " + subscriber);
    }

    // erre szükség van? esetleg ha lesz teljes Node-ra vonatkozó subscription, debug célból
    public void unsubscribe(Consumer<?> subscriber) {
        properties.forEach((prop, subscriptions) -> {
            subscriptions.removeIf(s -> Objects.equals(subscriber, s));
        });
    }

    static class PropertyData<T> extends ArrayList<Consumer<?>> {

        public ObservableScalar<T> boundTo;
        public boolean inheritanceInitialized;

    }
}
