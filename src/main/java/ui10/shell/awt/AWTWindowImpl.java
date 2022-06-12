package ui10.shell.awt;

import ui10.base.*;
import ui10.geom.Point;
import ui10.input.keyboard.KeyTypeEvent;
import ui10.input.pointer.MouseEvent;
import ui10.shell.renderer.java2d.J2DRenderer;
import ui10.shell.renderer.java2d.J2DUtil;
import ui10.window.Cursor;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AWTWindowImpl extends Frame {

    public final AWTRenderer renderer;
    private final EnduringElement window;
    private final AWTDesktop desktop;
    private final int scale;

    public AWTWindowImpl(EnduringElement window, AWTDesktop desktop, int scale) throws HeadlessException {
        this.window = window;
        this.desktop = desktop;
        this.scale = scale;

        enableEvents(AWTEvent.WINDOW_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK
                | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
        setTitle("Ablak");

        addNotify();
        createBufferStrategy(2);

        //renderer = new AwtSwRenderer();
        renderer = new J2DRenderer(desktop);
        renderer.awtWindow = this;
    }

    public void applySize() {
        renderer.uiContext.requestLayout(new UIContext.LayoutTask(window, () -> {
            Rectangle rect = new Rectangle(
                    (getWidth() - getInsets().left - getInsets().right) / scale,
                    (getHeight() - getInsets().top - getInsets().bottom) / scale);
            new LayoutContext2(window) {
                @Override
                public void accept(RenderableElement element) {
                }
            }.placeElement(window, J2DUtil.rect(rect));
        }));
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
        super.processMouseEvent(e);
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

    @Override
    protected void processMouseMotionEvent(java.awt.event.MouseEvent e) {
        super.processMouseMotionEvent(e);
        switch (e.getID()) {
            case java.awt.event.MouseEvent.MOUSE_MOVED:
                dispatchMouseEvent(new MouseEvent.MouseMoveEvent(
                        new Point(e.getX() - getInsets().left, e.getY() - getInsets().top)));
                break;
            case java.awt.event.MouseEvent.MOUSE_DRAGGED:
                dispatchMouseEvent(new MouseEvent.MouseDragEvent(
                        new Point(e.getX() - getInsets().left, e.getY() - getInsets().top)));
                break;
        }
    }

    private void dispatchMouseEvent(MouseEvent e) {
        renderer.uiContext.eventLoop().runLater(() -> {
            List<InputHandler> l = new ArrayList<>();
            EventContext eventContext = new EventContext();
            if (!renderer.captureMouseEvent(e, eventContext, l)) {
                setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
                window.focusContext().hoveredControl.set(null);
                return;
            }
            /*
            Cursor cursor = Cursor.POINTER;
            for (InputHandler control : l) {
                if (control.cursor.get() != null)
                    cursor = control.cursor.get();
            }
            setCursor(switch (cursor) {
                case POINTER -> java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR);
                case TEXT -> java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.TEXT_CURSOR);
            });

            // de mit csinálunk, ha a a controlnak vagy vmelyik ancestorának külön FocusContextje van?
            window.focusContext().hoveredControl.set(l.get(l.size() - 1));
*/
            for (int i = l.size() - 1; i >= 0; i--) {
                if (eventContext.stopPropagation)
                    break;

                InputHandler control = l.get(i);
                MouseEvent translatedEvent = e.subtract(((EnduringElement)control).origin());
                InputHandler.dispatchInputEvent(translatedEvent, control, eventContext, false);
            }
        });
    }

    private void dispatchKeyEvent(KeyTypeEvent e) {
        renderer.uiContext.eventLoop().runLater(() -> {
            EnduringElement focusedControl = window.focusContext().focusedControl.get();
            List<EnduringElement> hierarchy = new ArrayList<>();
            for (EnduringElement re = focusedControl; re != null; re = re.parentRenderable()) {
                if (re instanceof InputHandler)
                    hierarchy.add(0, re);
            }

            EventContext eventContext = new EventContext();
            for (EnduringElement c : hierarchy) {
                InputHandler.dispatchInputEvent(e, (InputHandler) c, eventContext, true);
                if (eventContext.stopPropagation)
                    return;
            }
            Collections.reverse(hierarchy);
            for (EnduringElement c : hierarchy) {
                InputHandler.dispatchInputEvent(e, (InputHandler) c, eventContext, false);
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
