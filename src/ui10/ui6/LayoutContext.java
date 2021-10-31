package ui10.ui6;

import ui10.geom.shape.Shape;

import java.util.function.Consumer;

public interface LayoutContext extends Consumer<RenderableElement> {

    default void placeElement(Element element, Shape shape) {
        element.applyShape(shape, this);
    }
}