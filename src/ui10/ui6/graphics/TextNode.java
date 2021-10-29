package ui10.ui6.graphics;

import ui10.font.TextStyle;
import ui10.geom.Rectangle;
import ui10.geom.shape.Shape;
import ui10.image.Fill;
import ui10.layout.BoxConstraints;
import ui10.ui6.Surface;

import java.util.Objects;

public class TextNode extends Surface {

    private String text;
    private Fill fill;
    private TextStyle textStyle;

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
    public Shape computeShape(BoxConstraints constraints) {
        return Rectangle.of(textStyle.textSize(text).size());
    }
}
