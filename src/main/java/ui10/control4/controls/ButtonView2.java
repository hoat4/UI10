package ui10.control4.controls;

import ui10.base.Element;
import ui10.base.EventContext;
import ui10.control4.ControlView2;
import ui10.decoration.d3.Decoration;
import ui10.input.pointer.MouseEvent;
import ui10.layout.Layouts;

public class ButtonView2 extends ControlView2<ButtonModel, Decoration>
        implements ButtonModel.ButtonModelListener {

    private final ButtonLabelModel textNode = new ButtonLabelModel();

    public ButtonView2(ButtonModel model) {
        super(model);
    }

    @Override
    public String elementName() {
        return "Button";
    }

    @Override
    protected Element contentImpl() {
        return textNode;
    }

    @EventHandler
    private void onMousePress(MouseEvent.MousePressEvent event, EventContext eventContext) {
        focusContext().focusedControl.set(this);
        model.pressed(true);
    }

    @EventHandler
    private void onMouseRelease(MouseEvent.MouseReleaseEvent event, EventContext eventContext) {
        model.pressed(false);
        model.performAction();
    }

    @Override
    public void textChanged() {
        textNode.textChanged();
    }

    @Override
    public void enabledChanged() {
        // no behavior changes, only Decoration needs to update
    }

    @Override
    public void pressedChanged() {
        // no behavior changes, only Decoration needs to update
    }

    public class ButtonLabelModel extends LabelModel {

        public void textChanged() {
            listener().textChanged();
        }

        @Override
        public String text() {
            return model.text();
        }
    }

}
