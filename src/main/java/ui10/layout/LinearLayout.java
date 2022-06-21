package ui10.layout;

import ui10.base.ElementExtra;
import ui10.base.Element;
import ui10.binding2.Property;
import ui10.geom.*;
import ui10.base.LayoutContext1;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

// ez most csak a GROW_FACTOR miatt publikus
public class LinearLayout extends RectangularLayout {

    public static final Property<Fraction> GROW_FACTOR = new Property<>(false, Fraction.WHOLE);

    private final Axis primaryAxis;
    private final List<? extends Element> children;
    public int gap; // TODO Length-nek kéne lennie

    public LinearLayout(Axis primaryAxis, List<? extends Element> children) {
        this.primaryAxis = primaryAxis;
        this.children = children;
    }

    private Axis secondaryAxis() {
        return primaryAxis.other();
    }

    @Override
    public void enumerateChildren(Consumer<Element> consumer) {
        children.forEach(consumer);
    }

    @Override
    protected Size preferredSize(BoxConstraints constraints, LayoutContext1 context) {
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

                    private final LinearLayoutConstraints constraints = e.extra(LinearLayoutConstraints.class);
                    @Override
                    public Size preferredSize(BoxConstraints constraints) {
                        return context.preferredSize(e, constraints);
                    }

                    @Override
                    public Fraction growFactor() {
                        return constraints == null ? Fraction.WHOLE : constraints.growFactor;
                    }
                }).toList());

        l.gap = gap;
        l.layout();
        return l;
    }

    @Override
    public String toString() {
        return primaryAxis + "LY" + children;
    }

    public static class LinearLayoutConstraints extends ElementExtra {

        public Fraction growFactor = Fraction.WHOLE;

        public static LinearLayoutConstraints of(Element element) {
            return element.extra(LinearLayoutConstraints.class, LinearLayoutConstraints::new);
        }
    }
}
