package ui10.decoration.css;

import java.time.Duration;

public record TransitionSpec<T>(CSSProperty<T> property, Duration duration) {
}
