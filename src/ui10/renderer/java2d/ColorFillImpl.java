package ui10.renderer.java2d;

import ui10.graphics.ColorFill;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.util.Objects;

public class ColorFillImpl extends Item<ColorFill> {

    private ui10.image.Color prevColor;
    private java.awt.Color awtColor;

    public ColorFillImpl(J2DRenderer renderer, ColorFill node) {
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
