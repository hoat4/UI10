package ui10.shell.awt;

import ui10.base.Element;
import ui10.base.LayoutContext2;
import ui10.base.RenderableElement;
import ui10.base.UIContext;
import ui10.geom.Point;
import ui10.input.Event;
import ui10.input.Phase;
import ui10.input.keyboard.KeyCombination;
import ui10.input.keyboard.KeySymbol;
import ui10.shell.renderer.java2d.J2DRenderer;
import ui10.shell.renderer.java2d.J2DUtil;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.awt.event.KeyEvent.*;

public class AWTWindowImpl extends Frame {

    public final AWTRenderer renderer;
    private final Element window;
    private final AWTDesktop desktop;
    private final int scale;

    public AWTWindowImpl(Element window, AWTDesktop desktop, int scale) throws HeadlessException {
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
    protected void processKeyEvent(KeyEvent event) {
        super.processKeyEvent(event);
        if (event.isConsumed())
            return;
        renderer.uiContext.eventLoop().runLater(() -> {
            switch (event.getID()) {
                case KeyEvent.KEY_PRESSED -> {
                    char ch = event.getKeyChar();
                    if (ch == KeyEvent.CHAR_UNDEFINED || ch == 8 || ch == 127) {
                        KeyCombination keyCombination = keyCombination(event);
                        if (keyCombination != null)
                            dispatchEvent(new Event.KeyCombinationEvent(keyCombination), keyEventHandlerChain());
                    } else
                        dispatchEvent(new Event.EnterContent(new StringSelection(String.valueOf(ch))), keyEventHandlerChain());
                }
            }
        });
    }

    private static KeyCombination keyCombination(KeyEvent keyEvent) {
        KeySymbol key = switch (keyEvent.getKeyCode()) {
            case VK_LEFT -> KeySymbol.StandardFunctionSymbol.LEFT;
            case VK_RIGHT -> KeySymbol.StandardFunctionSymbol.RIGHT;
            case VK_DOWN -> KeySymbol.StandardFunctionSymbol.DOWN;
            case VK_UP -> KeySymbol.StandardFunctionSymbol.UP;
            case VK_BACK_SPACE -> KeySymbol.StandardFunctionSymbol.BACKSPACE;
            case VK_DELETE -> KeySymbol.StandardFunctionSymbol.DELETE;
            default -> null;
        };
        if (key == null)
            return null;
        return new KeyCombination(key);
    }

    private EventResultWrapper<Event.ReleaseCallback> mouseFocus;
    private Point lastMouseDragPos;

    @Override
    protected void processMouseEvent(java.awt.event.MouseEvent e) {
        super.processMouseEvent(e);
        if (e.isConsumed())
            return;

        Point p = new Point(e.getX() - getInsets().left, e.getY() - getInsets().top);
        renderer.uiContext.eventLoop().runLater(() -> {

            switch (e.getID()) {
                case java.awt.event.MouseEvent.MOUSE_PRESSED:
                    List<Element> eventHandlerChain = mouseEventHandlerChain(p);

                    EventResultWrapper<Event.AcceptFocus> acceptFocus = dispatchEvent(new Event.Focus(), eventHandlerChain);
                    if (acceptFocus != null) {
                        if (keyFocusLostListener != null)
                            keyFocusLostListener.focusLost();
                        keyFocus = ancestors(acceptFocus.responder());
                        keyFocusLostListener = acceptFocus.response().focusLostListener();
                    }

                    mouseFocus = dispatchEvent(new Event.BeginPress(p), eventHandlerChain);
                    lastMouseDragPos = p;
                    break;
                case java.awt.event.MouseEvent.MOUSE_RELEASED:
                    if (mouseFocus == null) {
                        System.err.println("Received mouse released event, but no element is pressed");
                        return;
                    }

                    if (!p.equals(lastMouseDragPos))
                        mouseFocus.response.drag(lastMouseDragPos = p);
                    try {
                        if (mouseFocus.responder.getShapeOrFail().contains(p))
                            mouseFocus.response.commit();
                        else
                            mouseFocus.response.cancel();
                    } finally {
                        mouseFocus = null;
                    }
                    break;
            }
        });
    }

    @Override
    protected void processMouseMotionEvent(java.awt.event.MouseEvent e) {
        super.processMouseMotionEvent(e);
        if (e.isConsumed())
            return;
        renderer.uiContext.eventLoop().runLater(() -> {
            Point p = new Point(e.getX() - getInsets().left, e.getY() - getInsets().top);
            switch (e.getID()) {
                case java.awt.event.MouseEvent.MOUSE_MOVED:
                    if (mouseFocus != null) {
                        System.err.println("Received mouse dragged event, but an element is pressed: " + mouseFocus.responder);
                        return;
                    }

//                dispatchMouseEvent(new MouseEvent.MouseMoveEvent(
//                        new Point(e.getX() - getInsets().left, e.getY() - getInsets().top)));
                    break;
                case java.awt.event.MouseEvent.MOUSE_DRAGGED:
                    if (mouseFocus == null) {
                        System.err.println("Received mouse dragged event, but no element is pressed");
                        return;
                    }

                    mouseFocus.response.drag(lastMouseDragPos = p);
                    break;
            }
        });
    }

    private List<Element> keyFocus = new ArrayList<>();
    private Event.FocusLostListener keyFocusLostListener;

    private List<Element> keyEventHandlerChain() {
        for (int i = keyFocus.size() - 1; i >= 0; i--) {
            Element e = keyFocus.get(i);
            List<Element> ancestors = ancestors(e);
            if (ancestors.contains(window))
                return keyFocus = ancestors;
        }
        return Collections.emptyList();
    }

    private static List<Element> ancestors(Element e) {
        List<Element> l = new ArrayList<>();
        while (e != null) {
            l.add(e);
            e = e.parent;
        }
        Collections.reverse(l);
        return l;
    }

    private List<Element> mouseEventHandlerChain(Point point) {
        List<Element> elements = new ArrayList<>();
        renderer.captureMouseEvent(point, elements);
        return ancestors(elements.get(0));
    }

    private <R extends Event.EventResponse> EventResultWrapper<R> dispatchEvent(Event<R> event, List<Element> chain) {
        for (int i = 0; i < chain.size(); i++) {
            Element element = chain.get(i);
            R response = element.handleEvent(event, Phase.CAPTURE);
            if (response != null)
                return new EventResultWrapper<>(element, response);
        }

        for (int i = chain.size() - 1; i >= 0; i--) {
            Element element = chain.get(i);
            R response = element.handleEvent(event, Phase.BUBBLE);
            if (response != null)
                return new EventResultWrapper<>(element, response);
        }

        return null;
    }

    private record EventResultWrapper<R extends Event.EventResponse>(Element responder, R response) {
    }

    /*
    private MouseTarget.DragHandler dragHandler;

    private void dispatchMouseEvent(MouseEvent e) {
        renderer.uiContext.eventLoop().runLater(() -> {
            if (e instanceof MouseEvent.MouseDragEvent drag) {
                if (dragHandler != null)
                    dragHandler.drag(drag);
                return;
            }
            if (e instanceof MouseEvent.MouseReleaseEvent release) {
                if (dragHandler != null) {
                    dragHandler.release(release);
                    dragHandler = null;
                }
                return;
            }

            if (dragHandler != null)
                // ilyenkor inkább nullra kéne állítani dragHandlert és továbbmenni
                // esetleg logozni a hibát
                throw new IllegalStateException();

            List<MouseTarget> l = new ArrayList<>();
            EventContext eventContext = new EventContext();
            renderer.captureMouseEvent(e, eventContext, l);
            if (l.isEmpty()) {
                setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
                window.focusContext().hoveredControl.set(null);
                return;
            }

            ui10.window.Cursor cursor = ui10.window.Cursor.POINTER;
            for (MouseTarget control : l) {
                if (control.cursor() != null)
                    cursor = control.cursor();
            }
            setCursor(switch (cursor) {
                case POINTER -> java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR);
                case TEXT -> java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.TEXT_CURSOR);
            });

            // de mit csinálunk, ha a a controlnak vagy vmelyik ancestorának külön FocusContextje van?
            window.focusContext().hoveredControl.set(l.get(l.size() - 1));

            for (int i = l.size() - 1; i >= 0; i--) {
                if (eventContext.stopPropagation)
                    break;

                MouseTarget control = l.get(i);
                if (e instanceof MouseEvent.MousePressEvent press)
                    dragHandler = control.handlePress(press);
            }
        });
    }

    private void dispatchKeyEvent(KeyTypeEvent e) {
        renderer.uiContext.eventLoop().runLater(() -> {
            Element focusedControl = window.focusContext().focusedControl.get();
            List<Element> hierarchy = new ArrayList<>();
            for (Element re = focusedControl; re != null; re = re.parentRenderable()) {
                if (re instanceof InputHandler)
                    hierarchy.add(0, re);
            }

            EventContext eventContext = new EventContext();
            for (Element c : hierarchy) {
                Element.dispatchInputEvent(e, (InputHandler) c, eventContext, true);
                if (eventContext.stopPropagation)
                    return;
            }
            Collections.reverse(hierarchy);
            for (Element c : hierarchy) {
                Element.dispatchInputEvent(e, (InputHandler) c, eventContext, false);
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
     */
}
