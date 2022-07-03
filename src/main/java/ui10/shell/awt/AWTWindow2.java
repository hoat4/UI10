package ui10.shell.awt;

import ui10.base.*;
import ui10.geom.Size;
import ui10.window.Window;

import java.util.function.Consumer;

public class AWTWindow2 extends RootElement {

    private final AWTDesktop desktop;
    private final Element element;
    private AWTWindowImpl frame;
    private ViewProvider contentViewProvider;

    public AWTWindow2(AWTDesktop desktop, Element element) {
        this.desktop = desktop;
        this.element = element;

        int scale = 1;
        frame = new AWTWindowImpl(element, desktop, scale);
        contentViewProvider = frame.renderer.createViewProvider();

        Window window = Window.getOrCreate(element);
        window.view = this;
        element.initParent(this);
        frame.renderer.initRoot(element);

        //Size size = new LayoutContext1().preferredSize(window,
        //        new BoxConstraints(Size.ZERO, new Size(Size.INFINITY, Size.INFINITY)));
        Size size = new Size(640, 480);
        size = size.multiply(scale);

        // TODO if (window.rendererData != null)
        //          throw new IllegalStateException(window + " is already displayed");

        frame.addNotify();
        frame.setSize(frame.getInsets().left + size.width() + frame.getInsets().right,
                frame.getInsets().top + size.height() + frame.getInsets().bottom);
        frame.setLocationRelativeTo(null);
        frame.applySize();
        frame.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void collect(Class<T> type, Consumer<T> consumer) {
        if (type == ViewProvider.class)
            consumer.accept((T) contentViewProvider);
        if (type == UIContext.class)
            consumer.accept((T) frame.renderer.uiContext);
    }
/*
    @Override
    public void invalidateRendererData() {
        frame.renderer.requestRepaint();
    }

 */

    public void disposeFrame() {
        frame.dispose();
    }
}
