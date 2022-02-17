package ui10.ui6.layout;

import ui10.geom.*;
import ui10.geom.shape.RoundedRectangle;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.ui6.Element;
import ui10.ui6.decoration.css.CSSClass;
import ui10.ui6.graphics.Opacity;

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
        protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
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
        protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            return content.preferredSize(constraints, context);
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
        protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            return constraints.clamp(content.preferredSize(constraints.withMinimum(Size.ZERO), context));
        }


        @Override
        protected Shape computeContentShape(Shape containerShape, LayoutContext2 context) {
            Size contentSize = content.preferredSize(new BoxConstraints(Size.ZERO, containerShape.bounds().size()), context);
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
        protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            return content.preferredSize(constraints.withMinimum(Size.max(constraints.min(), minSize)), context);
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
        protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            Size insetsSize = insets.all();
            Size contentSize = content.preferredSize(constraints.subtract(insetsSize), context);
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
        protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            Size insetsSize = insets.all();
            Size contentSize = content.preferredSize(constraints.subtract(insetsSize), context);
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
        protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            Size minSize = Size.max(constraints.min(), new Size(radius * 2, radius * 2));
            return content.preferredSize(constraints.withMinimum(minSize), context);
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
            content.performLayout(computeContentShape(shape, context), context);
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
        protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            Size size = Size.ZERO;
            for (Element e : elements)
                size = Size.max(size, e.preferredSize(constraints, context));
            return size;
        }

        @Override
        protected void performLayoutImpl(Shape shape, LayoutContext2 context) {
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
        protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            return constraints.clamp(size);
        }

        @Override
        protected Shape computeContentShape(Shape containerShape, LayoutContext2 context) {
            return containerShape;
        }
    }


    public static Element vertically(List<? extends Element> elements) {
        return new VerticalLayout(elements);
    }

    public static Element vertically(Element... elements) {
        return new VerticalLayout(List.of(elements));
    }

    private static class VerticalLayout extends RectangularLayout {

        private final List<? extends Element> elements;

        public VerticalLayout(List<? extends Element> elements) {
            this.elements = elements;
        }

        @Override
        public void enumerateStaticChildren(Consumer<Element> consumer) {
            elements.forEach(consumer);
        }

        @Override
        protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            BoxConstraints elementConstraints = constraints.withUnboundedHeight();

            int w = 0;
            for (Element e : elements)
                w = Math.max(w, e.preferredSize(elementConstraints, context).width());

            elementConstraints = BoxConstraints.fixed(new Size(w, 0)).withUnboundedHeight();
            int h = 0;
            for (Element e : elements)
                h += e.preferredSize(elementConstraints, context).height();
            return new Size(w, h);
        }

        @Override
        protected void doPerformLayout(Size size, LayoutContext1 context, BiConsumer<Element, Rectangle> consumer) {
            BoxConstraints elementConstraints =
                    BoxConstraints.fixed(new Size(size.width(), 0)).withUnboundedHeight();

            int y = 0;
            for (Element e : elements) {
                Size elemSize = e.preferredSize(elementConstraints, context);
                consumer.accept(e, new Rectangle(new Point(0, y), elemSize));
                y += elemSize.height();
            }
        }
    }


    public static abstract class RectangularLayout extends Element {

        @Override
        protected abstract Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context1);

        @Override
        protected void performLayoutImpl(Shape shape, LayoutContext2 context) {
            doPerformLayout(shape.bounds().size(), context, (e, rect) -> {
                rect = rect.translate(shape.bounds().topLeft());
                Shape intersection = rect.intersectionWith(shape);
                if (intersection == null)
                    throw new RuntimeException("no intersection for "+e+": "+rect+", "+shape.bounds());
                e.performLayout(intersection, context);
            });
        }

        protected abstract void doPerformLayout(Size size, LayoutContext1 context, BiConsumer<Element, Rectangle> consumer);
    }

}
