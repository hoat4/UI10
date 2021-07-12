package ui10.renderer.java2d;

import java.awt.*;

class J2DFillNode extends J2DNode {

    public final Paint fill;
    public final Shape shape;

    public J2DFillNode(Stroke stroke, Paint fill, Shape shape) {
        this.fill = fill;
        this.shape = shape;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setPaint(fill);
        g.fill(shape);
    }
}
