package ui10.decoration.css;

import ui10.binding2.Property;

import java.util.Objects;

public class CSSPseudoClass extends Property<Void> {

    public final String name;

    public CSSPseudoClass(String name) {
        super(false);
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CSSPseudoClass that)) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
