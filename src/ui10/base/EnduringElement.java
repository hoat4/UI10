package ui10.base;

import ui10.binding2.ChangeEvent;
import ui10.binding2.ElementEvent;
import ui10.binding2.Property;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public sealed abstract class EnduringElement extends Element permits RenderableElement, ControlModel {

    public static final Property<UIContext> UI_CONTEXT_PROPERTY = new Property<>(true);

    // content of this field should be deleted if child is removed from container, but this is not implemented
    public EnduringElement parent;

    Map<Property<?>, Object> transientAncestorsProperties = Map.of();
    Set<Property<?>> transientDescendantInterestedProperties = new HashSet<>();

    public UIContext uiContext() {
        return getProperty(UI_CONTEXT_PROPERTY);
    }

    @SuppressWarnings("unchecked")
    <T> void elHelper(ExternalListener<?> l, ElementEvent evt) {
        if (l.prop().equals(evt.property()))
            l.consumer().accept(evt);
    }

    @Override
    void initLogicalParent(Element logicalParent) {
        Map<Property<?>, Object> map = new HashMap<>();
        Element e = logicalParent;
        while (e instanceof TransientElement t) {
            map.putAll(t.props);
            e = t.logicalParent;
        }
        transientAncestorsProperties = map;
        parent = (EnduringElement) e;
    }

    @SuppressWarnings("unchecked")
    @Override
    <T> T getPropertyFromParent(Property<T> prop) {
        if (transientAncestorsProperties != null && transientAncestorsProperties.containsKey(prop))
            return (T) transientAncestorsProperties.get(prop);

        if (parent == null)
            return prop.defaultValue;
        return parent.getProperty(prop);
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

    @Override
    public <T> void setProperty(Property<T> prop, T value) {
        super.setProperty(prop, value);
        dispatchElementEvent(new ChangeEvent(prop, value));
    }


    public abstract void invalidateDecoration(); // this seems like an ad-hoc thing, should rethink it
}
