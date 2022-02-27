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
        withClass("button", this);
        textNode.text("Gomb");

        pressed().subscribe(e -> {
            requestLayout();
            System.out.println(e.newValue());
            if (!e.newValue())
                onAction.postEvent(null);
        });
    }

    public ScalarProperty<Boolean> pressed() {
        return pressed;
    }

    @Override
    public Element content() {
        return textNode;
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

}
