package ui10.layout;

import ui10.base.*;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.geom.shape.Shape;

import java.util.function.Consumer;

public abstract class SingleNodeRectangularLayout extends LayoutElement {

    protected final Element content;

    public SingleNodeRectangularLayout(Element content) {
        this.content = content;
    }

    @Override
    public void enumerateChildren(Consumer<Element> consumer) {
        consumer.accept(content);
    }

    @Override
    protected void performLayout(Shape shape, LayoutContext2 context) {
        Rectangle shapeBounds = shape.bounds();
        Size size = shapeBounds.size();
        Shape elemShape = computeContentBounds(size, context).translate(shapeBounds.topLeft()).intersectionWith(shape);
        if (elemShape != null) // ilyenkor inkább nulla méretű shape-pel kéne hozzáadni
            context.placeElement(content, elemShape);
    }

    protected abstract Rectangle computeContentBounds(Size size, LayoutContext1 context);
}
