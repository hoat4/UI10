package ui10.window;

import ui10.base.Element;
import ui10.base.Container;
import ui10.base.FocusContext;
import ui10.base.UIContext;
import ui10.binding2.Property;

public abstract class Window extends Container {

    public final FocusContext focusContext = new FocusContext();

    public UIContext uiContext;

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
    public <T> T context(Class<T> clazz) {
        if (clazz == UIContext.class)
            return (T) uiContext;
        else if (clazz == FocusContext.class)
            return (T) focusContext;
        else
            throw new IllegalArgumentException("unknown context type: "+clazz);
    }
}
