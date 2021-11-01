package ui10.ui6.decoration.css;

import ui10.ui6.Attribute;
import ui10.ui6.Element;

public class CSSClass extends Attribute {

    public final String name;

    public CSSClass(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "." + name;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof CSSClass c && c.name.equals(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public static <E extends Element> E withClass(String className, E elem) {
        return attr(elem, new CSSClass(className));
    }
}
