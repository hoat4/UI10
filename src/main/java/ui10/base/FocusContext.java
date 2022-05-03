package ui10.base;

import ui10.binding.ScalarProperty;
import ui10.controls.Action;
import ui10.controls.Button;

public class FocusContext {

    public final ScalarProperty<Control> focusedControl = ScalarProperty.create("focusedControl");
    public final ScalarProperty<Control> hoveredControl = ScalarProperty.create("hoveredControl");

    public final ScalarProperty<Action> defaultAction = ScalarProperty.create("defaultAction");

    {
        focusedControl.subscribe(e->{
            if (e.oldValue() != null)
                e.oldValue().onFocusLost();
            if (e.newValue() != null)
                e.newValue().onFocusGain();
        });
        hoveredControl.subscribe(e->{
            if (e.oldValue() != null)
                e.oldValue().setProperty(Control.HOVERED_PROPERTY, false);
            if (e.newValue() != null)
                e.newValue().setProperty(Control.HOVERED_PROPERTY, true);
        });
    }
}
