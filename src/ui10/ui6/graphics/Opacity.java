package ui10.ui6.graphics;

import ui10.geom.Fraction;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.ui6.Element;
import ui10.ui6.RenderableElement;
import ui10.ui6.layout.LayoutContext1;
import ui10.ui6.layout.LayoutContext2;

import java.util.Objects;
import java.util.function.Consumer;

public class Opacity extends RenderableElement {

    public final RenderableElement content;
    public final Fraction fraction;

    public Opacity(Element content, Fraction fraction) {
        Objects.requireNonNull(content, "content");
        Objects.requireNonNull(fraction, "fraction");

        this.content = RenderableElement.of(content);
        this.fraction = fraction;
    }

    @Override
    public void enumerateStaticChildren(Consumer<Element> consumer) {
        consumer.accept(content);
    }

    @Override
    protected Shape preferredShapeImpl(BoxConstraints constraints, LayoutContext1 context1) {
        return content.preferredShape(constraints, context1);
    }

    @Override
    protected void onShapeApplied(Shape shape, LayoutContext2 context) {
        content.performLayout(shape, LayoutContext2.ignoring(this));
    }
}
