package ui10.graphics;

import ui10.base.ContentEditable;
import ui10.base.Element;
import ui10.binding9.OVal;
import ui10.geom.Point;
import ui10.geom.shape.Shape;
import ui10.image.Color;

public class ColorFill extends Element {

    private final OVal<Color> color = new OVal<>();

    public ColorFill() {
    }

    public ColorFill(Color color) {
        this.color.set(color);
    }

    public Color color() {
        return color.get();
    }

    public ColorFill color(Color color) {
        this.color.set(color);
        return this;
    }

    @Override
    public String toString() {
        return "ColorFill {color=" + color/* + ", shape=" + shape*/ + "}";
    }
}
