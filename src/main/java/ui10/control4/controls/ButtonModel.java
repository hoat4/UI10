package ui10.control4.controls;

import ui10.base.ElementModel;
import ui10.controls.Action;

public abstract class ButtonModel extends ElementModel<ButtonModel.ButtonModelListener> {

    private boolean pressed;

    public abstract String text();

    public abstract void performAction();

    public boolean enabled() {
        return true;
    }

    public boolean pressed() {
        return pressed;
    }

    public void pressed(boolean pressed) {
        this.pressed = pressed;
        listener().pressedChanged();
    }

    public static class OfAction extends ButtonModel {

        public final Action action;

        public OfAction(Action action) {
            this.action = action;
            action.subscribe(e -> {
                // TODO
            });
        }

        @Override
        public void performAction() {
            action.performAction();
        }

        @Override
        public String text() {
            return action.text();
        }
    }

    public interface ButtonModelListener extends ElementModelListener {

        void textChanged();

        void enabledChanged();

        void pressedChanged();
    }
}
