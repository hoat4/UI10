package ui10.ui6.decoration;

import ui10.geom.Insets;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.ui6.Element;
import ui10.ui6.layout.LayoutContext2;
import ui10.ui6.layout.LayoutContext1;

import java.util.function.Consumer;

public class Border extends Element.TransientElement {

    public final Insets insets;
    public final Element fill;
    public final Element content;

    public Border(Insets insets, Element fill, Element content) {
        this.insets = insets;
        this.fill = fill;
        this.content = content;
    }

    @Override
    public void enumerateStaticChildren(Consumer<Element> consumer) {
        consumer.accept(content);
        consumer.accept(fill);
    }

    @Override
    protected Shape preferredShapeImpl(BoxConstraints constraints, LayoutContext1 context) {
        return insets.addTo(content.preferredShape(constraints.subtract(insets.all()), context));
    }

    @Override
    protected void applyShapeImpl(Shape shape, LayoutContext2 context) {
        Shape contentShape = insets.removeFrom(shape);

        fill.performLayout(shape.subtract(contentShape), context);
        content.performLayout(contentShape, context);
    }
}
