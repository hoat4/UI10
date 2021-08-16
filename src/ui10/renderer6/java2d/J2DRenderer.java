package ui10.renderer6.java2d;

import ui10.nodes.EventLoop;
import ui10.ui6.*;

import java.awt.*;
import java.util.Map;

public class J2DRenderer {

    private final EventLoop eventLoop = new EventLoop();

    Item<?> root;
    Container c;

    public void draw() {
        Graphics2D g = (Graphics2D) c.getGraphics();

        Map<?, ?> desktopHints = (Map<?, ?>) Toolkit.getDefaultToolkit().
                getDesktopProperty("awt.font.desktophints");

        if (desktopHints != null) {
            g.setRenderingHints(desktopHints);
        } else {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.translate(c.getInsets().left, c.getInsets().top);
        root.draw(g);
        g.dispose();
    }

    public void requestRepaint() {
        eventLoop.runLater(this::draw);
    }

    public Item<?> makeItem(DelegatingPersistentRenderableNode.NodeAndPosition n) {
        Item<?> item = makeItem(n.node());
        item.bounds = n.bounds();
        return item;
    }

    @SuppressWarnings("unchecked")
    public <N extends RenderableNode> Item<N> makeItem(N n) {
        if (n instanceof FilledRectangleNode f)
            return (Item<N>) new FilledRectangleImpl(this, f);
        else if (n instanceof DelegatingPersistentRenderableNode d)
            return (Item<N>) new PersistentItem(this, d);
        else if (n instanceof TextNode t)
            return (Item<N>) new TextItem(this, t);
        else
            throw new UnsupportedOperationException(n.toString());
    }
}
