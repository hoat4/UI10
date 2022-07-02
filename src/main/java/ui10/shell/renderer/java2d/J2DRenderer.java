package ui10.shell.renderer.java2d;

import ui10.base.*;
import ui10.input.pointer.MouseEvent;
import ui10.shell.awt.AWTDesktop;
import ui10.shell.awt.AWTRenderer;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.List;
import java.util.Map;

public class J2DRenderer extends AWTRenderer {

    J2DRenderableElement<?> root;

    public J2DRenderer(AWTDesktop desktop) {
        super(desktop);
    }

    @Override
    protected void initRoot(Element root) {
        this.root = (J2DRenderableElement<?>) root.renderableElement();
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
    protected boolean captureMouseEvent(MouseEvent e, EventContext eventContext, List<MouseTarget> destinationList) {
        return root.captureMouseEvent(e, destinationList, eventContext);
    }

    @Override
    public ViewProvider createViewProvider() {
        return new J2DViewProvider(this);
    }
}
