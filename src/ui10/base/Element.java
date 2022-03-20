package ui10.base;

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

public sealed abstract class Element permits TransientElement, RenderableElement {

    protected Element replacement;

    final Map<Property<?>, Object> props = new HashMap<>();

    @Deprecated // use properties instead
    private final Set<Attribute> attributes = new HashSet<>();

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
    public final void replacement(Element e) {
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

        DecorationContext decorationContext = new DecorationContext();
        CSSDecorator d = parent.getProperty(CSSDecorator.DECORATOR_PROPERTY);
        if (d != null)
            d.applySelf(this, decorationContext);
        initFromProps();
        if (d != null)
            d.applyReplacements(this, decorationContext, parent);

        initialized = true;

        enumerateStaticChildren(e -> {
            Objects.requireNonNull(e);
            e.initParent(this);
        });

        Element e = this;
        while (e instanceof TransientElement t)
            e = t.logicalParent;
        if (e != null)
            ((RenderableElement) e).transientDescendantInterestedProperties.addAll(subscriptions());
    }

    protected Set<Property<?>> subscriptions() {
        return Set.of();
    }

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
    protected void initFromProps() { // értelmesebb név?
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

        return getPropertyFromParent(prop);
    }

    abstract <T> T getPropertyFromParent(Property<T> prop);

    public <T> void setProperty(Property<T> prop, T value) {
        props.put(prop, value);
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
