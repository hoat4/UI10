package ui10.controls;

import ui10.base.Control;
import ui10.base.Element;
import ui10.base.EventContext;
import ui10.graphics.TextNode;
import ui10.input.pointer.MouseEvent;
import ui10.layout.Layouts;

import static ui10.controls.Button.PRESSED_PROPERTY;
import static ui10.decoration.css.CSSClass.withClass;

public class ButtonView2 extends Control {

    public final Action action;

    private final TextNode textNode = withClass("button-text", new TextNode());

    public ButtonView2(Action action) {
        this.action = action;
    }

    @Override
    public String elementName() {
        return "Button";
    }

    @Override
    protected Element content() {
        textNode.text(action.text());
        if (action.equals(focusContext().defaultAction.get()))
            withClass("default-button", this);
        return Layouts.centered(textNode);
    }

    @EventHandler
    private void onMousePress(MouseEvent.MousePressEvent event, EventContext eventContext) {
        focusContext().focusedControl.set(this);
        setProperty(PRESSED_PROPERTY, true);
    }

    @EventHandler
    private void onMouseRelease(MouseEvent.MouseReleaseEvent event, EventContext eventContext) {
        setProperty(PRESSED_PROPERTY, false);
        action.perform();
    }

}
