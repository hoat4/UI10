package ui10.graphics;

import ui10.geom.Fraction;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.base.Element;
import ui10.base.RenderableElement;
import ui10.base.LayoutContext1;
import ui10.base.LayoutContext2;

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
    public Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context1) {
        return context1.preferredSize(content, constraints);
    }

    @Override
    protected void onShapeApplied(Shape shape) {
        LayoutContext2.ignoring().placeElement(content, shape);
    }
}
