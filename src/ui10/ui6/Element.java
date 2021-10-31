package ui10.ui6;

import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public interface Element {

    void enumerateLogicalChildren(Consumer<Element> consumer); // this does not honor replacement

    Shape preferredShape(BoxConstraints constraints); // this honors replacement

    void applyShape(Shape shape, LayoutContext context); // this also honors replacement

    Element replacement();

    void replacement(Element e);

    List<Attribute> attributes();

    abstract class TransientElement implements Element {

        private Element replacement;
        private boolean inReplacement;
        private final List<Attribute> attributes = new ArrayList<>();

        @Override
        public abstract void enumerateLogicalChildren(Consumer<Element> consumer);

        @Override
        public Shape preferredShape(BoxConstraints constraints) {
            if (replacement == null || inReplacement) {
                Shape preferredShape = preferredShapeImpl(constraints);
                Objects.requireNonNull(preferredShape, ()->this+" returned null preferred shape");
                return preferredShape.translate(preferredShape.bounds().topLeft().negate());
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

        protected abstract Shape preferredShapeImpl(BoxConstraints constraints);

        @Override
        public void applyShape(Shape shape, LayoutContext context) {
            if (replacement == null || inReplacement)
                applyShapeImpl(shape, context);
            else {
                boolean r = inReplacement;
                inReplacement = true;
                try {
                    replacement.applyShape(shape, context);
                } finally {
                    inReplacement = r;
                }
            }
        }

        protected abstract void applyShapeImpl(Shape shape, LayoutContext context);

        @Override
        public Element replacement() {
            return replacement;
        }

        @Override
        public void replacement(Element e) {
            this.replacement = e;
        }

        @Override
        public List<Attribute> attributes() {
            return attributes;
        }
    }
}
