package ui10.ui6;

import ui10.geom.Insets;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.geom.shape.RoundedRectangle;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

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
        public Shape computeShape(BoxConstraints constraints) {
            return constraints.clamp(content.computeShape(constraints.withMinimum(Size.ZERO)));
        }

        @Override
        public void applyShape(Shape shape, Consumer<Surface> consumer) {
            Rectangle r = shape.bounds();
            Shape contentShape = content.computeShape(new BoxConstraints(Size.ZERO, r.size())).translate(r.topLeft());
            content.applyShape(contentShape, consumer);
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
        public Shape computeShape(BoxConstraints constraints) {
            Size all = insets.all();
            BoxConstraints c = new BoxConstraints(
                    constraints.min().subtractOrClamp(all),
                    constraints.max().subtract(all));

            return content.computeShape(c).withOuterInsets(insets);
        }

        @Override
        public void applyShape(Shape shape, Consumer<Surface> consumer) {
            content.applyShape(shape.withInnerInsets(insets), consumer);
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
        public Shape computeShape(BoxConstraints constraints) {
            return new RoundedRectangle(Rectangle.of(constraints.min()), radius);
        }

        @Override
        public void applyShape(Shape shape, Consumer<Surface> consumer) {
            content.applyShape(shape, consumer);
        }
    }

    public static Element shaped(Element content) {
        return new SingleNodeLayout(content) {
            @Override
            public Shape computeShape(BoxConstraints constraints) {
                return content.computeShape(constraints).bounds();
            }

            @Override
            public void applyShape(Shape shape, Consumer<Surface> consumer) {
                Rectangle r = shape.bounds();
                content.applyShape(content.computeShape(BoxConstraints.fixed(r.size())).translate(r.topLeft()), consumer);
            }
        };
    }

    private static abstract class SingleNodeLayout implements Element {

        protected final Element content;

        public SingleNodeLayout(Element content) {
            this.content = content;
        }
    }

    public static Element stack(Element... nodes) {
        return new StackLayout(nodes);
    }

    private static class StackLayout implements Element {

        private final List<Element> nodes;

        public StackLayout(Element... nodes) {
            this.nodes = List.of(nodes);
        }

        public StackLayout(List<Element> nodes) {
            this.nodes = nodes;
        }

        @Override
        public Shape computeShape(BoxConstraints constraints) {
            return nodes.stream().map(n -> n.computeShape(constraints)).reduce(Shape.NULL, Shape::unionWith);
        }

        @Override
        public void applyShape(Shape shape, Consumer<Surface> consumer) {
            nodes.forEach(n -> n.applyShape(shape, consumer));
        }
    }
}
