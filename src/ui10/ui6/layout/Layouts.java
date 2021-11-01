package ui10.ui6.layout;

import ui10.geom.Insets;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.geom.shape.RoundedRectangle;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.ui6.Element;
import ui10.ui6.LayoutContext;
import ui10.ui6.decoration.css.CSSClass;

import java.util.List;
import java.util.Objects;
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
        protected Shape preferredShapeImpl(BoxConstraints constraints) {
            return Rectangle.of(constraints.min());
        }

        @Override
        protected void applyShapeImpl(Shape shape, LayoutContext context) {
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
        protected Shape preferredShapeImpl(BoxConstraints constraints) {
            return content.preferredShape(constraints);
        }

        @Override
        protected Shape computeContentShape(Shape containerShape, LayoutContext context) {
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
        protected Shape preferredShapeImpl(BoxConstraints constraints) {
            return constraints.clamp(content.preferredShape(constraints.withMinimum(Size.ZERO)));
        }

        @Override
        protected Shape computeContentShape(Shape containerShape, LayoutContext context) {
            Shape contentShape = content.preferredShape(new BoxConstraints(Size.ZERO, containerShape.bounds().size()));
            return contentShape.
                    translate(contentShape.bounds().topLeft().negate()).
                    translate(containerShape.bounds().centered(contentShape.bounds().size()).topLeft());
        }
    }

    public static Element minSize(Element content, Size minSize) {
        return new MinSize(content, minSize);
    }

    private static class MinSize extends SingleNodeLayout{

        private final Size minSize;

        public MinSize(Element content, Size minSize) {
            super(content);
            this.minSize = minSize;
        }

        @Override
        protected Shape preferredShapeImpl(BoxConstraints constraints) {
            return content.preferredShape(constraints.withMinimum(Size.max(constraints.min(), minSize)));
        }

        @Override
        protected Shape computeContentShape(Shape containerShape, LayoutContext context) {
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
        protected Shape preferredShapeImpl(BoxConstraints constraints) {
            Size all = insets.all();
            BoxConstraints c = new BoxConstraints(
                    constraints.min().subtractOrClamp(all),
                    constraints.max().subtract(all));

            Shape cs = content.preferredShape(c);
            //System.out.println("cs:"+cs.bounds());
            final Shape shape = insets.addTo(cs);
            //System.out.println(insets+" s:"+shape.bounds());
            return shape;
        }

        @Override
        protected Shape computeContentShape(Shape containerShape, LayoutContext context) {
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
        protected Shape preferredShapeImpl(BoxConstraints constraints) {
            Rectangle rect = content.preferredShape(constraints.withMinimum(
                    Size.max(constraints.min(), new Size(radius * 2, radius * 2)))).bounds();
            //return new RoundedRectangle(rect, radius);
            return rect;
        }

        @Override
        protected Shape computeContentShape(Shape containerShape, LayoutContext context) {
            return new RoundedRectangle(containerShape.bounds(), radius);
        }
    }

    public static Element shaped(Element content) {
        return new SingleNodeLayout(content) {

            @Override
            protected Shape preferredShapeImpl(BoxConstraints constraints) {
                return content.preferredShape(constraints).bounds();
            }

            @Override
            protected Shape computeContentShape(Shape containerShape, LayoutContext context) {
                return content.preferredShape(BoxConstraints.fixed(containerShape.bounds().size())).
                        translate(containerShape.bounds().topLeft());
            }
        };
    }

    private static abstract class SingleNodeLayout extends Element.TransientElement {

        protected final Element content;

        public SingleNodeLayout(Element content) {
            this.content = content;
        }

        @Override
        public void enumerateStaticChildren(Consumer<Element> consumer) {
            consumer.accept(content);
        }

        protected abstract Shape computeContentShape(Shape containerShape, LayoutContext context);

        @Override
        protected void applyShapeImpl(Shape shape, LayoutContext context) {
            content.applyShape(computeContentShape(shape, context), context);
        }
    }

    public static Element stack(Element... nodes) {
        return new StackLayout(nodes);
    }

    private static class StackLayout extends Element.TransientElement {

        private final List<Element> nodes;

        public StackLayout(Element... nodes) {
            this.nodes = List.of(nodes);
        }

        public StackLayout(List<Element> nodes) {
            this.nodes = nodes;
        }

        @Override
        public void enumerateStaticChildren(Consumer<Element> consumer) {
            nodes.forEach(consumer);
        }

        @Override
        protected Shape preferredShapeImpl(BoxConstraints constraints) {
            return nodes.stream().map(n -> n.preferredShape(constraints)).
                    peek(Objects::requireNonNull).
                    reduce(Shape.NULL, Shape::unionWith);
        }

        @Override
        protected void applyShapeImpl(Shape shape, LayoutContext context) {
            nodes.forEach(n -> context.placeElement(n, shape));
        }
    }
}
