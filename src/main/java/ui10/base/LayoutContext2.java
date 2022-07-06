package ui10.base;

import ui10.geom.shape.Shape;

import java.util.*;
import java.util.function.Consumer;

public abstract class LayoutContext2 extends LayoutContext1 implements Consumer<Element> {

    public LayoutContext2(Element defaultParent) {
        super(defaultParent);
    }

    public void placeElement(Element element, Shape shape) {
        Objects.requireNonNull(element, "element");
        Objects.requireNonNull(shape, "shape");

        if (element.parent() == null)
            element.initParent(defaultParent);

        element.applyShape(shape, this);

        if (element.view() == null)
            accept(element);
    }

    public static LayoutContext2 ignoring(Element defaultParent) {
        return new LayoutContext2(defaultParent) {
            @Override
            public void accept(Element renderableElement) {
            }
        };
    }
}
