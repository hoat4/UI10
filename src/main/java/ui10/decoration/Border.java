package ui10.decoration;

import ui10.base.*;
import ui10.geom.Insets;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.function.Consumer;

public class Border extends LayoutElement {

    public final Insets insets;
    public final Element fill;
    public final Element content;

    public Border(Insets insets, Element fill, Element content) {
        this.insets = insets;
        this.fill = fill;
        this.content = content;
    }

    @Override
    public void enumerateChildren(Consumer<Element> consumer) {
        consumer.accept(content);
        consumer.accept(fill);
    }

    @Override
    public Size preferredSize(BoxConstraints constraints, LayoutContext1 context) {
        return context.preferredSize(content, constraints.subtract(insets.all())).add(insets.all());
    }

    @Override
    protected void performLayout(Shape shape, LayoutContext2 context) {
        Shape contentShape = insets.removeFrom(shape);

        context.placeElement(fill, shape.subtract(contentShape));
        context.placeElement(content, contentShape);
    }
}
