package ui10.base;

import ui10.binding9.Bindings;
import ui10.binding9.OVal;

public abstract class Container extends ElementModel {

    public final OVal<Element> contentProp = new OVal<>();

    @Override
    protected void initBeforeView() {
        Bindings.repeatIfInvalidated(() -> contentProp.set(content()));
    }

    protected abstract Element content();

}
