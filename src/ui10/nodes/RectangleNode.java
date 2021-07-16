package ui10.nodes;

import ui10.binding.ObservableList;
import ui10.binding.ScalarProperty;
import ui10.geom.Size;
import ui10.node.Node;

public class RectangleNode extends FixedSizeNode {

    private Size size;

    public RectangleNode() {
    }

    public RectangleNode(Size size) {
        this.size = size;
    }

    public ScalarProperty<Size> rectangleSize() {
        return property((RectangleNode n) -> n.size, (n, v) -> n.size = v);
    }

    @Override
    protected Size fixedSize() {
        return size;
    }

    @Override
    protected ObservableList<Node> createChildren() {
        return null;
    }

}
