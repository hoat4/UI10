package ui10.nodes;

import ui10.geom.Size;
import ui10.layout.BoxConstraints;
import ui10.node.Node;

import static ui10.geom.Point.ORIGO;

public class StackPane extends Pane {
    public StackPane() {
    }

    public StackPane(Node... children) {
        super(children);
    }

    @Override
    protected Node.Layout computeLayout(BoxConstraints constraints) {
        Size size = children().stream().map(n -> n.layout(constraints).size).reduce(Size::max).orElse(constraints.min());
        return new Layout(constraints, size) {
            @Override
            protected void apply() {
                children().forEach(n -> {
                    n.layout(BoxConstraints.fixed(size)).apply(ORIGO);
                });
            }
        };
    }
}
