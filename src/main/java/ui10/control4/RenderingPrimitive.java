package ui10.control4;

import ui10.base.ElementModel;
import ui10.base.RenderableElement;

public abstract class RenderingPrimitive<M extends ElementModel<?>> extends RenderableElement {

    protected final M model;

    public RenderingPrimitive(M model) {
        this.model = model;
    }
}
