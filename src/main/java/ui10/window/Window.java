package ui10.window;

import ui10.base.Element;
import ui10.base.Container;
import ui10.base.FocusContext;
import ui10.binding2.Property;

public abstract class Window extends Container {

    public final FocusContext focusContext = new FocusContext();

    public static Window of(Element node) {
        return new Window() {
            @Override
            public Element content() {
                return node;
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(Property<T> prop) {
        if (prop.equals(FOCUS_CONTEXT_PROPERTY))
            return (T) focusContext;
        else
            return super.getProperty(prop);
    }
}
