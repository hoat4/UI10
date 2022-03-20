package ui10.controls;

import ui10.base.Control;
import ui10.base.Element;
import ui10.base.Pane;
import ui10.binding2.ElementEvent;
import ui10.binding2.Property;

import java.util.Set;

public class ControlModel extends Pane {

    protected ControlView<?> view;

    @Override
    protected Element content() {
        return view;
    }

    @Override
    protected Set<Property<?>> subscriptions() {
        return view.modelPropertySubscriptions();
    }

    @Override
    protected void onPropertyChange(ElementEvent changeEvent) {
        super.onPropertyChange(changeEvent);
        view.handleModelEvent(changeEvent);
    }
}
