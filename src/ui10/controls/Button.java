package ui10.controls;

import ui10.binding.EventBus;
import ui10.binding.ScalarProperty;
import ui10.binding.StandaloneEventBus;
import ui10.binding.impl.SelfContainedScalarProperty;
import ui10.input.pointer.MouseEvent;
import ui10.base.*;
import ui10.decoration.css.CSSPseudoClass;
import ui10.graphics.TextNode;

import static ui10.decoration.css.CSSClass.withClass;

public class Button extends Control {

    private final TextNode textNode = withClass("button-text", new TextNode());

    public final EventBus<Void> onAction = new StandaloneEventBus<>();

    private final ScalarProperty<Boolean> pressed = new SelfContainedScalarProperty<>("pressed");

    {
        pressed().subscribe(e -> {
            invalidate();
            System.out.println(e.newValue());
            if (!e.newValue())
                onAction.postEvent(null);
        });
    }

    public Button() {
    }

    public Button(String text) {
        text(text);
    }

    public ScalarProperty<Boolean> pressed() {
        return pressed;
    }

    @Override
    public Element content() {
        return textNode;
    }

    public void text(String text) {
        textNode.text(text);
    }

    public String text() {
        return textNode.text();
    }

    @EventHandler
    private void onMousePress(MouseEvent.MousePressEvent event, EventContext eventContext) {
        focusContext.focusedControl.set(this);

        pressed().set(true);
        attributes().add(new CSSPseudoClass("active"));
    }

    @EventHandler
    private void onMouseRelease(MouseEvent.MouseReleaseEvent event, EventContext eventContext) {
        pressed().set(false);
        attributes().remove(new CSSPseudoClass("active"));
    }

    @Override
    public String elementName() {
        return "Button";
    }
}
