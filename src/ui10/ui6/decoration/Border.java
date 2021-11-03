package ui10.ui6.decoration;

import ui10.geom.Insets;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.ui6.Element;
import ui10.ui6.LayoutContext;
import ui10.ui6.layout.LayoutResult;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
    protected LayoutResult preferredShapeImpl(BoxConstraints constraints) {
        LayoutResult contentLR = content.preferredShape(constraints.subtract(insets.all()));
        return new LayoutResult(
                insets.addTo(contentLR.shape()),
                this,
                contentLR
        );
    }

    @Override
    protected void applyShapeImpl(Shape shape, LayoutContext context, List<LayoutResult> lr) {
        Shape contentShape = insets.removeFrom(shape);

        fill.performLayout(shape.subtract(contentShape), context, List.of());
        content.performLayout(contentShape, context, lr.stream().
                map(l -> (LayoutResult) l.obj()).collect(Collectors.toList()));
    }
}
