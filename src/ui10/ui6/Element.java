package ui10.ui6;

import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.ui6.layout.LayoutContext1;
import ui10.ui6.layout.LayoutContext2;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public interface Element {

    // LAYOUT

    Shape preferredShape(BoxConstraints constraints, LayoutContext1 context); // this honors replacement

    void performLayout(Shape shape, LayoutContext2 context); // this also honors replacement

    // DECORATION

    /**
     * This can be used by decorators to walk the elementClass tree. When encountering a Pane, the decorator should
     * set the Pane.decorator field because Panes usually recreates its children every time, so decorating them once
     * is useless.
     */
    void enumerateStaticChildren(Consumer<Element> consumer); // this does not honor replacement

    Element replacement();

    /**
     * This can be used by decorators to provide a replacement elementClass.
     *
     * @param e
     */
    void replacement(Element e);

    // MISC

    Set<Attribute> attributes();

    abstract class TransientElement implements Element {

        private Element replacement;
        private boolean inReplacement;
        private final Set<Attribute> attributes = new HashSet<>();

        @Override
        public abstract void enumerateStaticChildren(Consumer<Element> consumer);

        @Override
        public Shape preferredShape(BoxConstraints constraints, LayoutContext1 context) {
            if (replacement == null || inReplacement) {
                Shape preferredShape = preferredShapeImpl(constraints, context);
                preferredShape = preferredShape.translate(preferredShape.bounds().topLeft().negate());
                Objects.requireNonNull(preferredShape, () -> this + " returned null preferred layout");
                return preferredShape;
            } else {
                boolean r = inReplacement;
                inReplacement = true;
                try {
                    return replacement.preferredShape(constraints, context);
                } finally {
                    inReplacement = r;
                }
            }
        }

        protected abstract Shape preferredShapeImpl(BoxConstraints constraints, LayoutContext1 context);

        @Override
        public void performLayout(Shape shape, LayoutContext2 context) {
            if (replacement == null || inReplacement) {
                applyShapeImpl(shape, context);
            } else {
                boolean r = inReplacement;
                inReplacement = true;
                try {
                    replacement.performLayout(shape, context);
                } finally {
                    inReplacement = r;
                }
            }
        }

        protected abstract void applyShapeImpl(Shape shape, LayoutContext2 context);

        @Override
        public Element replacement() {
            return replacement;
        }

        @Override
        public void replacement(Element e) {
            this.replacement = e;
        }

        @Override
        public Set<Attribute> attributes() {
            return attributes;
        }
    }
}
