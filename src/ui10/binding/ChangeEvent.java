package ui10.binding;

public record ChangeEvent<T>(PropertyDefinition<?, T> property, T oldValue, T newValue) implements PropertyEvent {
}
