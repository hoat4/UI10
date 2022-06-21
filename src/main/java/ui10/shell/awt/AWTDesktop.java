package ui10.shell.awt;

import ui10.base.Element;
import ui10.base.EventLoop;
import ui10.binding.ObservableList;
import ui10.window.Desktop;
import ui10.window.Window;

public class AWTDesktop extends Desktop {

    private static final AWTDesktop INSTANCE = new AWTDesktop();
    private final EventLoop eventLoop = new EventLoop();

    {
        windows.subscribe(ObservableList.simpleListSubscriber(this::showWindow, this::hideWindow));
    }

    private void showWindow(Element element) {
        AWTWindow2 w = new AWTWindow2(this, element);

    }

    private void hideWindow(Element window) {
        AWTWindow2 w = (AWTWindow2) Window.of(window).view;
        w.disposeFrame();
        // ki kéne szedni a viewproviderchainből

        if (windows.isEmpty())
            eventLoop.stop();
    }

    public EventLoop eventLoop() {
        return eventLoop;
    }

    public static AWTDesktop instance() {
        return INSTANCE;
    }
}
