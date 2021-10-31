package ui10.renderer6.java2d;

import ui10.nodes.EventLoop;
import ui10.ui6.Pane;
import ui10.ui6.RenderableElement;
import ui10.ui6.graphics.ColorFill;
import ui10.ui6.graphics.LinearGradient;
import ui10.ui6.graphics.TextNode;

import java.awt.*;
import java.util.Map;

public class J2DRenderer {

    private final EventLoop eventLoop;

    Item<?> root;
    Container c;

    public J2DRenderer(EventLoop eventLoop) {
        this.eventLoop = eventLoop;
    }

    public void draw() {
        Graphics2D g = (Graphics2D) c.getGraphics();

        Map<?, ?> desktopHints = (Map<?, ?>) Toolkit.getDefaultToolkit().
                getDesktopProperty("awt.font.desktophints");

        if (desktopHints != null) {
            g.setRenderingHints(desktopHints);
        } else {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
//        System.out.println(desktopHints);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.translate(c.getInsets().left, c.getInsets().top);


        Rectangle rect = new Rectangle(
                c.getWidth() - c.getInsets().left - c.getInsets().right,
                c.getHeight() - c.getInsets().top - c.getInsets().bottom);
        System.out.println(rect);
        root.node.applyShape(J2DUtil.rect((java.awt.Rectangle) rect), (surface) -> {
        });

        //root.width = rect.width;
        //root.height = rect.height;
        root.draw(g);
        System.err.println("done");
        g.dispose();
    }

    public void requestRepaint() {
        eventLoop.runLater(this::draw);
    }


    @SuppressWarnings("unchecked")
    public <N extends RenderableElement> Item<N> makeItem(N n) {
        if (n instanceof ColorFill f)
            return (Item<N>) new ColorFillImpl(this, f);
        else if (n instanceof Pane d)
            return (Item<N>) new PaneItem(this, d);
        else if (n instanceof TextNode t)
            return (Item<N>) new TextItem(this, t);
        else if (n instanceof LinearGradient l)
            return (Item<N>) new LinearGradientImpl(this, l);
        else
            throw new UnsupportedOperationException(n.toString());
    }
}
