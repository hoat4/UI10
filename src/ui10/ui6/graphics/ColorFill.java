package ui10.ui6.graphics;

import ui10.geom.Rectangle;
import ui10.image.Color;
import ui10.layout.BoxConstraints;
import ui10.ui6.RenderableElement;
import ui10.ui6.layout.LayoutResult;

import java.util.List;

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
    protected LayoutResult preferredShapeImpl(BoxConstraints constraints) {
        return new LayoutResult(Rectangle.of(constraints.min()), this, List.of());
    }
}
