package ui10.base;

import ui10.Main6;
import ui10.binding.Observable;
import ui10.binding2.ChangeEvent;
import ui10.binding2.ElementEvent;
import ui10.binding2.Property;
import ui10.decoration.DecorationContext;
import ui10.decoration.css.CSSDecorator;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.*;
import java.util.function.Consumer;

public sealed abstract class Element permits TransientElement, EnduringElement {

    protected Element replacement;

    Object[] props = new Object[4];

    final List<ExternalListener<?>> externalListeners = new ArrayList<>();

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
    protected abstract void enumerateStaticChildren(Consumer<Element> consumer); // this does not honor replacement

    public final Element replacement() {
        return replacement;
    }

    /**
     * This can be used by decorators to provide a replacement element.
     */
    public void replacement(Element e) {
        //if (logicalParent != null)
        //    e.logicalParent = logicalParent;
        this.replacement = e;
        // TODO onChange
    }

    // MISC

    abstract void initLogicalParent(Element logicalParent);

    boolean initialized;

    public void initParent(Element parent) {
        initialized = false;

        initLogicalParent(parent);

        DecorationContext decorationContext = new DecorationContext(this);
        CSSDecorator d = decorator();
        if (d != null)
            d.applySelf(this, decorationContext);
        initFromProps();
        if (d != null)
            d.applyReplacements(this, decorationContext, parent);

        initialized = true;
        Main6.counter++;
        enumerateStaticChildren(e -> {
            Objects.requireNonNull(e);
            e.initParent(this);
        });
    }

    protected CSSDecorator decorator() {
        return getProperty(CSSDecorator.DECORATOR_PROPERTY);
    }

    @Override
    public String toString() {
        return elementName() == null ? super.toString() : elementName() + "@" + Integer.toHexString(hashCode());
    }

    // PROPERTIES
    protected void initFromProps() { // értelmesebb név?
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(Property<T> prop) {
        for (int i = 0; i < props.length; i += 2)
            if (props[i] == null)
                break;
            else if (props[i].equals(prop))
                return (T) props[i + 1];

        if (prop.inheritable)
            return getPropertyFromParent(prop);
        else
            return prop.defaultValue;
    }

    public boolean hasProperty(Property<?> prop) {
        for (int i = 0; i < props.length; i += 2)
            if (props[i] == null)
                break;
            else if (props[i].equals(prop))
                return true;

        if (prop.inheritable)
            return hasPropertyInParent(prop);
        else
            return false;
    }

    abstract boolean hasPropertyInParent(Property<?> prop);

    public boolean hasPropertyInSelf(Property<?> prop) {
        for (int i = 0; i < props.length; i += 2)
            if (props[i] == null)
                break;
            else if (props[i].equals(prop))
                return true;
        return false;
    }

    abstract <T> T getPropertyFromParent(Property<T> prop);

    public <T> void setProperty(Property<T> prop, T value) {
        for (int i = 0; i < props.length; i += 2) {
            if (props[i] == null || props[i].equals(prop)) {
                props[i] = prop;
                props[i + 1] = value;
                return;
            }
        }

        int i = props.length;
        props = Arrays.copyOf(props, i * 3);
        props[i] = prop;
        props[i + 1] = value;
    }

    public <T> void removeProperty(Property<T> prop) {
        for (int i = 0; i < props.length; i += 2) {
            if (props[i] == null || props[i].equals(prop)) {
                System.arraycopy(props, i + 2, props, i, props.length - i);
                props[props.length - 2] = null;
                props[props.length - 1] = null;
                return;
            }
        }

        throw new RuntimeException("property not set: " + prop);
    }

    public <T> Observable<? extends ElementEvent> observable(Property<T> prop) {
        return new Observable<>() {
            @Override
            public void subscribe(Consumer<? super ElementEvent> subscriber) {
                externalListeners.add(new ExternalListener<>(prop, subscriber));
            }

            @Override
            public void unsubscribe(Consumer<? super ElementEvent> subscriber) {
                throw new UnsupportedOperationException("TODO");
            }
        };
    }

    record ExternalListener<T>(Property<T> prop, Consumer<? super ElementEvent> consumer) {
    }
}
