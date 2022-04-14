package ui10.layout;

import ui10.binding2.Property;
import ui10.geom.*;
import ui10.base.Element;
import ui10.base.LayoutContext1;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

// ez most csak a GROW_FACTOR miatt publikus
public class LinearLayout extends RectangularLayout {

    public static final Property<Fraction> GROW_FACTOR = new Property<>(false, Fraction.WHOLE);

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
    protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
        return computeLayout(constraints, context).containerSize;
    }

    @Override
    protected void doPerformLayout(Size size, BiConsumer<Element, Rectangle> placer, LayoutContext1 context) {
        FlexLayout l = computeLayout(BoxConstraints.fixed(size), context);
        int x = 0;
        for (int i = 0; i < children.size(); i++) {
            Size s = l.childrenSizes.get(i);
            placer.accept(children.get(i), new Rectangle(Point.of(primaryAxis, x, 0), s));
            x += s.value(primaryAxis) + l.gap;
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
                        return e.getProperty(GROW_FACTOR);
                    }
                }).toList());

        l.gap = getProperty(Grid.GAP_PROPERTY);
        l.layout();
        return l;
    }

    @Override
    public String toString() {
        return primaryAxis + "LY" + children;
    }
}
