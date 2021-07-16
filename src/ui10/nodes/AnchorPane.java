package ui10.nodes;

import ui10.binding.*;
import ui10.geom.Point;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;
import ui10.node.Node;

import java.util.List;

public class AnchorPane extends Node {

    private final ObservableList<AnchoredItem> items = new ObservableListImpl<>();

    private final ObservableList<Node> children = items.streamBinding().map(i -> i.node).toList();

    public AnchorPane() {
    }

    public AnchorPane(AnchoredItem... children) {
        items.addAll(List.of(children));
    }

    @Override
    protected ObservableList<Node> createChildren() {
        return children;
    }

    public List<AnchoredItem> items() {
        return items;
    }

    @Override
    protected Node.Layout computeLayout(BoxConstraints constraints) {
        if (items.isEmpty())
            return new Layout(constraints, constraints.max()) {
                @Override
                protected void apply() {
                }
            }; // TODO

        Size size = items.stream().
                map(n -> n.node.layout(constraints.subtract(n.pos().get())).size).
                reduce(Size::max).get();

        return new Layout(constraints, size) {

            @Override
            protected void apply() {
                items.forEach(n -> {
                    n.pos().subscribe(c -> valid.set(false));
                    Layout childLayout = n.node.layout(new BoxConstraints(size, size).subtract(n.pos().get()));
                    applyChild(childLayout, n.pos().get());
                });
            }
        };
    }

    public static class AnchoredItem {

        private static final ExtendedPropertyDefinition<Node, Point> POS_PROPERTY = new ExtendedPropertyDefinition<>();

        public final Node node;
        private final ScalarProperty<Point> point = ScalarProperty.create();

        public AnchoredItem(Node node, Point pos) {
            this.node = node;
            pos().set(pos);
        }

        public ScalarProperty<Point> pos() {
            return point;
        }
    }
}
