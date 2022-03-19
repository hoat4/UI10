package ui10.base;

import ui10.binding2.Property;
import ui10.decoration.css.CSSDecorator;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public abstract class Element {

    protected Element replacement;
    public Element logicalParent;

    private final Map<Property<?>, Object> props = new HashMap<>();

    @Deprecated // use properties instead
    private final Set<Attribute> attributes = new HashSet<>();

    // LAYOUT

    // nevek 4-es layoutban computeSize és setBounds voltak
    protected abstract Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context);

    protected abstract void performLayoutImpl(Shape shape, LayoutContext2 context);

    // DECORATION

    public String elementName() {
        return null;
    }

    /**
     * This can be used by decorators to walk the elementClass tree. When encountering a Pane, the decorator should
     * set the Pane.decorator field because Panes usually recreate its children every time, so decorating them only once
     * is useless.
     */
    public abstract void enumerateStaticChildren(Consumer<Element> consumer); // this does not honor replacement

    public final Element replacement() {
        return replacement;
    }

    /**
     * This can be used by decorators to provide a replacement element.
     */
    public final void replacement(Element e) {
        //if (logicalParent != null)
        //    e.logicalParent = logicalParent;
        this.replacement = e;
        // TODO onChange
    }

    /**
     * This should be called if an element is placed that is not enumerated by
     * {@linkplain #enumerateStaticChildren(Consumer)}.
     */
    protected void initChild(Element e) {
        getProperty(CSSDecorator.DECORATOR_PROPERTY).applyOnRegularElement(e, this);
    }
    // MISC

    @Deprecated
    public Set<Attribute> attributes() {
        // onChange
        return attributes;
    }

    @Override
    public String toString() {
        return elementName() == null ? super.toString() : elementName() + "@" + Integer.toHexString(hashCode());
    }

    // PROPERTIES
    public void initFromProps() { // értelmesebb név?
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(Property<T> prop) {
        if (!prop.inheritable) {
            if (props.containsKey(prop))
                return (T) props.get(prop);
            else
                return prop.defaultValue;
        }

        if (props.containsKey(prop))
            return (T) props.get(prop);
        if (logicalParent == null)
            return prop.defaultValue;
        return logicalParent.getProperty(prop);
    }

    public <T> void setProperty(Property<T> prop, T value) {
        props.put(prop, value);
    }
}
