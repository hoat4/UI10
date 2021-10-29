package ui10.ui6.graphics;

import ui10.geom.Rectangle;
import ui10.geom.shape.Shape;
import ui10.image.Color;
import ui10.layout.BoxConstraints;
import ui10.ui6.Surface;

public class ColorFill extends Surface {

    private Color color;

    public ColorFill() {
    }

    public ColorFill(Color color) {
        this.color = color;
    }

    public Color color() {
        return color;
    }

    public ColorFill color(Color color) {
        this.color = color;
        invalidate();
        return this;
    }

    @Override
    public Shape computeShape(BoxConstraints constraints) {
        return Rectangle.of(constraints.min());
    }
}
