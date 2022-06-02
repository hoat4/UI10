package ui10.control4;

import ui10.base.Container;
import ui10.base.ElementModel;

public abstract class SimpleElementView<M extends ElementModel<?>> extends Container {

    protected final M model;

    public SimpleElementView(M model) {
        this.model = model;
    }
}
