package ui10.base;

import ui10.geom.Size;
import ui10.layout.BoxConstraints;

import java.util.Objects;

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
            throw new RuntimeException("no parent: " + e);

        while (e instanceof ElementModel m)
            // kikerüljük ElementModeleket, hogy ne dobozosítsunk alternatív layout protokollok esetén
            // de így sem teljesen jó, mert így meg Containernél lesz ugyanez a probléma
            e = m.view();

        RenderableElement e2 = (RenderableElement) e;
        O output = protocol.preferredSize(e2, constraints, this);
        Objects.requireNonNull(output, e::toString);

        return output;
    }
}
