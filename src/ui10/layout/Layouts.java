package ui10.layout;

import ui10.geom.*;
import ui10.geom.shape.RoundedRectangle;
import ui10.geom.shape.Shape;
import ui10.base.Element;
import ui10.base.LayoutContext1;
import ui10.base.LayoutContext2;
import ui10.decoration.css.CSSClass;
import ui10.graphics.Opacity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static ui10.geom.Point.ORIGO;

// TODO absolutePositioned layout pointra és rectangle-re
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
        return new Padding(content, insets);
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

    public static Element vertically(List<? extends Element> elements) {
        return new LinearLayout(Axis.VERTICAL, List.copyOf(elements));
    }

    public static Element vertically(Element... elements) {
        return new LinearLayout(Axis.VERTICAL, List.of(elements));
    }

    public static Element grid(int cols, Element... elements) {
        List<List<Element>> rows = new ArrayList<>();
        for (int i = 0; i < elements.length; i += cols)
            rows.add(List.of(Arrays.copyOfRange(elements, i, i + cols)));
        return new Grid(rows);
    }

    public static Element halign(HorizontalAlignment align, Element element) {
        return new HAlign(element, align);
    }

    private static class HAlign extends SingleNodeRectangularLayout {

        private final HorizontalAlignment align;

        HAlign(Element content, HorizontalAlignment align) {
            super(content);
            this.align = align;
        }

        @Override
        protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            Size contentSize = context.preferredSize(content, constraints.withMinimum(constraints.min().withWidth(0)));
            return constraints.clamp(contentSize);
        }

        @Override
        protected Rectangle computeContentBounds(Size size, LayoutContext1 context) {
            Size contentSize = context.preferredSize(content, new BoxConstraints(size.withWidth(0), size));
            return switch (align) {
                case LEFT -> Rectangle.of(contentSize);
                case CENTER -> Rectangle.of(size).centered(contentSize);
                case RIGHT -> Rectangle.cornerAt(Rectangle.Corner.BOTTOM_RIGHT, Point.of(size), contentSize);
            };
        }
    }

    public static enum HorizontalAlignment {
        LEFT, CENTER, RIGHT
    }

    public static Element valign(VerticalAlignment align, Element element) {
        return new VAlign(element, align);
    }

    private static class VAlign extends SingleNodeRectangularLayout {

        private final VerticalAlignment align;

        VAlign(Element content, VerticalAlignment align) {
            super(content);
            this.align = align;
        }

        @Override
        protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            Size contentSize = context.preferredSize(content, constraints.withMinimum(constraints.min().withHeight(0)));
            return constraints.clamp(contentSize);
        }

        @Override
        protected Rectangle computeContentBounds(Size size, LayoutContext1 context) {
            Size contentSize = context.preferredSize(content, new BoxConstraints(size.withHeight(0), size));
            return switch (align) {
                case TOP -> Rectangle.of(contentSize);
                case CENTER -> Rectangle.of(size).centered(contentSize);
                case BOTTOM -> Rectangle.cornerAt(Rectangle.Corner.BOTTOM_RIGHT, Point.of(size), contentSize);
            };
        }
    }

    public static enum VerticalAlignment {
        TOP, CENTER, BOTTOM
    }

}
