package ui10.ui6;

import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.ui6.layout.LayoutResult;

import java.util.*;
import java.util.function.Consumer;

public interface Element {

    // LAYOUT

    LayoutResult preferredShape(BoxConstraints constraints); // this honors replacement

    void performLayout(Shape shape, LayoutContext context, List<LayoutResult> lr); // this also honors replacement

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
        public LayoutResult preferredShape(BoxConstraints constraints) {
            if (replacement == null || inReplacement) {
                LayoutResult preferredShape = preferredShapeImpl(constraints);
                Objects.requireNonNull(preferredShape, () -> this + " returned null preferred layout");
                assert preferredShape.elementClass() == getClass();
                return preferredShape;
            } else {
                boolean r = inReplacement;
                inReplacement = true;
                try {
                    return replacement.preferredShape(constraints);
                } finally {
                    inReplacement = r;
                }
            }
        }

        protected abstract LayoutResult preferredShapeImpl(BoxConstraints constraints);

        @Override
        public void performLayout(Shape shape, LayoutContext context, List<LayoutResult> lr) {
            if (replacement == null || inReplacement) {
                for (LayoutResult l : lr)
                    assert l.elementClass() == getClass() : this + ", " + l;

                applyShapeImpl(shape, context, lr);
            } else {
                boolean r = inReplacement;
                inReplacement = true;
                try {
                    replacement.performLayout(shape, context, lr);
                } finally {
                    inReplacement = r;
                }
            }
        }

        protected abstract void applyShapeImpl(Shape shape, LayoutContext context, List<LayoutResult> lr);

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
