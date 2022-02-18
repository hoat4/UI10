package ui10.ui6;

import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class LayoutContext1 {

    final Set<Element> inReplacement = new HashSet<>();

    public Size preferredSize(Element e, BoxConstraints constraints) {
        Objects.requireNonNull(constraints);

        if (e.replacement() == null || inReplacement.contains(e)) {
            Size s = e.preferredSizeImpl(constraints, this);
            Objects.requireNonNull(s, this::toString);
            if (e instanceof RenderableElement)
                addLayoutDependency((RenderableElement) e,
                        new LayoutContext1.LayoutDependency(constraints, s));
            return s;
        } else {
            inReplacement.add(e);
            try {
                return preferredSize(e.replacement(), constraints);
            } finally {
                inReplacement.remove(e);
            }
        }
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

    record LayoutDependency(BoxConstraints inputConstraints, Size size/*, LayoutExtra extra*/) {
    }
}
