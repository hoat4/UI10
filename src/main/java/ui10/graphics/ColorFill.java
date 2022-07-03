package ui10.graphics;

import ui10.base.ElementModel;
import ui10.binding7.InvalidationMark;
import ui10.image.Color;

public class ColorFill extends ElementModel {

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
        invalidate(ColorFillProperty.COLOR);
        return this;
    }

    @Override
    public String toString() {
        return "ColorFill {color=" + color/* + ", shape=" + shape*/ + "}";
    }

    public enum ColorFillProperty implements InvalidationMark {

        COLOR
    }
}
