package ui10.layout;

import ui10.base.Element;
import ui10.base.LayoutContext1;
import ui10.base.LayoutContext2;
import ui10.base.TransientElement;
import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.geom.shape.CompositeShape;
import ui10.geom.shape.Shape;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FlowLayout extends TransientElement implements Flowable {

    public final List<Element> items;

    public FlowLayout(List<Element> items) {
        this.items = List.copyOf(items);
    }

    @Override
    public void enumerateStaticChildren(Consumer<Element> consumer) {
        items.forEach(consumer);
    }

    @Override
    protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
        FlowableElementSize s = computeSize(null, constraints.max(), context);
        Size size = Size.ZERO;
        for (Size line : s.lineSizes())
            size = merge(size, line);
        return size;
    }

    private static Size merge(Size a, Size b) {
        if (a == null)
            return b;
        if (b == null)
            return a;
        return new Size(Math.max(a.width(), b.width()), a.height() + b.height());
    }

    @Override
    public FlowableElementSize layoutFlowable(FlowableLayoutInput constraints, LayoutContext1 context) {
        return computeSize(constraints.rightMax(), constraints.bottomMax(), context);
    }

    private FlowableElementSize computeSize(Size rightMax, Size bottomMax, LayoutContext1 context) {
        List<Size> resultSizes = new ArrayList<>();
        Size currLineSize = Size.ZERO;
        boolean wrapBeforeFirst = false;
        for (Element e : items) {
            FlowableLayoutInput constraints = new FlowableLayoutInput(rightMax, bottomMax);
            FlowableElementSize elemSize = context.preferredSize(e, constraints, Flowable.PROTOCOL);

            boolean first = true;
            for (Size lineSize : elemSize.lineSizes()) {
                if (!first || elemSize.wrapBeforeFirst()) {
                    if (resultSizes.isEmpty())
                        wrapBeforeFirst = elemSize.wrapBeforeFirst();
                    resultSizes.add(currLineSize);
                    rightMax = bottomMax;
                    currLineSize = Size.ZERO;
                }
                first = false;

                currLineSize = merge(currLineSize, lineSize);
                bottomMax = new Size(bottomMax.width(), rightMax.height() - currLineSize.height());
                rightMax = computeRightMax(bottomMax, currLineSize);
            }
        }
        resultSizes.add(currLineSize);
        return new FlowableElementSize(wrapBeforeFirst, resultSizes);
    }

    @Override
    protected void performLayoutImpl(Shape shape, LayoutContext2 context) {
        // TODO kezelni kell, ha a shape FlowShape
        //      de hogy kéne ezt, ha annak egy transzformált (vagy más miatt wrappelt) változata?

        Rectangle bounds = shape.bounds();

        Size rightMax = bounds.size(), bottomMax = bounds.size();
        Point p = bounds.topLeft();
        Size currLineSize = Size.ZERO;
        for (Element e : items) {
            FlowableLayoutInput constraints = new FlowableLayoutInput(rightMax, bottomMax);
            FlowableElementSize childSize = context.preferredSize(e, constraints, Flowable.PROTOCOL);

            List<Rectangle> rects = new ArrayList<>();
            boolean first = true;
            for (Size lineSize : childSize.lineSizes()) {
                if (!first || childSize.wrapBeforeFirst()) {
                    rightMax = bottomMax;
                    p = p.addY(currLineSize.height());
                    currLineSize = Size.ZERO;
                }
                first = false;

                rects.add(new Rectangle(p.addX(currLineSize.width()), lineSize));
                currLineSize = merge(currLineSize, lineSize);
                bottomMax = new Size(bottomMax.width(), rightMax.height() - currLineSize.height());
                rightMax = computeRightMax(bottomMax, currLineSize);
            }
            context.placeElement(e, new FlowShape(rects));
        }
    }

    private Size computeRightMax(Size bottomMax, Size existingLinePart) {
        return new Size(
                bottomMax.width() - existingLinePart.width(),
                bottomMax.height() + existingLinePart.height()
        );
    }

    public static class FlowShape extends CompositeShape {

        public FlowShape(List<Rectangle> rects) {
            super(rects);
        }
    }
}
