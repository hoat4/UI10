package ui10.ui6.layout;

import ui10.geom.shape.Shape;
import ui10.ui6.Element;
import ui10.ui6.LayoutContext2;

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
        context.placeElement(content, computeContentShape(shape, context));
    }

    protected abstract Shape computeContentShape(Shape containerShape, LayoutContext2 context);
}
