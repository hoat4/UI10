package ui10.ui6.layout;

import ui10.geom.Fraction;
import ui10.geom.Insets;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.geom.shape.RoundedRectangle;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.ui6.Element;
import ui10.ui6.decoration.css.CSSClass;
import ui10.ui6.graphics.Opacity;

import java.util.List;
import java.util.function.Consumer;

public class Layouts {

    public static Element empty() {
        return new Empty();
    }

    private static class Empty extends Element.TransientElement {

        @Override
        public void enumerateStaticChildren(Consumer<Element> consumer) {
        }

        @Override
        protected Shape preferredShapeImpl(BoxConstraints constraints, LayoutContext1 context) {
            return Rectangle.of(constraints.min());
        }

        @Override
        protected void applyShapeImpl(Shape shape, LayoutContext2 context) {
        }
    }

    public static Element wrap(Element element) {
        return new Wrapper(element);
    }

    private static class Wrapper extends SingleNodeLayout {

        public Wrapper(Element content) {
            super(content);
        }

        @Override
        protected Shape preferredShapeImpl(BoxConstraints constraints, LayoutContext1 context) {
            return content.preferredShape(constraints, context);
        }

        @Override
        protected Shape computeContentShape(Shape containerShape, LayoutContext2 context) {
            return containerShape;
        }
    }

    // ez legyen inkább CSSClass-ban?
    public static Element wrapWithClass(String className, Element element) {
        return CSSClass.withClass(className, wrap(element));
    }

    public static Element centered(Element n) {
        return new Centered(n);
    }

    private static class Centered extends SingleNodeLayout {

        public Centered(Element content) {
            super(content);
        }

        @Override
        protected Shape preferredShapeImpl(BoxConstraints constraints, LayoutContext1 context) {
            Shape contentShape = content.preferredShape(constraints.withMinimum(Size.ZERO), context);
            return constraints.clamp(contentShape);
        }


        @Override
        protected Shape computeContentShape(Shape containerShape, LayoutContext2 context) {
            Shape contentShape = content.preferredShape(new BoxConstraints(Size.ZERO, containerShape.bounds().size()), context);

            return contentShape.
                    translate(contentShape.bounds().topLeft().negate()).
                    translate(containerShape.bounds().centered(contentShape.bounds().size()).topLeft());
        }

    }

    public static Element minSize(Element content, Size minSize) {
        return new MinSize(content, minSize);
    }

    private static class MinSize extends SingleNodeLayout {

        private final Size minSize;

        public MinSize(Element content, Size minSize) {
            super(content);
            this.minSize = minSize;
        }

        @Override
        protected Shape preferredShapeImpl(BoxConstraints constraints, LayoutContext1 context) {
            return content.preferredShape(constraints.withMinimum(Size.max(constraints.min(), minSize)), context);
        }

        @Override
        protected Shape computeContentShape(Shape containerShape, LayoutContext2 context) {
            return containerShape;
        }
    }

    public static Element padding(Element content, Insets insets) {
        return new Padding(content, insets);
    }

    private static class Padding extends SingleNodeLayout {
        private final Insets insets;

        public Padding(Element content, Insets insets) {
            super(content);
            this.insets = insets;
        }

        @Override
        protected Shape preferredShapeImpl(BoxConstraints constraints, LayoutContext1 context) {
            Size all = insets.all();

            Shape contentShape = content.preferredShape(new BoxConstraints(
                    constraints.min().subtractOrClamp(all),
                    constraints.max().subtract(all)), context);

            return insets.addTo(contentShape);
        }

        @Override
        protected Shape computeContentShape(Shape containerShape, LayoutContext2 context) {
            return insets.removeFrom(containerShape);
        }
    }

    public static Element roundRectangle(int radius, Element content) {
        return new RoundRectLayout(content, radius);
    }

    private static class RoundRectLayout extends SingleNodeLayout {

        private final int radius;

        public RoundRectLayout(Element content, int radius) {
            super(content);
            this.radius = radius;
        }

        @Override
        protected Shape preferredShapeImpl(BoxConstraints constraints, LayoutContext1 context) {
            Size minSize = Size.max(constraints.min(), new Size(radius * 2, radius * 2));
            Shape contentShape = content.preferredShape(constraints.withMinimum(minSize), context);
            return new RoundedRectangle(contentShape.bounds(), radius);
        }

        @Override
        protected Shape computeContentShape(Shape containerShape, LayoutContext2 context) {
            return new RoundedRectangle(containerShape.bounds(), radius);
        }
    }

    public static Element shaped(Element content) {
        return new SingleNodeLayout(content) {

            @Override
            protected Shape preferredShapeImpl(BoxConstraints constraints, LayoutContext1 context) {
                return content.preferredShape(constraints, context);
            }

            @Override
            protected Shape computeContentShape(Shape containerShape, LayoutContext2 context) {
                Shape contentShape = content.preferredShape(BoxConstraints.fixed(containerShape.bounds().size()), context);
                return contentShape.translate(containerShape.bounds().topLeft());
            }
        };
    }

    public static abstract class SingleNodeLayout extends Element.TransientElement {

        protected final Element content;

        public SingleNodeLayout(Element content) {
            this.content = content;
        }

        @Override
        public void enumerateStaticChildren(Consumer<Element> consumer) {
            consumer.accept(content);
        }

        @Override
        protected void applyShapeImpl(Shape shape, LayoutContext2 context) {
            content.performLayout(computeContentShape(shape, context), context);
        }

        protected abstract Shape computeContentShape(Shape containerShape, LayoutContext2 context);
    }

    public static Element stack(Element... nodes) {
        return new StackLayout(nodes);
    }

    private static class StackLayout extends Element.TransientElement {

        private final List<Element> elements;

        public StackLayout(Element... elements) {
            this.elements = List.of(elements);
        }

        @Override
        public void enumerateStaticChildren(Consumer<Element> consumer) {
            elements.forEach(consumer);
        }

        @Override
        protected Shape preferredShapeImpl(BoxConstraints constraints, LayoutContext1 context) {
            Shape shape = Shape.NULL;
            for (Element e : elements)
                shape = shape.unionWith(e.preferredShape(constraints, context));
            return shape;
        }

        @Override
        protected void applyShapeImpl(Shape shape, LayoutContext2 context) {
            for (Element e : elements)
                e.performLayout(shape, context);
        }
    }

    public static Element withOpacity(Element e, Fraction opacity) {
        return new Opacity(e, opacity);
    }

    public static Element withSize(Element e, Size size) { // paramétersorrend ne fordítva legyen?
        return new WithSize(e, size);
    }

    private static class WithSize extends SingleNodeLayout {

        private final Size size;

        public WithSize(Element content, Size size) {
            super(content);
            this.size = size;
        }

        @Override
        protected Shape preferredShapeImpl(BoxConstraints constraints, LayoutContext1 context) {
            return Rectangle.of(constraints.clamp(size));
        }

        @Override
        protected Shape computeContentShape(Shape containerShape, LayoutContext2 context) {
            return containerShape;
        }
    }
}
