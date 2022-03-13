package ui10.shell.awt;

import ui10.geom.Point;
import ui10.base.EventLoop;
import ui10.base.*;

import java.awt.*;

import ui10.input.keyboard.KeyTypeEvent;
import ui10.input.pointer.MouseEvent;
import ui10.base.LayoutContext2;
import ui10.shell.renderer.java2d.J2DRenderer;
import ui10.shell.renderer.java2d.J2DUtil;
import ui10.window.Window;

import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AWTWindowImpl extends Frame implements RendererData {

    private final AWTRenderer renderer;
    private final Window window;
    private final AWTDesktop desktop;
    private final int scale;

    public AWTWindowImpl(Window window, AWTDesktop desktop, int scale) throws HeadlessException {
        this.window = window;
        this.desktop = desktop;
        this.scale = scale;

        enableEvents(AWTEvent.WINDOW_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
        setTitle("Ablak");

        addNotify();
        createBufferStrategy(2);

        //renderer = new AwtSwRenderer();
        renderer = new J2DRenderer();
        renderer.awtWindow = this;
        window.uiContext = renderer.uiContext;
        renderer.initRoot(window);
        window.focusContext = new FocusContext();
    }

    public void applySize() {
        renderer.uiContext.requestLayout(new UIContext.LayoutTask(window, () -> {
            Rectangle rect = new Rectangle(
                    (getWidth() - getInsets().left - getInsets().right) / scale,
                    (getHeight() - getInsets().top - getInsets().bottom) / scale);
            new LayoutContext2() {
                @Override
                public void accept(RenderableElement element) {
                }
            }.placeElement(window, J2DUtil.rect(rect));
        }));
    }

    @Override
    public void invalidateRendererData() {
        renderer.requestRepaint();
    }

    @Override
    public void paint(Graphics g1) {
        try {
            renderer.requestRepaint().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            desktop.windows.remove(window);
            System.exit(0);
        }
    }

    @Override
    protected void processKeyEvent(KeyEvent e) {
        switch (e.getID()) {
            case KeyEvent.KEY_PRESSED -> {
                dispatchKeyEvent(new AWTKeyTypeEvent(e));
            }
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
        renderer.uiContext.eventLoop().runLater(() -> {
            List<Control> l = new ArrayList<>();
            EventContext eventContext = new EventContext();
            if (!renderer.captureMouseEvent(e, eventContext, l))
                return;
            for (int i = l.size() - 1; i >= 0; i--) {
                if (eventContext.stopPropagation)
                    break;

                l.get(i).dispatchInputEvent(e, eventContext, false);
            }
        });
    }

    private void dispatchKeyEvent(KeyTypeEvent e) {
        renderer.uiContext.eventLoop().runLater(() -> {
            Control focusedControl = window.focusContext.focusedControl.get();
            List<Control> hierarchy = new ArrayList<>();
            for (RenderableElement re = focusedControl; re != null; re = re.parent) {
                if (re instanceof Control c)
                    hierarchy.add(0, c);
            }

            EventContext eventContext = new EventContext();
            for (Control c : hierarchy) {
                c.dispatchInputEvent(e, eventContext, true);
                if (eventContext.stopPropagation)
                    return;
            }
            Collections.reverse(hierarchy);
            for (Control c : hierarchy) {
                c.dispatchInputEvent(e, eventContext, false);
                if (eventContext.stopPropagation)
                    return;
            }
        });
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
