package ui10.nodes;

import ui10.binding.ObservableList;
import ui10.binding.ScalarProperty;
import ui10.geom.Point;
import ui10.geom.Size;
import ui10.node.Node;

import static ui10.geom.Point.ORIGO;
import static ui10.geom.Rectangle.rect;

public class LineNode extends FixedSizeNode {

    private Point end;

    public LineNode() {
    }

    public LineNode(Point end) {
        this.end = end;
    }

    public ScalarProperty<Point> end() {
        return property((LineNode n) -> n.end, (n, v) -> n.end = v);
    }

    @Override
    protected ObservableList<Node> createChildren() {
        // TODO melyik legyen a primitív?
        return null;
    }

    @Override
    protected Size fixedSize() {
        return rect(ORIGO, end).size();
    }
}