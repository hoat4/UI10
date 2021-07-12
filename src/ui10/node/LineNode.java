package ui10.node;

import ui10.binding.ObservableList;
import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.geom.Point;
import ui10.layout.BoxConstraints;

import static ui10.geom.Point.ORIGO;
import static ui10.geom.Rectangle.rect;

public class LineNode extends Node {

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
    public ObservableList<Node> children() {
        // TODO melyik legyen a primitív?
        return null;
    }

    @Override
    public Layout layout(BoxConstraints constraints) {
        return new Layout(rect(ORIGO, end).size(), () -> {
        });
    }
}