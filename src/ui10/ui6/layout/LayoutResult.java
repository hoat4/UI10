package ui10.ui6.layout;

import ui10.geom.shape.Shape;
import ui10.ui6.Element;

public record LayoutResult(Shape shape, Class<? extends Element> elementClass, Object obj) {

    public LayoutResult {
        shape = shape.translate(shape.bounds().topLeft().negate());
    }

    public LayoutResult(Shape shape, Element element, Object obj) {
        this(shape, element.getClass(), obj);
    }
}
