package ui10.layout;

import ui10.base.TransientElement;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.base.Element;
import ui10.base.LayoutContext1;
import ui10.base.LayoutContext2;

import java.util.function.BiConsumer;

public abstract class RectangularLayout extends TransientElement {
    @Override
    protected void performLayoutImpl(Shape shape, LayoutContext2 context) {
        Rectangle shapeBounds = shape.bounds();
        Size size = shapeBounds.size();
        doPerformLayout(size, (elem, rect) -> {
            Shape elemShape = rect.translate(shapeBounds.topLeft()).intersectionWith(shape);
            if (elemShape != null) // ilyenkor inkább nulla méretű shape-pel kéne hozzáadni
                context.placeElement(elem, elemShape);
        }, context);
    }

    protected abstract void doPerformLayout(Size size, BiConsumer<Element, Rectangle> placer, LayoutContext1 context);
}
