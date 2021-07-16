package ui10.renderer.java2d;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class FillItem extends RenderItem {

    public Paint fill;
    public Shape shape;

    @Override
    Rectangle2D computeBounds(AffineTransform transform) {
        return transform.createTransformedShape(shape).getBounds2D();
    }

    @Override
    public void draw(Graphics2D g) {
        g.setPaint(fill);
        g.fill(shape);
    }
}
