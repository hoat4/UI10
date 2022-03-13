package ui10.base;

import ui10.geom.Size;
import ui10.layout.BoxConstraints;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class LayoutContext1 {

    final Set<Element> inReplacement = new HashSet<>();

    public Size preferredSize(Element e, BoxConstraints constraints) {
        Objects.requireNonNull(constraints);

        if (e.replacement() == null || inReplacement.contains(e)) {
            return preferredSizeIgnoreReplacement(e, constraints);
        } else {
            inReplacement.add(e);
            try {
                return preferredSize(e.replacement(), constraints);
            } finally {
                inReplacement.remove(e);
            }
        }
    }

    Size preferredSizeIgnoreReplacement(Element e, BoxConstraints constraints) {
        Size s = e.preferredSizeImpl(constraints, this);

        if (s.isInfinite())
            throw new IllegalStateException("preferred size must be finite: " + s + " (by " + e + ")"); // milyen exceptionnek kéne ennek lennie?
        if (!constraints.contains(s))
            throw new IllegalStateException("invalid size returned by preferredSizeImpl for " +
                    constraints + ": " + s + " (by " + e + ")");

        Objects.requireNonNull(s, this::toString);
        if (e instanceof RenderableElement)
            addLayoutDependency((RenderableElement) e,
                    new LayoutDependency(constraints, s));
        return s;
    }


    // TODO "performing layout" helyett írjuk ki értelmesen, hogy elhelyezi az elementeket

    /**
     * Records that the current element must be layouted again if the size of the specified element is changed.
     * This is invoked automatically by RenderableElement when computing preferred size and by Pane when performing
     * layout.
     */
    void addLayoutDependency(RenderableElement element, LayoutDependency d) {
        // these known shapes should be used later to avoid computing preferred shapes redundantly
    }

    /**
     * This info applies to the element itself, not its replacement
     */
    record LayoutDependency(BoxConstraints inputConstraints, Size size/*, LayoutExtra extra*/) {

        LayoutDependency {
            Objects.requireNonNull(inputConstraints);
            Objects.requireNonNull(size);
        }
    }
}
