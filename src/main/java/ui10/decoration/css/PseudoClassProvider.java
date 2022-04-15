package ui10.decoration.css;

import ui10.base.Element;
import ui10.binding2.Property;

import java.util.List;
import java.util.function.Predicate;

public record PseudoClassProvider(String name, List<Property<?>> dependencies, Predicate<Element> predicate) {

    public PseudoClassProvider(String name, Property<Boolean> booleanProperty) {
        this(name, List.of(booleanProperty), e -> {
            Boolean b = e.getProperty(booleanProperty);
            return b != null && b;
        });
    }
}
