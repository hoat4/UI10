package ui10.ui6.graphics;

import ui10.geom.Size;
import ui10.image.Color;
import ui10.layout.BoxConstraints;
import ui10.ui6.RenderableElement;
import ui10.ui6.LayoutContext1;

public class ColorFill extends RenderableElement {

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
        invalidateRendererData();
        return this;
    }

    @Override
    public Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context1) {
        return constraints.min();
    }
}
