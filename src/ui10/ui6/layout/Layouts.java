package ui10.ui6.layout;

import ui10.geom.*;
import ui10.geom.shape.RoundedRectangle;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.layout4.Weight;
import ui10.ui6.Element;
import ui10.ui6.LayoutContext1;
import ui10.ui6.LayoutContext2;
import ui10.ui6.decoration.css.CSSClass;
import ui10.ui6.graphics.Opacity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Layouts {

    public static Element empty() {
        return new Empty();
    }

    private static class Empty extends Element {

        @Override
        public void enumerateStaticChildren(Consumer<Element> consumer) {
        }

        @Override
        public Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            return constraints.min();
        }

        @Override
        protected void performLayoutImpl(Shape shape, LayoutContext2 context) {
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
        public Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            return context.preferredSize(content, constraints);
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
        public Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            return constraints.clamp(context.preferredSize(content, constraints.withMinimum(Size.ZERO)));
        }


        @Override
        protected Shape computeContentShape(Shape containerShape, LayoutContext2 context) {
            Size contentSize = context.preferredSize(content, new BoxConstraints(Size.ZERO, containerShape.bounds().size()));
            return containerShape.bounds().centered(contentSize).intersectionWith(containerShape);
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
        public Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            return context.preferredSize(content, constraints.withMinimum(Size.max(constraints.min(), minSize)));
        }

        @Override
        protected Shape computeContentShape(Shape containerShape, LayoutContext2 context) {
            return containerShape;
        }
    }

    public static Element padding(Element content, Insets insets) {
        return new ShapedPadding(content, insets);
    }

    private static class Padding extends SingleNodeLayout {
        private final Insets insets;

        public Padding(Element content, Insets insets) {
            super(content);
            this.insets = insets;
        }

        @Override
        public Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            Size insetsSize = insets.all();
            Size contentSize = context.preferredSize(content, constraints.subtract(insetsSize));
            return contentSize.add(insetsSize);
        }

        @Override
        protected Shape computeContentShape(Shape containerShape, LayoutContext2 context) {
            return containerShape.bounds().withInnerInsets(insets).intersectionWith(containerShape);
        }
    }

    public static Element shapedPadding(Element content, Insets insets) {
        return new ShapedPadding(content, insets);
    }

    private static class ShapedPadding extends SingleNodeLayout {
        private final Insets insets;

        public ShapedPadding(Element content, Insets insets) {
            super(content);
            this.insets = insets;
        }

        @Override
        public Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            Size insetsSize = insets.all();
            Size contentSize = context.preferredSize(content, constraints.subtract(insetsSize));
            return contentSize.add(insetsSize);
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
        public Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            Size minSize = Size.max(constraints.min(), new Size(radius * 2, radius * 2));
            return context.preferredSize(content, constraints.withMinimum(minSize));
        }

        @Override
        protected Shape computeContentShape(Shape containerShape, LayoutContext2 context) {
            return new RoundedRectangle(containerShape.bounds(), radius);
        }
    }

    public static abstract class SingleNodeLayout extends Element {

        protected final Element content;

        public SingleNodeLayout(Element content) {
            this.content = content;
        }

        @Override
        public void enumerateStaticChildren(Consumer<Element> consumer) {
            consumer.accept(content);
        }

        @Override
        protected void performLayoutImpl(Shape shape, LayoutContext2 context) {
            context.placeElement(content, computeContentShape(shape, context));
        }

        protected abstract Shape computeContentShape(Shape containerShape, LayoutContext2 context);
    }

    public static Element stack(Element... nodes) {
        return new StackLayout(nodes);
    }

    private static class StackLayout extends Element {

        private final List<Element> elements;

        public StackLayout(Element... elements) {
            this.elements = List.of(elements);
        }

        @Override
        public void enumerateStaticChildren(Consumer<Element> consumer) {
            elements.forEach(consumer);
        }

        @Override
        public Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            Size size = Size.ZERO;
            for (Element e : elements)
                size = Size.max(size, context.preferredSize(e, constraints));
            return size;
        }

        @Override
        protected void performLayoutImpl(Shape shape, LayoutContext2 context) {
            for (Element e : elements)
                context.placeElement(e, shape);
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
        public Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            return constraints.clamp(size);
        }

        @Override
        protected Shape computeContentShape(Shape containerShape, LayoutContext2 context) {
            return containerShape;
        }
    }

    private static abstract class RectangularLayout extends Element {
        @Override
        protected void performLayoutImpl(Shape shape, LayoutContext2 context) {
            Rectangle shapeBounds = shape.bounds();
            Size size = shapeBounds.size();
            doPerformLayout(size, (elem, rect)->{
                context.placeElement(elem, rect.translate(shapeBounds.topLeft()).intersectionWith(shape));
            }, context);
        }

        protected abstract void doPerformLayout(Size size, BiConsumer<Element, Rectangle> placer, LayoutContext1 context);
    }

    public static Element vertically(List<? extends Element> elements) {
        return new LinearLayout(Axis.VERTICAL, List.copyOf(elements));
    }

    public static Element vertically(Element... elements) {
        return new LinearLayout(Axis.VERTICAL, List.of(elements));
    }

    private static class LinearLayout extends RectangularLayout {

        private final Axis primaryAxis;
        private final List<? extends Element> children;

        public LinearLayout(Axis primaryAxis, List<? extends Element> children) {
            this.primaryAxis = primaryAxis;
            this.children = children;
        }

        private Axis secondaryAxis() {
            return primaryAxis.other();
        }

        @Override
        public void enumerateStaticChildren(Consumer<Element> consumer) {
            children.forEach(consumer);
        }

        @Override
        protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context1) {
            return computeLayout(constraints, context1).size;
        }

        @Override
        protected void doPerformLayout(Size size, BiConsumer<Element, Rectangle> placer, LayoutContext1 context) {
            ComputedLayout l = computeLayout(BoxConstraints.fixed(size), context);
            int x = 0;
            for (int i = 0; i < children.size(); i++) {
                Size s = l.childrenSizes.get(i);
                placer.accept(children.get(i), new Rectangle(Point.of(primaryAxis, x, 0), s));
                x += s.value(primaryAxis);
            }
        }

        private ComputedLayout computeLayout(BoxConstraints constraints, LayoutContext1 context) {
            Axis secondaryAxis = primaryAxis.other();

            // ebben a függvényben width alatt primaryAxis-beli méretet értjük, height alatt pedig secondaryAxis-beli méretet

            BoxConstraints c1 = new BoxConstraints(
                    constraints.min().with(primaryAxis, 0),
                    constraints.max().with(primaryAxis, Size.INFINITY));
            int height = children.stream().mapToInt(n -> context.preferredSize(n, c1).value(secondaryAxis)).max().orElse(0);
            List<Size> childrenSizes = new ArrayList<>();
            var w = constraints.max().value(primaryAxis);
            for (Element e : children) {
                var l = context.preferredSize(e, new BoxConstraints(
                        Size.of(primaryAxis, 0, height),
                        Size.of(primaryAxis, w, height)
                ));
                if (w != Size.INFINITY)
                    w -= l.value(primaryAxis);
                childrenSizes.add(l);
            }
            var width = childrenSizes.stream().mapToInt(l -> l.value(primaryAxis)).sum();

            int remaining = constraints.min().value(primaryAxis) - width;
            if (remaining >= 0) {
                double weightSum = children.stream().mapToDouble(Weight::weight).sum();
                int lastWithWeight = -1;
                for (int i = children.size() - 1; i >= 0; i--) {
                    if (Weight.weight(children.get(i)) != 0) {
                        lastWithWeight = i;
                        break;
                    }
                }

                if (lastWithWeight != -1) {
                    for (int i = 0; i < children.size() && weightSum != 0; i++) {
                        Element e = children.get(i);
                        double weight = Weight.weight(e);
                        if (weight != 0) {
                            Size currentSize = childrenSizes.get(i);
                            double w2 = currentSize.value(primaryAxis) + remaining * weight / weightSum;
                            assert Double.isFinite(w2);
                            BoxConstraints c3 = new BoxConstraints(
                                    Size.of(primaryAxis, i == lastWithWeight ? (int) Math.ceil(w2) : (int) Math.floor(w2), height),
                                    Size.of(primaryAxis, (int) Math.ceil(w2), height));
                            Size s = context.preferredSize(e, c3);
                            childrenSizes.set(i, s);
                            remaining -= s.value(primaryAxis) - currentSize.value(primaryAxis);
                            weightSum -= weight;
                        }
                    }
                    width = constraints.min().value(primaryAxis);
                }
            }

            return new ComputedLayout(childrenSizes, Size.of(primaryAxis, width, height));
        }

        private record ComputedLayout(List<Size> childrenSizes, Size size) {
        }
    }
}
