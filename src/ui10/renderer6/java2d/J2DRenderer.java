package ui10.renderer6.java2d;

import ui10.nodes.EventLoop;
import ui10.ui6.graphics.Opacity;
import ui10.ui6.layout.LayoutContext2;
import ui10.ui6.Pane;
import ui10.ui6.RenderableElement;
import ui10.ui6.graphics.ColorFill;
import ui10.ui6.graphics.LinearGradient;
import ui10.ui6.graphics.TextNode;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class J2DRenderer {

    public final UIContextImpl uiContext = new UIContextImpl(this);

    Item<?> root;
    Window c;


    public void draw() {
        long layoutBegin = System.nanoTime();

        uiContext.performLayouts();

        long layoutEnd = System.nanoTime();

        BufferStrategy bs = c.getBufferStrategy();

        do {
            do {
                Graphics2D g = (Graphics2D) bs.getDrawGraphics();

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


                //System.out.println(rect);


                root.draw(g);

                long drawEnd = System.nanoTime();
                System.err.println("Layout " + (layoutEnd - layoutBegin) / 1000 + ", draw " + (drawEnd - layoutEnd) / 1000);

                g.dispose();
            } while (bs.contentsRestored());
            bs.show();
        } while (bs.contentsLost());

        Toolkit.getDefaultToolkit().sync();
    }

    public CompletableFuture<Void> requestRepaint() {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        uiContext.eventLoop().runLater(()->{
            draw();
            cf.complete(null);
        });
        return cf;
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
        else if (n instanceof Opacity o)
            return (Item<N>) new OpacityItem(this, o);
        else
            throw new UnsupportedOperationException(n.toString());
    }
}
