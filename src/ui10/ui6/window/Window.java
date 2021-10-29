package ui10.ui6.window;

import ui10.ui6.Decorable;
import ui10.ui6.Element;

public abstract class Window extends Decorable {

    public static Window of(Element node) {
        return new Window() {
            @Override
            protected Element innerContent() {
                return node;
            }
        };
    }
}
