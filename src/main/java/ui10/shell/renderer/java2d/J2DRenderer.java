package ui10.shell.renderer.java2d;

import ui10.base.Control;
import ui10.base.EventContext;
import ui10.graphics.Opacity;
import ui10.base.Container;
import ui10.base.RenderableElement;
import ui10.graphics.ColorFill;
import ui10.graphics.LinearGradient;
import ui10.graphics.TextNode;
import ui10.input.pointer.MouseEvent;
import ui10.shell.awt.AWTDesktop;
import ui10.shell.awt.AWTRenderer;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class J2DRenderer extends AWTRenderer {

    Item<?> root;

    public J2DRenderer(AWTDesktop desktop) {
        super(desktop);
    }

    @Override
    protected void initRoot(RenderableElement root) {
        this.root = makeItem(root);
    }

    @Override
    protected void draw(Graphics2D g) {
        Map<?, ?> desktopHints = (Map<?, ?>) Toolkit.getDefaultToolkit().
                getDesktopProperty("awt.font.desktophints");

        if (desktopHints != null) {
            g.setRenderingHints(desktopHints);
        } else {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
//        System.out.println(desktopHints);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        root.draw(g);
    }

    @Override
    protected boolean captureMouseEvent(MouseEvent e, EventContext eventContext, List<Control> destinationList) {
        return root.captureMouseEvent(e, destinationList, eventContext);
    }

    @SuppressWarnings("unchecked")
    public <N extends RenderableElement> Item<N> makeItem(N n) {
        if (n instanceof ColorFill f)
            return (Item<N>) new ColorFillImpl(this, f);
        else if (n instanceof Container d)
            return (Item<N>) new PaneItem(this, d);
        else if (n instanceof TextNode t)
            return (Item<N>) new TextItem(this, t);
        else if (n instanceof LinearGradient l)
            return (Item<N>) new LinearGradientImpl(this, l);
        else if (n instanceof Opacity o)
            return (Item<N>) new OpacityItem(this, o);
        else
            throw new UnsupportedOperationException(n.toString());
    }
}
