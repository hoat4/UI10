package ui10.ui6.graphics;

import ui10.font.TextStyle;
import ui10.geom.Rectangle;
import ui10.geom.shape.Shape;
import ui10.image.Fill;
import ui10.layout.BoxConstraints;
import ui10.ui6.RenderableElement;

import java.util.Objects;

public class TextNode extends RenderableElement {

    private String text;
    private Fill fill;
    private TextStyle textStyle;

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

    public Fill fill() {
        return fill;
    }

    public TextNode fill(Fill fill) {
        this.fill = fill;
        invalidateRendererData();
        return this;
    }

    public TextStyle textStyle() {
        return textStyle;
    }

    public TextNode textStyle(TextStyle textStyle) {
        this.textStyle = textStyle;
        invalidateRendererData();
        return this;
    }

    @Override
    protected Shape preferredShapeImpl(BoxConstraints constraints) {
        return Rectangle.of(textStyle.textSize(text).size());
    }
}
