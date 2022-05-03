package ui10.shell.awt;

import ui10.base.Container;
import ui10.base.EventLoop;
import ui10.base.FocusContext;
import ui10.base.LayoutContext1;
import ui10.binding.ObservableList;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;
import ui10.window.Desktop;
import ui10.window.Window;

public class AWTDesktop extends Desktop {

    private final EventLoop eventLoop = new EventLoop();

    {
        windows.subscribe(ObservableList.simpleListSubscriber(this::showWindow, this::hideWindow));
    }

    private void showWindow(Window window) {
        Size size = new LayoutContext1().preferredSize(window,
                new BoxConstraints(Size.ZERO, new Size(Size.INFINITY, Size.INFINITY)));
        //Size size = new Size(640, 480);
        int scale = 1;
        size = size.multiply(scale);

        if (window.rendererData != null)
            throw new IllegalStateException(window + " is already displayed");

        AWTWindowImpl frame = new AWTWindowImpl(window, this, scale);
        window.rendererData = frame;

        frame.addNotify();
        frame.setSize(frame.getInsets().left + size.width() + frame.getInsets().right,
                frame.getInsets().top + size.height() + frame.getInsets().bottom);
        frame.setLocationRelativeTo(null);
        frame.applySize();
        frame.setVisible(true);
    }

    private void hideWindow(Window window) {
        AWTWindowImpl w = (AWTWindowImpl) window.rendererData;
        w.dispose();
        window.rendererData = null;

        if (windows.isEmpty())
            eventLoop.stop();
    }

    public EventLoop eventLoop() {
        return eventLoop;
    }
}
