package ui10.controls;

import ui10.base.Control;
import ui10.binding2.ElementEvent;
import ui10.binding2.Property;

import java.util.Set;

public abstract class ControlView<M extends ControlModel> extends Control {

    protected final M model;

    public ControlView(M model) {
        this.model = model;
    }

    protected abstract Set<Property<?>> modelPropertySubscriptions();

    protected abstract void handleModelEvent(ElementEvent event);
}
