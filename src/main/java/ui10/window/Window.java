package ui10.window;

import ui10.base.Element;
import ui10.base.Pane;

public abstract class Window extends Pane {

    public static Window of(Element node) {
        return new Window() {
            @Override
            public Element content() {
                return node;
            }
        };
    }
}
