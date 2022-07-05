package ui10.decoration.views;

import ui10.base.Element;
import ui10.controls.Button;
import ui10.controls.Button.ButtonState;
import ui10.decoration.Style;
import ui10.input.Event;

public class StyleableButtonView extends StyleableView<Button, Style> {

    public StyleableButtonView(Button model) {
        super(model);
    }

    @Override
    protected Element contentImpl() {
        return model.content;
    }

    @EventHandler
    private Event.ReleaseCallback mousePress(Event.BeginPress beginPress) {
        model.state.set(new ButtonState(true, true, true));
        return new Event.ReleaseCallback() {
            @Override
            public void commit() {
                model.state.set(new ButtonState(true, true, false));
            }

            @Override
            public void cancel() {
                model.state.set(new ButtonState(true, true, false));
            }
        };
    }
}
