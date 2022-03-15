package ui10.graphics;

import ui10.decoration.DecorationContext;
import ui10.decoration.Fill;
import ui10.decoration.css.CSSProperty;
import ui10.decoration.css.Length;
import ui10.decoration.css.Styleable;
import ui10.font.TextStyle;
import ui10.geom.Point;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.image.Colors;
import ui10.layout.BoxConstraints;
import ui10.base.Element;
import ui10.base.LayoutContext2;
import ui10.base.RenderableElement;
import ui10.base.LayoutContext1;
import ui10.shell.renderer.java2d.AWTTextStyle;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class TextNode extends RenderableElement implements Styleable {

    private String text = "";
    private Element fill = new ColorFill(Colors.BLACK);
    private TextStyle textStyle;
    private List<HighlightedRange> highlights;

    public TextNode() {
    }

    public TextNode(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }

    public TextNode text(String text) {
        if (text == null)
            text = "";
        if (!Objects.equals(text, this.text)) {
            this.text = text;
            invalidate();
        }
        return this;
    }

    public Element textFill() {
        return fill;
    }

    public TextNode textFill(Element fill) {
        Objects.requireNonNull(fill);

        if (!Objects.equals(fill, this.fill)) {
            this.fill = fill;
            invalidate();
        }
        return this;
    }

    public List<HighlightedRange> highlights() {
        return highlights;
    }

    public void highlights(List<HighlightedRange> highlights) {
        this.highlights = highlights;
        if (!Objects.equals(highlights, highlights))
        invalidate();
    }

    public TextStyle textStyle() {
        return textStyle;
    }

    public TextNode textStyle(TextStyle textStyle) {
        if (!Objects.equals(textStyle, this.textStyle)) {
            this.textStyle = textStyle;
            invalidate();
        }
        return this;
    }

    public int pickTextPos(Point p) {
        int x = p.x();

        int prevW = 0;
        for (int i = 1; i <= text.length(); i++) {
            int w = textStyle.textSize(text.substring(0, i)).width();
            int mid = (prevW + w * 2) / 3;

            if (mid >= x)
                return i - 1;

            prevW = w;
        }
        return text.length();
    }

    @Override
    public void enumerateStaticChildren(Consumer<Element> consumer) {
        consumer.accept(fill);
    }

    @Override
    public Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context1) {
        // TODO mit csináljunk, ha nem stimmel?
        return constraints.clamp(textStyle.textSize(text).size());
    }

    @Override
    protected void onShapeApplied(Shape shape) {
        LayoutContext2.ignoring().placeElement(fill, shape);
    }

    @Override
    public String elementName() {
        return null; // should return an element name?
    }

    @Override
    public <T> void setProperty(CSSProperty<T> property, T value, DecorationContext decorationContext) {
        if (property.equals(CSSProperty.textColor) && value != null)
            textFill(((Fill) value).makeElement(decorationContext));
        if (property.equals(CSSProperty.fontSize) && value != null)
            textStyle(AWTTextStyle.of(decorationContext.length((Length) value)));
    }

    public static record HighlightedRange(int begin, int end, Fill color) {
    }
}
