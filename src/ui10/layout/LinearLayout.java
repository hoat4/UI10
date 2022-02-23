package ui10.layout;

import ui10.geom.*;
import ui10.base.Element;
import ui10.base.LayoutContext1;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

class LinearLayout extends RectangularLayout {

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
        return computeLayout(constraints, context1).containerSize;
    }

    @Override
    protected void doPerformLayout(Size size, BiConsumer<Element, Rectangle> placer, LayoutContext1 context) {
        FlexLayout l = computeLayout(BoxConstraints.fixed(size), context);
        int x = 0;
        for (int i = 0; i < children.size(); i++) {
            Size s = l.childrenSizes.get(i);
            placer.accept(children.get(i), new Rectangle(Point.of(primaryAxis, x, 0), s));
            x += s.value(primaryAxis);
        }
    }

    private FlexLayout computeLayout(BoxConstraints constraints, LayoutContext1 context) {
        FlexLayout l = new FlexLayout(primaryAxis, constraints,
                children.stream().map(e -> new FlexLayout.FlexElement() {
                    @Override
                    public Size preferredSize(BoxConstraints constraints) {
                        return context.preferredSize(e, constraints);
                    }

                    @Override
                    public Fraction growFactor() {
                        return GrowFactor.growFactor(e);
                    }
                }).toList());

        l.layout();
        return l;
    }
}
