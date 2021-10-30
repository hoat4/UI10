package ui10.ui6.window;

import ui10.ui6.Element;
import ui10.ui6.Pane;

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
