package ui10.decoration.css;

import ui10.binding2.Property;

import java.time.Duration;
import java.util.Objects;

public record TransitionSpec<T>(CSSProperty<T> property, Duration duration) {

    public static class TransitionKey<T> extends Property<Transition<T>> {

        public final CSSProperty<T> cssProperty;

        public TransitionKey(CSSProperty<T> cssProperty) {
            super(false);
            this.cssProperty = Objects.requireNonNull(cssProperty);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TransitionKey<?> that = (TransitionKey<?>) o;
            return cssProperty.equals(that.cssProperty);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cssProperty);
        }
    }
}
