package ui10.ui6;

import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.function.Consumer;

public interface Element {

    void enumerateChildren(Consumer<Element> consumer); // this does not honor replacement

    Shape preferredShape(BoxConstraints constraints); // this honors replacement

    void applyShape(Shape shape, LayoutContext context); // this also honors replacement

    Element replacement();

    void replacement(Element e);

    abstract class TransientElement implements Element{

        private Element replacement;

        @Override
        public abstract void enumerateChildren(Consumer<Element> consumer);

        @Override
        public Shape preferredShape(BoxConstraints constraints) {
            return replacement == null ? preferredShapeImpl(constraints) : replacement.preferredShape(constraints);
        }

        protected abstract Shape preferredShapeImpl(BoxConstraints constraints);

        @Override
        public void applyShape(Shape shape, LayoutContext context) {
            if (replacement == null)
                applyShapeImpl(shape, context);
            else
                replacement.applyShape(shape, context);
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
    }
}
