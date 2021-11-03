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

import java.util.ArrayList;
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
        protected LayoutResult preferredShapeImpl(BoxConstraints constraints) {
            return new LayoutResult(Rectangle.of(constraints.min()), this, List.of());
        }

        @Override
        protected void applyShapeImpl(Shape shape, LayoutContext context, List<LayoutResult> lr) {
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
        protected LayoutResult preferredShapeImpl(BoxConstraints constraints) {
            return content.preferredShape(constraints);
        }

        @Override
        protected void applyShapeImpl(Shape shape, LayoutContext context, List<LayoutResult> lr) {
            content.performLayout(shape, context, unwrap(lr));
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
        protected LayoutResult preferredShapeImpl(BoxConstraints constraints) {
            LayoutResult contentLR = content.preferredShape(constraints.withMinimum(Size.ZERO));
            return new LayoutResult(constraints.clamp(contentLR.shape()), this, contentLR);
        }

        @Override
        protected void applyShapeImpl(Shape containerShape, LayoutContext context, List<LayoutResult> lr) {
            LayoutResult contentLR = content.preferredShape(new BoxConstraints(Size.ZERO, containerShape.bounds().size()));

            Shape contentShape = contentLR.shape();
            contentShape = contentShape.
                    translate(contentShape.bounds().topLeft().negate()).
                    translate(containerShape.bounds().centered(contentShape.bounds().size()).topLeft());

            List<LayoutResult> deps = new ArrayList<>(unwrap(lr));
            deps.add(contentLR);

            content.performLayout(contentShape, context, deps);
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
        protected LayoutResult preferredShapeImpl(BoxConstraints constraints) {
            return content.preferredShape(constraints.withMinimum(Size.max(constraints.min(), minSize)));
        }

        @Override
        protected void applyShapeImpl(Shape shape, LayoutContext context, List<LayoutResult> lr) {
            content.performLayout(shape, context, unwrap(lr));
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
        protected LayoutResult preferredShapeImpl(BoxConstraints constraints) {
            Size all = insets.all();

            LayoutResult contentLR = content.preferredShape(new BoxConstraints(
                    constraints.min().subtractOrClamp(all),
                    constraints.max().subtract(all)));

            return new LayoutResult(
                    insets.addTo(contentLR.shape()),
                    this,
                    contentLR
            );
        }

        @Override
        protected void applyShapeImpl(Shape shape, LayoutContext context, List<LayoutResult> lr) {
            content.performLayout(insets.removeFrom(shape), context, unwrap(lr));
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
        protected LayoutResult preferredShapeImpl(BoxConstraints constraints) {
            Size minSize = Size.max(constraints.min(), new Size(radius * 2, radius * 2));
            LayoutResult contentLR = content.preferredShape(constraints.withMinimum(minSize));
            return new LayoutResult(
                    new RoundedRectangle(contentLR.shape().bounds(), radius),
                    this,
                    contentLR
            );
        }

        @Override
        protected void applyShapeImpl(Shape shape, LayoutContext context, List<LayoutResult> lr) {
            content.performLayout(new RoundedRectangle(shape.bounds(), radius), context, unwrap(lr));
        }
    }

    public static Element shaped(Element content) {
        return new SingleNodeLayout(content) {

            @Override
            protected LayoutResult preferredShapeImpl(BoxConstraints constraints) {
                LayoutResult lr = content.preferredShape(constraints);
                return new LayoutResult(
                        lr.shape().bounds(),
                        this,
                        lr
                );
            }

            @Override
            protected void applyShapeImpl(Shape shape, LayoutContext context, List<LayoutResult> lr) {
                LayoutResult contentLR = content.preferredShape(BoxConstraints.fixed(shape.bounds().size()));
                List<LayoutResult> l = unwrap(lr);
                l.add(contentLR);
                content.performLayout(contentLR.shape().translate(shape.bounds().topLeft()), context, l);
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

        protected static List<LayoutResult> unwrap(List<LayoutResult> lr) {
            return switch (lr.size()) {
                case 0 -> List.of();
                case 1 -> List.of((LayoutResult) lr.get(0).obj());
                case 2 -> List.of((LayoutResult) lr.get(0).obj(), (LayoutResult) lr.get(1).obj());
                case 3 -> List.of((LayoutResult) lr.get(0).obj(), (LayoutResult) lr.get(1).obj(), (LayoutResult) lr.get(2).obj());
                default -> lr.stream().map(l -> (LayoutResult) l.obj()).toList();
            };

        }
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
        protected LayoutResult preferredShapeImpl(BoxConstraints constraints) {
            Shape shape = Shape.NULL;
            LayoutResult[] l = new LayoutResult[elements.size()];
            int i = 0;

            for (Element e : elements) {
                LayoutResult lr = e.preferredShape(constraints);
                shape = shape.unionWith(lr.shape());
                l[i++] = lr;
            }
            return new LayoutResult(shape, this, l);
        }

        @Override
        protected void applyShapeImpl(Shape shape, LayoutContext context, List<LayoutResult> lr) {
            int i = 0;
            for (Element e : elements) {
                List<LayoutResult> l = new ArrayList<>();
                for (LayoutResult a : lr) {
                    l.add(((LayoutResult[]) a.obj())[i]);
                }
                e.performLayout(shape, context, l);
                i++;
            }
        }
    }
}
