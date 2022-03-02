package ui10.graphics;

import ui10.font.TextStyle;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.base.Element;
import ui10.base.LayoutContext2;
import ui10.base.RenderableElement;
import ui10.base.LayoutContext1;

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
            invalidate();
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
    public Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context1) {
        return textStyle.textSize(text).size();
    }

    @Override
    protected void onShapeApplied(Shape shape, LayoutContext2 context) {
        LayoutContext2.ignoring().placeElement(fill, shape);
    }
}
