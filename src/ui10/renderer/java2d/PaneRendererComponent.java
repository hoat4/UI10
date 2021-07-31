package ui10.renderer.java2d;

import ui10.binding.ScalarProperty;
import ui10.node.EventLoop;
import ui10.nodes2.Pane;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.time.Instant;

public class PaneRendererComponent extends Canvas {

    public final ScalarProperty<Pane> root = ScalarProperty.create();

    private final EventLoop eventLoop;
    private J2DRenderer renderer;

    public PaneRendererComponent(EventLoop eventLoop) {
        this.eventLoop = eventLoop;
        root.getAndSubscribe(v -> {
            eventLoop.runLater(() -> {
                if (v != null)
                    renderer = new J2DRenderer(v, this::requestRepaint, this);
                else if (renderer != null) {
                    renderer.dispose();
                    renderer = null;
                }
            });
        });

        root.subscribe(evt -> requestRepaint());
    }

    @Override
    public void paint(Graphics g) {
        requestRepaint();
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
