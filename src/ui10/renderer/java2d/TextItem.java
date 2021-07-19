package ui10.renderer.java2d;

import ui10.font.FontContext;
import ui10.geom.Rectangle;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class TextItem extends RenderItem {

    String text;
    FontContext font;
    J2DFontRenderer fontRenderer;

    @Override
    Rectangle2D computeBounds(AffineTransform transform) {
        final Rectangle r = Rectangle.of(fontRenderer.measure(text, font).size());
        return transform.createTransformedShape(J2DUtil.rect(r)).getBounds2D();
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.drawString(text, 0, (float) fontRenderer.measure(text, font).ascent().toDouble());
    }
}
