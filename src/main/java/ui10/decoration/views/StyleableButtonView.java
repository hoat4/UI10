package ui10.decoration.views;

import ui10.base.Element;
import ui10.controls.Button;
import ui10.controls.Button.ButtonState;
import ui10.input.EventInterpretation;

public class StyleableButtonView extends StyleableView<Button> {

    public StyleableButtonView(Button model) {
        super(model);
    }

    @Override
    protected Element contentImpl() {
        return model.content;
    }

    @EventHandler
    private EventInterpretation.ReleaseCallback mousePress(EventInterpretation.BeginPress beginPress) {
        model.state.set(new ButtonState(true, true, true));
        return new EventInterpretation.ReleaseCallback() {
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
