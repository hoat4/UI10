package ui10.renderer6.java2d;

import ui10.input.pointer.MouseEvent;
import ui10.ui6.EventHandler;
import ui10.ui6.Pane;
import ui10.ui6.Surface;

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
        for (Surface n : node.children)
            children.add(renderer.makeItem(n));
    }

    @Override
    protected void drawImpl(Graphics2D g) {
        AffineTransform t = g.getTransform();
        for (Item<?> item : children) {
            // g.setClip
            //g.translate(item., item.y);
            item.draw(g);
            //g.setTransform(t);
        }
    }

    @Override
    public boolean captureMouseEvent(MouseEvent p, List<EventHandler> l) {
        if (node.eventHandler != null) {
            if (node.eventHandler.capture(p))
                return true;

            l.add(node.eventHandler);
        }

        for (Item<?> item : children)  {
            if (item.shape.contains(J2DUtil.point(p.point()))) {
                return item.captureMouseEvent(p, l);
                //return item.captureMouseEvent(p.subtract(new Point(item.x, item.y)), l);
            }
        }
        return false;
    }

}
