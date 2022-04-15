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

    Object[] transientAncestorsProperties;

    public UIContext uiContext() {
        return getProperty(UI_CONTEXT_PROPERTY);
    }

    @SuppressWarnings("unchecked")
    <T> void elHelper(ExternalListener<?> l, ElementEvent evt) {
        if (l.prop().equals(evt.property()))
            l.consumer().accept(evt);
    }

    protected Set<Property<?>> subscriptions() {
        return Set.of();
    }

    @Override
    void initLogicalParent(Element logicalParent) {
        Element e = logicalParent;
        int propCount = 0;
        while (e instanceof TransientElement t) {
            propCount += t.props.length;
            e = t.logicalParent;
        }

        Object[] transientAncestorsProperties = new Object[propCount];

        e = logicalParent;
        int targetIndex = 0;
        while (e instanceof TransientElement t) {
            props:
            for (int i = 0; i < t.props.length; i += 2) {
                for (int j = 0; j < targetIndex; j += 2) {
                    if (transientAncestorsProperties[j] == null)
                        break;
                    if (transientAncestorsProperties[j].equals(t.props[i]))
                        continue props;
                }

                transientAncestorsProperties[targetIndex++] = t.props[i];
                transientAncestorsProperties[targetIndex++] = t.props[i + 1];
            }

            e = t.logicalParent;
        }
        this.transientAncestorsProperties = transientAncestorsProperties;
        parent = (EnduringElement) e;
    }

    @SuppressWarnings("unchecked")
    @Override
    <T> T getPropertyFromParent(Property<T> prop) {
        if (transientAncestorsProperties != null) {
            for (int i = 0; i < transientAncestorsProperties.length; i++) {
                Object p = transientAncestorsProperties[i];
                if (p == null)
                    break;
                if (p.equals(prop))
                    return (T) transientAncestorsProperties[i + 1];
            }
        }

        if (parent == null)
            return prop.defaultValue;
        return parent.getProperty(prop);
    }

    @Override
    boolean hasPropertyInParent(Property<?> prop) {
        return hasPropertyInTransientAncestor(prop) || parent != null && parent.hasProperty(prop);
    }

    boolean hasPropertyInTransientAncestor(Property<?> property) {
        if (transientAncestorsProperties != null)
            for (int i = 0; i < transientAncestorsProperties.length; i++) {
                Object p = transientAncestorsProperties[i];
                if (p == null)
                    break;
                if (p.equals(property))
                    return true;
            }
        return false;
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
        T prevValue = getProperty(prop);
        super.setProperty(prop, value);
        dispatchElementEvent(new ChangeEvent(prop, prevValue, value));
    }


    public abstract void invalidateDecoration(); // this seems like an ad-hoc thing, should rethink it

}
