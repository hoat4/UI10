package ui10.ui6;

import ui10.geom.Size;
import ui10.layout.BoxConstraints;

public interface  Node {

    public abstract Size computeSize(BoxConstraints constraints);

    public abstract void applySize(Size size, LayoutNode.LayoutContext layoutContext);
}
