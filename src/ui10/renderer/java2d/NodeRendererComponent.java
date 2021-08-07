package ui10.renderer.java2d;

import ui10.binding.ScalarProperty;
import ui10.geom.Point;
import ui10.input.EventTarget;
import ui10.input.InputEvent;
import ui10.input.pointer.MouseEvent.MousePressEvent;
import ui10.input.pointer.MouseEvent.MouseReleaseEvent;
import ui10.nodes.Context;
import ui10.nodes.Node;
import ui10.nodes.EventLoop;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.time.Instant;

import static java.awt.event.MouseEvent.MOUSE_PRESSED;
import static java.awt.event.MouseEvent.MOUSE_RELEASED;

public class NodeRendererComponent extends Canvas {

    public final ScalarProperty<Node> root = ScalarProperty.create();

    private final EventLoop eventLoop;
    private J2DRenderer renderer;
    final AWTInputEnvironment inputEnvironment = new AWTInputEnvironment();
    public final Context context;

    public NodeRendererComponent(EventLoop eventLoop) {
        this.eventLoop = eventLoop;
        root.getAndSubscribe(v -> {
            eventLoop.runLater(() -> {
                if (v != null)
                    renderer = new J2DRenderer(root.get(), this::requestRepaint, this);
                else if (renderer != null) {
                    renderer.dispose();
                    renderer = null;
                }
            });
        });
        context = new Context(eventLoop, inputEnvironment);

        root.subscribe(evt -> requestRepaint());

        enableEvents(AWTEvent.MOUSE_EVENT_MASK| AWTEvent.KEY_EVENT_MASK);
    }

    @Override
    public void paint(Graphics g) {
        requestRepaint();
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        if (e.isConsumed())
            return;

        for (ParentRenderItem t : renderer.mouseTargets) {
            if (t.bounds.contains(e.getPoint())) {
                dispatchMouseEvent(t.mouseTarget, e);
                return;
            }
        }

        switch (e.getID()) {
            case MOUSE_PRESSED, MOUSE_RELEASED -> Toolkit.getDefaultToolkit().beep();
        }
    }

    @Override
    protected void processKeyEvent(KeyEvent e) {
        super.processKeyEvent(e);
        if (e.isConsumed())
            return;

        eventLoop.runLater(()->{
            System.out.println(e);

            if (e.getID() == KeyEvent.KEY_PRESSED) {
                inputEnvironment.dispatchEvent(new AWTKeyTypeEvent(e));
            }
        });
    }

    private void dispatchEvent(EventTarget eventTarget, InputEvent event) {
        eventTarget.eventHandlers.forEach(e->e.bubble(event)); // TODO
    }

    private void dispatchMouseEvent(EventTarget mouseTarget, MouseEvent e) {
        switch (e.getID()) {
            case MOUSE_PRESSED:
                // System.out.println("press"+mouseTarget.pressedButtons);
                dispatchEvent(mouseTarget, new MousePressEvent(Point.ORIGO, translateMouseButton(e.getButton())));
                break;
            case MOUSE_RELEASED:
                // System.out.println("release"+e.getButton());
                dispatchEvent(mouseTarget, new MouseReleaseEvent(Point.ORIGO, translateMouseButton(e.getButton())));
                break;
        }
    }

    private ui10.input.pointer.MouseEvent.MouseButton translateMouseButton(int mouseButton) {
        switch (mouseButton) {
            case MouseEvent.BUTTON1:
                return ui10.input.pointer.MouseEvent.MouseButton.LEFT_BUTTON;
            case MouseEvent.BUTTON2:
                return ui10.input.pointer.MouseEvent.MouseButton.WHEEL;
            case MouseEvent.BUTTON3:
                return ui10.input.pointer.MouseEvent.MouseButton.RIGHT_BUTTON;
            default:
                throw new UnsupportedOperationException(Integer.toString(mouseButton));
                // vagy nyeljük le? vagy logozzuk/beepeljünk?
        }
    }

    private void requestRepaint() {
        eventLoop.runLater(() -> {
            if (!isDisplayable() || renderer == null)
                return;
            if (getBufferStrategy() == null)
                createBufferStrategy(2);

            System.out.println("repaint " + Instant.now());
            renderer.canvasWidth = getWidth();
            renderer.canvasHeight = getHeight();
            BufferStrategy bs = getBufferStrategy();

            do {
                do {
                    Graphics g = bs.getDrawGraphics();
                    renderer.render((Graphics2D) g);
                    g.dispose();
                } while (bs.contentsRestored());
                bs.show();
            } while (bs.contentsLost());
            Toolkit.getDefaultToolkit().sync();
        });
    }
}
