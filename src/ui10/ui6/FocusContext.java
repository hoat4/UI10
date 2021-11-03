package ui10.ui6;

import ui10.binding.ScalarProperty;

public class FocusContext {

    public final ScalarProperty<Control> focusedControl = ScalarProperty.create("focusedControl");

    {
        focusedControl.subscribe(e->{
            if (e.oldValue() != null)
                e.oldValue().onFocusLost();
            if (e.newValue() != null)
                e.newValue().onFocusGain();
        });
    }
}
