/*
package ui10.shell.renderer.java2d;

import ui10.binding.ScalarProperty;
import ui10.input.*;

public class AWTInputEnvironment implements InputEnvironment {

    private final ScalarProperty<EventTarget> focus = ScalarProperty.create();

    public void dispatchEvent(InputEvent event) {
        EventTarget p = focus.get();
        if (p == null)
            return;

        for (InputEventHandler h : p.eventHandlers) {
            if (h.capture(event))
                return;
        }

        for (InputEventHandler h : p.eventHandlers) {
            if (h.bubble(event))
                return;
        }
    }

    @Override
    public ScalarProperty<EventTarget> focus() {
        return focus;
    }

}
*/