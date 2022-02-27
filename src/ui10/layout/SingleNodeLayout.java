package ui10.layout;

import ui10.geom.shape.Shape;
import ui10.base.Element;
import ui10.base.LayoutContext2;

import java.util.function.Consumer;

public abstract class SingleNodeLayout extends Element {

    protected final Element content;

    public SingleNodeLayout(Element content) {
        this.content = content;
    }

    @Override
    public void enumerateStaticChildren(Consumer<Element> consumer) {
        consumer.accept(content);
    }

    @Override
    protected void performLayoutImpl(Shape shape, LayoutContext2 context) {
        Shape contentShape = computeContentShape(shape, context);
        // ilyenkor lehet hogy mégis hozzá kéne adni, de mondjuk 0 méretű téglalapként
        if (contentShape != null)
            context.placeElement(content, contentShape);
    }

    protected abstract Shape computeContentShape(Shape containerShape, LayoutContext2 context);
}
