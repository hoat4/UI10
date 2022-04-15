package ui10.layout;

import ui10.geom.Axis;
import ui10.geom.Fraction;
import ui10.geom.Size;

import java.util.ArrayList;
import java.util.List;

import static ui10.geom.Fraction.subtract;

/**
 * Common code for {@linkplain LinearLayout} and {@linkplain Grid}
 */
class FlexLayout {

    private final Axis primaryAxis;
    private final BoxConstraints containerConstraints;
    private final List<? extends FlexElement> elements;

    public List<Size> childrenSizes;
    public Size containerSize;
    public int gap;

    // in this class, "width" means primary axis size, "height" means secondary axis size
    private int width, height;

    public FlexLayout(Axis primaryAxis, BoxConstraints containerConstraints, List<? extends FlexElement> elements) {
        this.primaryAxis = primaryAxis;
        this.containerConstraints = containerConstraints;
        this.elements = elements;
    }

    public void layout() {
        computeHeight();
        layoutWithoutGrow();
        distributeRemainingSpace();

        containerSize = Size.of(primaryAxis, width, height);
    }

    private void computeHeight() {
        Axis secondaryAxis = primaryAxis.other();
        int minH = containerConstraints.min().value(secondaryAxis);
        int maxH = containerConstraints.max().value(secondaryAxis);
        if (minH == maxH) {
            height = minH;
            return;
        }

        BoxConstraints c1 = new BoxConstraints(
                containerConstraints.min().with(primaryAxis, 0),
                containerConstraints.max().with(primaryAxis, Size.INFINITY));
        height = elements.stream().mapToInt(n -> n.preferredSize(c1).value(secondaryAxis)).max().orElse(0);
    }

    private void layoutWithoutGrow() {
        this.childrenSizes = new ArrayList<>();
        int allGaps = gap * Math.max(0, elements.size() - 1);
        var w = Size.subtract(containerConstraints.max().value(primaryAxis), allGaps);
        for (FlexElement e : elements) {
            var l = e.preferredSize(new BoxConstraints(
                    Size.of(primaryAxis, 0, height),
                    Size.of(primaryAxis, w, height)
            ));
            if (w != Size.INFINITY)
                w -= l.value(primaryAxis);
            childrenSizes.add(l);
        }
        width = childrenSizes.stream().mapToInt(l -> l.value(primaryAxis)).sum() + allGaps;
    }


    private void distributeRemainingSpace() {
        int remaining = containerConstraints.min().value(primaryAxis) - width;
        if (remaining <= 0)
            return;

        Fraction growFactorSum = elements.stream().map(FlexElement::growFactor).reduce(Fraction.ZERO, Fraction::add);
        int indexOfFirstGrowable = -1;
        for (int i = elements.size() - 1; i >= 0; i--) {
            if (!elements.get(i).growFactor().isZero()) {
                indexOfFirstGrowable = i;
                break;
            }
        }

        if (indexOfFirstGrowable == -1)
            return;

        for (int i = 0; i < elements.size() && !growFactorSum.isZero(); i++) {
            FlexElement e = elements.get(i);
            Fraction growFactor = e.growFactor();
            if (!growFactor.isZero()) {
                Size currentSize = childrenSizes.get(i);
                Fraction w2 = growFactor.multiply(remaining).divide(growFactorSum).add(currentSize.value(primaryAxis));
                Size s = Size.of(primaryAxis, i == indexOfFirstGrowable ? w2.ceil() : w2.floor(), height);
                childrenSizes.set(i, s);
                remaining -= s.value(primaryAxis) - currentSize.value(primaryAxis);
                growFactorSum = subtract(growFactorSum, growFactor);
            }
        }
        width = containerConstraints.min().value(primaryAxis);
    }

    public interface FlexElement {

        Size preferredSize(BoxConstraints constraints);

        Fraction growFactor();
    }
}
