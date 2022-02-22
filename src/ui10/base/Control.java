package ui10.ui6;

import ui10.input.InputEvent;

public abstract class Control extends Pane {

    public void capture(InputEvent event, EventContext context) {
    }

    public abstract void bubble(InputEvent event, EventContext context);

    public void onFocusGain() {
        invalidatePane();
    }

    public void onFocusLost() {
        invalidatePane();
    }
}
