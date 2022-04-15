package ui10.shell.awt;

import ui10.binding.ObservableList;
import ui10.geom.Size;
import ui10.window.Desktop;
import ui10.window.Window;

public class AWTDesktop extends Desktop {

    {
        windows.subscribe(ObservableList.simpleListSubscriber(this::showWindow, this::hideWindow));
    }

    private void showWindow(Window window) {
        Size size = new Size(640, 480);
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
        ((AWTWindowImpl) window.rendererData).dispose();
        window.rendererData = null;
    }
}
