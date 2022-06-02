package ui10.base;

import ui10.Main6;
import ui10.binding.Observable;
import ui10.binding2.ElementEvent;
import ui10.binding2.Property;
import ui10.decoration.DecorationContext;
import ui10.decoration.css.CSSDecorator;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.*;
import java.util.function.Consumer;

public sealed abstract class Element permits TransientElement, EnduringElement  {

    protected Element replacement;

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

        initialized = true;
        Main6.counter++;
        enumerateStaticChildren(e -> {
            Objects.requireNonNull(e);
            e.initParent(this);
        });
    }

    @Override
    public String toString() {
        return elementName() == null ? super.toString() : elementName() + "@" + Integer.toHexString(hashCode());
    }

    // PROPERTIES
    protected void initFromProps() { // értelmesebb név?
    }

}
