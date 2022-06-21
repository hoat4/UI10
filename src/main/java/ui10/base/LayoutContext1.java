package ui10.base;

import ui10.geom.Size;
import ui10.layout.BoxConstraints;

import java.util.*;

public class LayoutContext1 {

    protected final Element defaultParent;

    public LayoutContext1(Element defaultParent) {
        this.defaultParent = defaultParent;
    }

    public Size preferredSize(Element e, BoxConstraints constraints) {
        return preferredSize(e, constraints, LayoutProtocol.BOX);
    }

    public <I, O> O preferredSize(Element e, I constraints, LayoutProtocol<I, O> protocol) {
        Objects.requireNonNull(e);
        Objects.requireNonNull(constraints);

        if (e.parent() == null)
            throw new RuntimeException("no parent: "+e);

        while (e instanceof ElementModel m)
            // kikerüljük ElementModeleket, hogy ne dobozosítsunk alternatív layout protokollok esetén
            // de így sem teljesen jó, mert így meg Containernél lesz ugyanez a probléma
            e = m.view();

        O output = protocol.preferredSize(e, constraints, this);
        Objects.requireNonNull(output, e::toString);

        if (e instanceof RenderableElement)
            addLayoutDependency((RenderableElement) e, new LayoutDependency<>(constraints, output, protocol));
        return output;
    }

    <I, O> boolean isInvalidated(RenderableElement renderableElement, LayoutDependency<I, O> dep) {
        O output = dep.protocol().preferredSize(renderableElement, dep.inputConstraints, this);
        Objects.requireNonNull(output);
        // addLayoutDependency?
        return !Objects.equals(output, dep.size);
    }

    /**
     * Records that the current element must be layouted again if the size of the specified element is changed.
     * This is invoked only in this class, when an element has completed computing its preferred size.
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
