package ui10.ui6.layout;

import ui10.geom.Insets;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.geom.shape.RoundedRectangle;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.ui6.Element;
import ui10.ui6.LayoutContext;

import java.util.List;
import java.util.function.Consumer;

public class Layouts {

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
            return content.preferredShape(new BoxConstraints(Size.ZERO, containerShape.bounds().size())).
                    translate(containerShape.bounds().topLeft());
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

            return content.preferredShape(c).withOuterInsets(insets);
        }

        @Override
        protected Shape computeContentShape(Shape containerShape, LayoutContext context) {
            return containerShape.withInnerInsets(insets);
        }
    }

    public static Element roundRectangle(Element content, int radius) {
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
            return new RoundedRectangle(Rectangle.of(constraints.min()), radius);
        }

        @Override
        protected Shape computeContentShape(Shape containerShape, LayoutContext context) {
            return containerShape;
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
        public void enumerateChildren(Consumer<Element> consumer) {
            consumer.accept(content);
        }

        protected abstract Shape computeContentShape(Shape containerShape, LayoutContext context);

        @Override
        protected void applyShapeImpl(Shape shape, LayoutContext context) {
            context.placeElement(content, computeContentShape(shape, context));
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
        public void enumerateChildren(Consumer<Element> consumer) {
            nodes.forEach(consumer);
        }

        @Override
        protected Shape preferredShapeImpl(BoxConstraints constraints) {
            return nodes.stream().map(n -> n.preferredShape(constraints)).reduce(Shape.NULL, Shape::unionWith);
        }

        @Override
        protected void applyShapeImpl(Shape shape, LayoutContext context) {
            nodes.forEach(n -> context.placeElement(n, shape));
        }
    }
}
