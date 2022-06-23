package ui10.controls;

import ui10.binding7.PropertyBasedModel;

public class Button extends PropertyBasedModel<Button.ButtonProperty> {

    private String text;
    private boolean pressed;
    private Runnable action;

    public Button() {
    }

    public Button(String text, Runnable action) {
        this.text = text;
        this.action = action;
        dirtyProperties().add(ButtonProperty.TEXT);
    }

    public String text() {
        return text;
    }

    public void text(String text) {
        this.text = text;
        invalidate(ButtonProperty.TEXT);
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
        invalidate(ButtonProperty.PRESSED);
    }

    public enum ButtonProperty {

        TEXT, PRESSED, ENABLED
    }
}
