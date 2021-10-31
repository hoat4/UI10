package ui10.ui6.decoration;

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

    public static <E extends Element> E withClass(String className, E elem) {
        return attr(elem, new CSSClass(className));
    }
}
