package ui10.ui6;

import ui10.input.InputEvent;

public abstract class Control extends Pane {

    public boolean capture(InputEvent event) {
        return false;
    }

    public abstract boolean bubble(InputEvent event);
}
