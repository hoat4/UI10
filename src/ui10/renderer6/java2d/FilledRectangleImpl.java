package ui10.renderer6.java2d;

import ui10.image.Fill;
import ui10.ui6.FilledRectangleNode;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.util.Objects;

public class FilledRectangleImpl extends Item<FilledRectangleNode> {

    private Fill prevFill;
    private Paint paint;

    public FilledRectangleImpl(J2DRenderer renderer, FilledRectangleNode node) {
        super(renderer, node);
    }

    @Override
    protected void validate() {
        Objects.requireNonNull(node.fill());
        if (!Objects.equals(prevFill, node.fill())) {
            paint = J2DUtil.asPaint(node.fill());
            prevFill = node.fill();
        }
    }

    @Override
    public void drawImpl(Graphics2D g) {
        Objects.requireNonNull(paint);
        g.setPaint(paint);
        g.fillRect(bounds.topLeft().x(), bounds.topLeft().y(), bounds.size().width(), bounds.size().height());
    }
}
