package ui10.decoration.css;

import ui10.base.Attribute;

import java.util.Objects;

class CSSElementName extends Attribute { // not a real attribute

    public final String name;

    public CSSElementName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CSSElementName that)) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

