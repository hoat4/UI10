package ui10.binding2;

public record ChangeEvent<T>(Property<T> property, T value) implements ElementEvent {
}
