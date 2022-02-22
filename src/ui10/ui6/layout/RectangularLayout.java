package ui10.ui6.layout;

import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.ui6.Element;
import ui10.ui6.LayoutContext1;
import ui10.ui6.LayoutContext2;

import java.util.function.BiConsumer;

abstract class RectangularLayout extends Element {
    @Override
    protected void performLayoutImpl(Shape shape, LayoutContext2 context) {
        Rectangle shapeBounds = shape.bounds();
        Size size = shapeBounds.size();
        doPerformLayout(size, (elem, rect) -> {
            context.placeElement(elem, rect.translate(shapeBounds.topLeft()).intersectionWith(shape));
        }, context);
    }

    protected abstract void doPerformLayout(Size size, BiConsumer<Element, Rectangle> placer, LayoutContext1 context);
}
