package ui10.renderer.java2d;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class StrokeItem extends RenderItem {

    public Stroke stroke;
    public Shape shape;
    public Paint paint;

    @Override
    Rectangle2D computeBounds(AffineTransform transform) {
        return transform.createTransformedShape(shape).getBounds2D();
    }

    @Override
    public void draw(Graphics2D g) {
        g.setStroke(stroke);
        g.setPaint(paint);
        g.draw(shape);
    }
}
