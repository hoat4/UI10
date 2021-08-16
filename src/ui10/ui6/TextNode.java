package ui10.ui6;

import ui10.font.TextStyle;
import ui10.geom.Size;
import ui10.image.Fill;
import ui10.layout.BoxConstraints;

public class TextNode extends RenderableNode {

    private String text;
    private Fill fill;
    private TextStyle textStyle;

    public String text() {
        return text;
    }

    public TextNode text(String text) {
        this.text = text;
        invalidate();
        return this;
    }

    public Fill fill() {
        return fill;
    }

    public TextNode fill(Fill fill) {
        this.fill = fill;
        invalidate();
        return this;
    }

    public TextStyle textStyle() {
        return textStyle;
    }

    public TextNode textStyle(TextStyle textStyle) {
        this.textStyle = textStyle;
        invalidate();
        return this;
    }

    @Override
    public Size computeSize(BoxConstraints constraints) {
        return textStyle.textSize(text).size();
    }

    @Override
    public void applySize(Size size, LayoutNode.LayoutContext layoutContext) {
    }
}
