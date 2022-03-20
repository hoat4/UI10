package ui10.base;

import ui10.binding2.Property;

public non-sealed abstract class TransientElement extends Element {

    public Element logicalParent;

    @Override
    void initLogicalParent(Element logicalParent) {
        this.logicalParent = logicalParent;
    }

    @Override
    <T> T getPropertyFromParent(Property<T> prop) {
        if (logicalParent == null)
            return prop.defaultValue;
        return logicalParent.getProperty(prop);
    }
}
