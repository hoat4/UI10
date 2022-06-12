package ui10.controls;

import ui10.base.ElementModel;

public class Button extends ElementModel<Button.ButtonModelListener> {

    private String text;
    private boolean pressed;
    private Runnable action;

    public Button() {
    }

    public Button(String text, Runnable action) {
        this.text = text;
        this.action = action;
    }

    public String text() {
        return text;
    }

    public void text(String text) {
        this.text = text;
        listener().textChanged();
    }

    public Runnable action() { // ez lehetne csak sima publikus field is
        return action;
    }

    public void action(Runnable action) {
        this.action = action;
    }

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

    public interface ButtonModelListener extends ElementModelListener {

        default void textChanged() {}

        default void enabledChanged() {}

        default void pressedChanged() {}
    }
}
