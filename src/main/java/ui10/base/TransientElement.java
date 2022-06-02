package ui10.base;

import ui10.binding2.Property;

public non-sealed abstract class TransientElement extends Element {

    public Element logicalParent;

    @Override
    void initLogicalParent(Element logicalParent) {
        this.logicalParent = logicalParent;
    }

    // context metódus?
}
