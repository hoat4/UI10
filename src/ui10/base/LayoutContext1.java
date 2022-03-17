package ui10.base;

import ui10.geom.Size;
import ui10.layout.BoxConstraints;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class LayoutContext1 {

    final Set<Element> inReplacement = new HashSet<>();

    public Size preferredSize(Element e, BoxConstraints constraints) {
        return preferredSize(e, constraints, LayoutProtocol.BOX);
    }

    public <I, O> O preferredSize(Element e, I constraints, LayoutProtocol<I, O> protocol) {
        Objects.requireNonNull(e);
        Objects.requireNonNull(constraints);

        if (e.replacement() == null || inReplacement.contains(e)) {
            O output = protocol.preferredSize(e, constraints, this);
            Objects.requireNonNull(output, e::toString);
            if (e instanceof RenderableElement)
                addLayoutDependency((RenderableElement) e, new LayoutDependency<>(constraints, output, protocol));
            return output;
        } else {
            inReplacement.add(e);
            try {
                return preferredSize(e.replacement(), constraints, protocol);
            } finally {
                inReplacement.remove(e);
            }
        }
    }

    <I, O> boolean isInvalidated(RenderableElement renderableElement, LayoutDependency<I, O> dep) {
        O output = dep.protocol().preferredSize(renderableElement, dep.inputConstraints, this);
        Objects.requireNonNull(output);
        // addLayoutDependency?
        return !Objects.equals(output, dep.size);
    }

    // TODO "performing layout" helyett írjuk ki értelmesen, hogy elhelyezi az elementeket

    /**
     * Records that the current element must be layouted again if the size of the specified element is changed.
     * This is invoked automatically by RenderableElement when computing preferred size and by Pane when performing
     * layout.
     */
    void addLayoutDependency(RenderableElement element, LayoutDependency<?, ?> d) {
        // these known shapes should be used later to avoid computing preferred shapes redundantly
    }

    /**
     * This info applies to the element itself, not its replacement
     */
    record LayoutDependency<I, O>(I inputConstraints, O size, LayoutProtocol<I, O> protocol/*, LayoutExtra extra*/) {

        LayoutDependency {
            Objects.requireNonNull(inputConstraints);
            Objects.requireNonNull(size);
        }
    }
}
