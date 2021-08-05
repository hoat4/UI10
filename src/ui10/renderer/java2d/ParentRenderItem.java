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
        Objects.requireNonNull(node.size.get(), node+" has no size");
        Rectangle rect = new Rectangle(node.position.get(), node.size.get());
        return transform.createTransformedShape(J2DUtil.rect(rect)).getBounds2D();
    }

    @Override
    public void draw(Graphics2D g) {
        throw new UnsupportedOperationException();
    }
}
