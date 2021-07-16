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
        if (children().isEmpty())
            return new Layout(constraints, constraints.min()) {

                @Override
                protected void apply() {
                }
            }; // TODO

        Size size = children().stream().map(n -> n.layout(constraints).size).reduce(Size::max).get();
        return new Layout(constraints, size) {
            @Override
            protected void apply() {
                children().forEach(n -> {
                    n.layout(new BoxConstraints(size, size)).apply(ORIGO);
                });
            }
        };
    }
}
