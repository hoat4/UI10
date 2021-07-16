package ui10.nodes;

import ui10.geom.Size;
import ui10.layout.BoxConstraints;
import ui10.node.Node;

public abstract class FixedSizeNode extends Node {

    protected abstract Size fixedSize();

    @Override
    protected Layout computeLayout(BoxConstraints constraints) {
        return new Layout(constraints, fixedSize()) {
            @Override
            protected void apply() {
            }
        };
    }
}
