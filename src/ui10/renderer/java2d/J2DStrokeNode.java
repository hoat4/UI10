package ui10.renderer.java2d;

import java.awt.*;

public class J2DStrokeNode extends J2DNode {

    public final Stroke stroke;
    public final Shape shape;
    public final Paint paint;

    public J2DStrokeNode(Stroke stroke, Shape shape, Paint paint) {
        this.stroke = stroke;
        this.shape = shape;
        this.paint = paint;
    }

    @Override
    void draw(Graphics2D g) {
        g.setStroke(stroke);
        g.setPaint(paint);
        g.draw(shape);
    }
}
