package ui10.renderer6.java2d;

import ui10.input.pointer.MouseEvent;
import ui10.ui6.Control;
import ui10.ui6.EventContext;
import ui10.ui6.Pane;
import ui10.ui6.RenderableElement;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class PaneItem extends Item<Pane> {

    private final List<Item<?>> children = new ArrayList<>();

    public PaneItem(J2DRenderer renderer, Pane node) {
        super(renderer, node);
    }

    @Override
    protected void validateImpl() {
        children.clear();
        for (RenderableElement n : node.renderableElements())
            children.add(renderer.makeItem(n));
    }

    @Override
    protected void drawImpl(Graphics2D g) {
        AffineTransform t = g.getTransform();
        for (Item<?> item : children) {
            assert item != this;
            // g.setClip
            //g.translate(item., item.y);
            item.draw(g);
            //g.setTransform(t);
        }
    }

    @Override
    public boolean captureMouseEvent(MouseEvent p, List<Control> l, EventContext eventContext) {
        if (node instanceof Control c) {
            c.capture(p, eventContext);
            if (eventContext.stopPropagation)
                return true;

            l.add(c);
        }

        for (Item<?> item : children) {
            if (item.shape.contains(J2DUtil.point(p.point())) && item.captureMouseEvent(p, l, eventContext)) {
                return true;
                //return item.captureMouseEvent(p.subtract(new Point(item.x, item.y)), l);
            }
        }

        return node instanceof Control;
    }

}
