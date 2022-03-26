package ui10.base;

import ui10.binding2.ElementEvent;
import ui10.binding2.Property;

import java.util.Set;

// ha a viewnak az attribútumai változnak meg, akkor újra kéne dekorálni
public abstract class ControlView<M extends ControlModel> extends Control {

    public final M model;

    public ControlView(M model) {
        this.model = model;
    }

    protected abstract Set<Property<?>> modelPropertySubscriptions();

    protected abstract void handleModelEvent(ElementEvent event);
}
