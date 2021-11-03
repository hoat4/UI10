package ui10.ui6.graphics;

import ui10.font.TextStyle;
import ui10.geom.Rectangle;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.ui6.Element;
import ui10.ui6.LayoutContext;
import ui10.ui6.RenderableElement;
import ui10.ui6.layout.LayoutResult;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class TextNode extends RenderableElement {

    private String text;
    private Element fill;
    private TextStyle textStyle;

    public TextNode() {
    }

    public TextNode(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }

    public TextNode text(String text) {
        if (!Objects.equals(text, this.text)) {
            this.text = text;
            invalidateRendererData();
        }
        return this;
    }

    public Element fill() {
        return fill;
    }

    public TextNode fill(Element fill) {
        if (!Objects.equals(fill, this.fill)) {
            this.fill = fill;
            // invalidateRendererData();
        }
        return this;
    }

    public TextStyle textStyle() {
        return textStyle;
    }

    public TextNode textStyle(TextStyle textStyle) {
        if (!Objects.equals(textStyle, this.textStyle)) {
            this.textStyle = textStyle;
            //invalidateRendererData();
        }
        return this;
    }

    @Override
    public void enumerateStaticChildren(Consumer<Element> consumer) {
        consumer.accept(fill);
    }

    @Override
    protected LayoutResult preferredShapeImpl(BoxConstraints constraints) {
        Rectangle rect = Rectangle.of(textStyle.textSize(text).size().divide(1000));
        return new LayoutResult(rect, this, new RH(constraints, rect));
    }

    @Override
    protected void onShapeApplied(Shape shape, LayoutContext context, List<LayoutResult> dependencies) {
        fill.performLayout(shape, r -> {
        }, List.of());
    }

    private record RH(BoxConstraints c, Shape s) {

    }
}
