package ui10.shell.renderer.java2d;

import ui10.base.LayoutContext1;
import ui10.geom.Size;
import ui10.graphics.ColorFill;
import ui10.layout.BoxConstraints;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.util.Objects;

public class J2DColorFillElement extends J2DRenderableElement<ColorFill> {

    private ui10.image.Color prevColor;
    private java.awt.Color awtColor;

    public J2DColorFillElement(J2DRenderer renderer, ColorFill node) {
        super(renderer, node);
    }

    @Override
    protected void validateImpl() {
        Objects.requireNonNull(node.color());
        if (!Objects.equals(prevColor, node.color())) {
            awtColor = J2DUtil.color(node.color());
            prevColor = node.color();
        }
    }

    @Override
    protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
        return constraints.min();
    }

    @Override
    public void drawImpl(Graphics2D g) {
        Objects.requireNonNull(awtColor);
        g.setColor(awtColor);
        g.fill(shape);
    }

    @Override
    public Paint asPaint() {
        validateIfNeeded();
        return awtColor;
    }
}
