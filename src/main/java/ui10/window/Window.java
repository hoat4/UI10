package ui10.window;

import ui10.base.ElementExtra;
import ui10.base.EnduringElement;

public class Window extends ElementExtra {

    public static Window of(EnduringElement element) {
        return element.extra(Window.class);
    }

    public static Window getOrCreate(EnduringElement element) {
        Window w = element.extra(Window.class);
        if (w == null)
            element.extras.add(w = new Window());
        return w;
    }
}
