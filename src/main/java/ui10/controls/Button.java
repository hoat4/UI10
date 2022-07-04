package ui10.controls;

import ui10.base.Element;
import ui10.base.ElementModel;
import ui10.binding9.OVal;

public class Button extends ElementModel {

    public final Action action;
    public final Element content;

    public final OVal<PressDetector.ButtonState> state = new OVal<>(new PressDetector.ButtonState(false, false, false)) {
        @Override
        protected void afterChange(PressDetector.ButtonState oldValue, PressDetector.ButtonState newValue) {
            if (oldValue.press() && !newValue.press())
                Button.this.action.execute();
        }
    };

    public Button(Element content, Action action) {
        this.content = content;
        this.action = action;
    }

    public interface Action {

        default boolean isEnabled() {
            return true;
        }

        void execute();
    }
}
