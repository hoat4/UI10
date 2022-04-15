package ui10.decoration.css;

import ui10.base.Element;
import ui10.binding2.Property;

public class CSSClass extends Property<Void> {

    public final String name;

    public CSSClass(String name) {
        super(false);
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
        elem.setProperty(new CSSClass(className), null);
        return elem;
    }


}
