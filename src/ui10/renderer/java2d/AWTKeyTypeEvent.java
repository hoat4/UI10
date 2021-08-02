package ui10.renderer.java2d;

import ui10.binding.ScalarProperty;
import ui10.input.keyboard.KeyTypeEvent;

public class AWTKeyTypeEvent implements KeyTypeEvent {

    private final ScalarProperty<Boolean> consumed = ScalarProperty.<Boolean>create().set(false);
    private final String text;

    public AWTKeyTypeEvent(String text) {
        this.text = text;
    }

    @Override
    public ScalarProperty<Boolean> consumed() {
        return consumed;
    }

    @Override
    public String text() {
        return text;
    }
}
