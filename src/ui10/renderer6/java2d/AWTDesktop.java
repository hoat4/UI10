package ui10.renderer6.java2d;

import ui10.binding.ObservableList;
import ui10.nodes.EventLoop;
import ui10.ui6.window.Desktop;
import ui10.ui6.window.Window;

public class AWTDesktop extends Desktop {

    {
        windows.subscribe(ObservableList.simpleListSubscriber(this::showWindow, this::hideWindow));
    }

    private void showWindow(Window window) {
        if (window.rendererData != null)
            throw new IllegalStateException(window + " is already displayed");

        AWTWindowImpl frame = new AWTWindowImpl(window, this);
        window.rendererData = frame;

        frame.addNotify();
        frame.setSize(frame.getInsets().left + 640 + frame.getInsets().right,
                frame.getInsets().top + 480 + frame.getInsets().bottom);
        frame.setLocationRelativeTo(null);
        frame.applySize();
        frame.setVisible(true);
    }

    private void hideWindow(Window window) {
        ((AWTWindowImpl) window.rendererData).dispose();
        window.rendererData = null;
    }
}
