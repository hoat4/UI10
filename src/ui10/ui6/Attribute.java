package ui10.ui6;

import java.util.List;

public abstract class Attribute {

    public static <E extends Element> E attr(E elem, Attribute... attributes) {
        elem.attributes().addAll(List.of(attributes));
        return elem;
    }
}
