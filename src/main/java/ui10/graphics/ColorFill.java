package ui10.graphics;

import ui10.base.ElementModel;
import ui10.image.Color;

public class ColorFill extends ElementModel<ColorFill.ColorFillListener> {

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
        listener().colorChanged();
        return this;
    }

    @Override
    public String toString() {
        return "ColorFill {color=" + color/* + ", shape=" + shape*/ + "}";
    }

    public interface ColorFillListener extends  ElementModelListener{

        void colorChanged();
    }
}
