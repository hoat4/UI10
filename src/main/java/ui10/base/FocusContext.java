package ui10.base;

import ui10.binding.ScalarProperty;
import ui10.controls.Action;

public class FocusContext {

    public final ScalarProperty<Element> focusedControl = ScalarProperty.create("focusedControl");
    public final ScalarProperty<Element> hoveredControl = ScalarProperty.create("hoveredControl");

    public final ScalarProperty<Action> defaultAction = ScalarProperty.create("defaultAction");

    {
        focusedControl.subscribe(e->{
            if (e.oldValue() instanceof InputHandler h)
                h.onFocusLost();
            if (e.newValue() instanceof InputHandler h)
                h.onFocusGain();
        });
        /*
        hoveredControl.subscribe(e->{
            if (e.oldValue() != null)
                e.oldValue().setProperty(Control.HOVERED_PROPERTY, false);
            if (e.newValue() != null)
                e.newValue().setProperty(Control.HOVERED_PROPERTY, true);
        });

         */
    }
}
