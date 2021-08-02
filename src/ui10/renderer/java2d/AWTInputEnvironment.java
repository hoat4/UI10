package ui10.renderer.java2d;

import ui10.binding.ScalarProperty;
import ui10.input.EventTargetPane;
import ui10.input.InputEnvironment;
import ui10.input.InputEvent;

public class AWTInputEnvironment implements InputEnvironment {

    private final ScalarProperty<EventTargetPane> focus = ScalarProperty.create();

    public void dispatchEvent(InputEvent event) {
        EventTargetPane p = focus.get();
        if (p == null)
            return;
        p.eventHandler.capture(event);
        if (!event.consumed().get())
            p.eventHandler.bubble(event);
    }
    
    @Override
    public ScalarProperty<EventTargetPane> focus() {
        return focus;
    }

}
