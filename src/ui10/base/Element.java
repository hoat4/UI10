package ui10.ui6;

import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public abstract class Element {

    protected Element replacement;

    // this should become a manual linked list, standard collections has large memory overhead
    private final Set<Attribute> attributes = new HashSet<>();

    // LAYOUT

    protected abstract Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context1);

    protected abstract void performLayoutImpl(Shape shape, LayoutContext2 context);


    // DECORATION

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
     * This can be used by decorators to provide a replacement elementClass.
     *
     * @param e
     */
    public final void replacement(Element e) {
        this.replacement = e;
        // TODO onChange
    }

    // MISC

    public Set<Attribute> attributes() {
        // onChange
        return attributes;
    }
}
