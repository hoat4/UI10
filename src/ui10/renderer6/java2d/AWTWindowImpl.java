package ui10.renderer6.java2d;

import ui10.geom.Point;
import ui10.nodes.EventLoop;
import ui10.ui6.*;

import java.awt.*;

import ui10.input.pointer.MouseEvent;
import ui10.ui6.window.Window;

import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class AWTWindowImpl extends Frame implements RendererData {

    private final J2DRenderer renderer;
    private final Window window;
    private final AWTDesktop desktop;

    public AWTWindowImpl(Window window, AWTDesktop desktop) throws HeadlessException {
        this.window = window;
        this.desktop = desktop;

        renderer = new J2DRenderer(desktop.eventLoop);
        renderer.c = this;
        renderer.root = renderer.makeItem(RenderableElement.of(window.getContent()));

        enableEvents(AWTEvent.WINDOW_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);

        setTitle("Ablak");
    }

    @Override
    public EventLoop eventLoop() {
        return desktop.eventLoop;
    }

    @Override
    public void invalidateRendererData() {
        renderer.requestRepaint();
    }

    @Override
    public void paint(Graphics g1) {
        renderer.requestRepaint();
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            desktop.windows.remove(window);
            System.exit(0);
        }
    }

    @Override
    protected void processMouseEvent(java.awt.event.MouseEvent e) {
        switch (e.getID()) {
            case java.awt.event.MouseEvent.MOUSE_PRESSED:
                dispatchMouseEvent(new ui10.input.pointer.MouseEvent.MousePressEvent(
                        new Point(e.getX() - getInsets().left, e.getY() - getInsets().top),
                        translateMouseButton(e.getButton())));
                break;
            case java.awt.event.MouseEvent.MOUSE_RELEASED:
                dispatchMouseEvent(new MouseEvent.MouseReleaseEvent(
                        new Point(e.getX() - getInsets().left, e.getY() - getInsets().top),
                        translateMouseButton(e.getButton())));
                break;
        }
    }

    private void dispatchMouseEvent(MouseEvent e) {
        List<Control> l = new ArrayList<>();
        EventContext eventContext = new EventContext();
        if (!renderer.root.captureMouseEvent(e, l, eventContext))
            return;
        for (int i = l.size() - 1; i >= 0; i--) {
            if (eventContext.stopPropagation)
                break;

            l.get(i).bubble(e, eventContext);
        }
    }

    private ui10.input.pointer.MouseEvent.MouseButton translateMouseButton(int mouseButton) {
        switch (mouseButton) {
            case java.awt.event.MouseEvent.BUTTON1:
                return ui10.input.pointer.MouseEvent.MouseButton.LEFT_BUTTON;
            case java.awt.event.MouseEvent.BUTTON2:
                return ui10.input.pointer.MouseEvent.MouseButton.WHEEL;
            case java.awt.event.MouseEvent.BUTTON3:
                return ui10.input.pointer.MouseEvent.MouseButton.RIGHT_BUTTON;
            default:
                throw new UnsupportedOperationException(Integer.toString(mouseButton));
                // vagy nyeljük le? vagy logozzuk/beepeljünk?
        }
    }
}
