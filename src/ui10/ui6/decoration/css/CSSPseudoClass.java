package ui10.ui6.decoration.css;

import ui10.ui6.Attribute;

import java.util.Objects;

public class CSSPseudoClass extends Attribute {

    public final String name;

    public CSSPseudoClass(String name) {
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
