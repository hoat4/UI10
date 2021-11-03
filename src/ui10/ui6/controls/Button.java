package ui10.ui6.controls;

import ui10.binding.EventBus;
import ui10.binding.ScalarProperty;
import ui10.binding.StandaloneEventBus;
import ui10.geom.Insets;
import ui10.image.Colors;
import ui10.image.RGBColor;
import ui10.input.InputEvent;
import ui10.input.pointer.MouseEvent;
import ui10.renderer.java2d.AWTTextStyle;
import ui10.ui6.*;
import ui10.ui6.decoration.css.CSSClass;
import ui10.ui6.decoration.css.CSSPseudoClass;
import ui10.ui6.graphics.ColorFill;
import ui10.ui6.graphics.LinearGradient;
import ui10.ui6.graphics.TextNode;
import ui10.ui6.layout.Layouts;

import static ui10.ui6.decoration.css.CSSClass.withClass;
import static ui10.ui6.layout.Layouts.padding;
import static ui10.ui6.layout.Layouts.wrapWithClass;

public class Button extends Control {

    private boolean _pressed;

    private final TextNode textNode = withClass("button-text", new TextNode());

    public final EventBus<Void> onAction = new StandaloneEventBus<>();

    {
        withClass("button", this);
        textNode.text("Gomb");

        pressed().subscribe(e->{
            System.out.println(e.newValue());
            if (!e.newValue())
                onAction.postEvent(null);
        });
    }

    @Override
    public void validate() {
        super.validate();
    }

    public ScalarProperty<Boolean> pressed() {
        return property((Button b) -> b._pressed, (b, v) -> b._pressed = v);
    }

    @Override
    public Element content() {
        return textNode;
    }

    @Override
    public void bubble(InputEvent event, EventContext eventContext) {
        if (event instanceof MouseEvent.MousePressEvent) {
            focusContext.focusedControl.set(this);

            pressed().set(true);
            attributes().add(new CSSPseudoClass("active"));
        }
        if (event instanceof MouseEvent.MouseReleaseEvent) {
            pressed().set(false);
            attributes().remove(new CSSPseudoClass("active"));
        }
    }

}
