package ui10.shell.awt;

import ui10.base.Control;
import ui10.base.EventContext;
import ui10.base.RenderableElement;
import ui10.input.pointer.MouseEvent;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class AWTRenderer {

    public final UIContextImpl uiContext = new UIContextImpl(this);

    java.awt.Window awtWindow;

    void draw() {
        long layoutBegin = System.nanoTime();

        uiContext.performLayouts();

        long layoutEnd = System.nanoTime();

        BufferStrategy bs = awtWindow.getBufferStrategy();

        do {
            do {
                Graphics2D g = (Graphics2D) bs.getDrawGraphics();
                try {
                    g.translate(awtWindow.getInsets().left, awtWindow.getInsets().top);

                    draw(g);
                    //System.out.println(rect);

                    long drawEnd = System.nanoTime();
                    System.err.println("Layout " + (layoutEnd - layoutBegin) / 1000 + ", draw " + (drawEnd - layoutEnd) / 1000);
                } finally {
                    g.dispose();
                }
            } while (bs.contentsRestored());
            bs.show();
        } while (bs.contentsLost());

        Toolkit.getDefaultToolkit().sync();
    }

    public CompletableFuture<Void> requestRepaint() {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        uiContext.eventLoop().runLater(() -> {
            try {
                draw();
                cf.complete(null);
            } catch (Throwable e) {
                e.printStackTrace();
                cf.completeExceptionally(e);
            }
        });
        return cf;
    }

    protected abstract void initRoot(RenderableElement root);

    protected abstract void draw(Graphics2D g);

    protected abstract boolean captureMouseEvent(MouseEvent e, EventContext eventContext, List<Control> destinationList);
}