package ui10.ui6;

import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.ui6.layout.LayoutContext1;
import ui10.ui6.layout.LayoutContext2;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public abstract class Element {

    protected Element replacement;
    protected boolean inReplacement;

    // this should become a manual linked list, standard collections has large memory overhead
    private final Set<Attribute> attributes = new HashSet<>();

    // LAYOUT

    // the returned shape will have bounds with topLeft at origo
    // this honors replacement
    public final Size preferredSize(BoxConstraints constraints, LayoutContext1 context) {
        Objects.requireNonNull(constraints);

        if (replacement == null || inReplacement) {
            Size s = preferredSizeImpl(constraints, context);
            Objects.requireNonNull(s, this::toString);
            if (this instanceof RenderableElement)
                context.addLayoutDependency((RenderableElement) this,
                        new LayoutContext1.LayoutDependency(constraints, ((RenderableElement)this).shape));
            return s;
        } else {
            boolean r = inReplacement;
            inReplacement = true;
            try {
                return replacement.preferredSize(constraints, context);
            } finally {
                inReplacement = r;
            }
        }
    }

    protected abstract Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context1);

    // this also honors replacement
    public final void performLayout(Shape shape, LayoutContext2 context) {
        Objects.requireNonNull(shape);
        Objects.requireNonNull(context);

        if (replacement != null && !inReplacement) {
            boolean r = inReplacement;
            inReplacement = true;
            try {
                replacement.performLayout(shape, context);
            } finally {
                inReplacement = r;
            }
        } else
            performLayoutImpl(shape, context);
    }

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
