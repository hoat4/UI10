package ui10.ui6;

import ui10.geom.Size;
import ui10.layout.BoxConstraints;

public abstract class TransientNode implements Node {

    public abstract Node content();

    @Override
    public Size computeSize(BoxConstraints constraints) {
        return content().computeSize(constraints);
    }

    @Override
    public void applySize(Size size, LayoutNode.LayoutContext layoutContext) {
        content().applySize(size, layoutContext);
    }
}
