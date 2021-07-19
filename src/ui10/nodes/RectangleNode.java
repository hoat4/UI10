package ui10.nodes;

import ui10.binding.ObservableList;
import ui10.binding.ScalarProperty;
import ui10.geom.Size;
import ui10.image.Color;
import ui10.layout.BoxConstraints;
import ui10.node.Node;

public class RectangleNode extends Node {

    private Color color;

    public RectangleNode() {
    }

    public RectangleNode(Color color) {
        this.color = color;
    }

    public ScalarProperty<Color> color() {
        return property((RectangleNode n) -> n.color, (n, v) -> n.color = v);
    }

    @Override
    protected Layout computeLayout(BoxConstraints constraints) {
        return new Layout(constraints, constraints.min()) {
            @Override
            protected void apply() {
            }
        };
    }

    @Override
    protected ObservableList<Node> createChildren() {
        return null;
    }

    @Override
    public String toString() {
        return "RectangleNode (" + color + ", " + position().get() + ", " + size().get() + ")";
    }
}
