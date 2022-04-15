package ui10.layout;

import ui10.base.Element;
import ui10.base.LayoutContext1;
import ui10.base.LayoutContext2;
import ui10.base.TransientElement;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.geom.shape.Shape;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class SingleNodeRectangularLayout extends TransientElement {

    protected final Element content;

    public SingleNodeRectangularLayout(Element content) {
        this.content = content;
    }

    @Override
    public void enumerateStaticChildren(Consumer<Element> consumer) {
        consumer.accept(content);
    }

    @Override
    protected abstract Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context);


    @Override
    protected void performLayoutImpl(Shape shape, LayoutContext2 context) {
        Rectangle shapeBounds = shape.bounds();
        Size size = shapeBounds.size();
        Shape elemShape = computeContentBounds(size, context).translate(shapeBounds.topLeft()).intersectionWith(shape);
        if (elemShape != null) // ilyenkor inkább nulla méretű shape-pel kéne hozzáadni
            context.placeElement(content, elemShape);
    }

    protected abstract Rectangle computeContentBounds(Size size, LayoutContext1 context);
}
