package ui10.binding2;

public record ChangeEvent<T>(Property<T> property, T oldValue, T newValue) implements ElementEvent {
}
