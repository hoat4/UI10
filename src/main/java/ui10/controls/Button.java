package ui10.controls;

import ui10.base.Element;

public class Button extends SmallStateControl<PressDetector.ButtonState> {

    public final Action action;
    public final Element content;

    public Button(Element content, Action action) {
        super(new PressDetector.ButtonState(false, false, false));

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
