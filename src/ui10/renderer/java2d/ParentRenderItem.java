package ui10.renderer.java2d;

import ui10.input.MouseTarget;
import ui10.pane.Frame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class ParentRenderItem extends RenderItem {

    private final Frame frame;
    public MouseTarget mouseTarget;

    public ParentRenderItem(Frame frame) {
        this.frame = frame;
    }

    @Override
    Rectangle2D computeBounds(AffineTransform transform) {
        return transform.createTransformedShape(J2DUtil.rect(frame.bounds().get())).getBounds2D();
    }

    @Override
    public void draw(Graphics2D g) {
        throw new UnsupportedOperationException();
    }
}
