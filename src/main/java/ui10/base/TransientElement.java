package ui10.base;

import java.util.Objects;

public non-sealed abstract class TransientElement extends Element {

    public Element logicalParent;

    public void initParent(Element parent) {
        this.logicalParent = parent;

        enumerateStaticChildren(e -> {
            Objects.requireNonNull(e);
            e.initParent(this);
        });
    }

    @Override
    public Element parent() {
        return logicalParent;
    }

    // context metódus?
}
