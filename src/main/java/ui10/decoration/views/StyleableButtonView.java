package ui10.decoration.views;

import ui10.base.Element;
import ui10.base.EventContext;
import ui10.base.InputHandler;
import ui10.controls.Button;
import ui10.controls.Label;
import ui10.decoration.Style;
import ui10.input.pointer.MouseEvent;

// "Button" element name
public class StyleableButtonView extends StyleableView<Button, Style>
        implements Button.ButtonModelListener, InputHandler {

    private final Label textNode = new Label();

    public StyleableButtonView(Button model) {
        super(model);
    }

    @Override
    protected Element contentImpl() {
        return textNode;
    }

    @Override
    @AfterModelAttach
    public void textChanged() {
        textNode.text(model.text());
    }

    @EventHandler
    private void onMousePress(MouseEvent.MousePressEvent event, EventContext eventContext) {
        focusContext().focusedControl.set(this);
        model.pressed(true);
    }

    @EventHandler
    private void onMouseRelease(MouseEvent.MouseReleaseEvent event, EventContext eventContext) {
        model.pressed(false);
        model.action().run();
    }
}
