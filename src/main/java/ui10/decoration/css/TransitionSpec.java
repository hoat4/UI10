package ui10.decoration.css;

import ui10.base.ElementExtra;

import java.time.Duration;
import java.util.Objects;

public record TransitionSpec<T>(CSSProperty<T> property, Duration duration) {

    public static class TransitionKey<T> extends ElementExtra {

        public final CSSProperty<T> cssProperty;
        public Transition<T> transition;

        public TransitionKey(CSSProperty<T> cssProperty) {
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
