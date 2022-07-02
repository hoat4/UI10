package ui10.decoration.views;

import ui10.base.Element;
import ui10.binding7.PropertyBasedView;
import ui10.controls.Button;
import ui10.controls.PressDetector;
import ui10.decoration.Style;

public class StyleableButtonView extends PropertyBasedView<Button, Style> {

    public StyleableButtonView(Button model) {
        super(model);
    }

    @Override
    protected void validateImpl() {
    }

    @Override
    protected Element contentImpl() {
        return new PressDetector(model.content, state->{
            if (state.focus())
                focusContext().focusedControl.set(this);
            model.state(state);
        });
    }
}
