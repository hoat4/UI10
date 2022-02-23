package ui10.base;

import java.util.List;

public abstract class Attribute {

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    public static <E extends Element> E attr(E elem, Attribute... attributes) {
        elem.attributes().addAll(List.of(attributes));
        return elem;
    }
}
