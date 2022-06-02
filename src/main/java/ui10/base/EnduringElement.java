package ui10.base;

import ui10.binding2.ElementEvent;

public sealed abstract class EnduringElement extends Element
        permits RenderableElement, ElementModel {

    // content of this field should be deleted if child is removed from container, but this is not implemented
    public EnduringElement parent;

    public <T> T context(Class<T> clazz) {
        return parent.context(clazz);
    }

    public UIContext uiContext() {
        return context(UIContext.class);
    }

    public FocusContext focusContext() {
        return context(FocusContext.class);
    }

    @Override
    // TODO ez legyen package-private
    protected void initLogicalParent(Element logicalParent) {
        Element e = logicalParent;
        while (e instanceof TransientElement t) {
            e = t.logicalParent;
        }

        parent = (EnduringElement) e;
    }

    public RenderableElement parentRenderable() {
        EnduringElement e = parent;
        while (!(e instanceof RenderableElement r)) {
            if (e == null)
                return null;
            e = e.parent;
        }
        return r;
    }

    public abstract void dispatchElementEvent(ElementEvent event);

    public abstract void invalidateDecoration(); // this seems like an ad-hoc thing, should rethink it
}
