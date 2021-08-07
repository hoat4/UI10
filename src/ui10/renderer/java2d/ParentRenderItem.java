package ui10.renderer.java2d;

import ui10.geom.Rectangle;
import ui10.input.EventTarget;
import ui10.nodes.Node;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Objects;

public class ParentRenderItem extends RenderItem {

    private final Node node;
    public EventTarget mouseTarget;

    public ParentRenderItem(Node node) {
        this.node = node;
    }

    @Override
    Rectangle2D computeBounds(AffineTransform transform) {
        Rectangle bounds = Objects.requireNonNull(node.bounds.get(), node+" has no size");
        return transform.createTransformedShape(J2DUtil.rect(bounds)).getBounds2D();
    }

    @Override
    public void draw(Graphics2D g) {
        throw new UnsupportedOperationException();
    }
}
