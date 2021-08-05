package ui10.layout;

import ui10.binding.ObservableList;
import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.binding.Scope;
import ui10.geom.Point;
import ui10.geom.Size;
import ui10.nodes.LayoutNode;
import ui10.nodes.Node;
import ui10.nodes.WrapperPane;

public class Positioned extends WrapperPane {

    public final ScalarProperty<Point> position = ScalarProperty.createWithDefault(Point.ORIGO); // itt legyen default?

    public Positioned() {
    }

    public Positioned(Node content) {
        super(content);
    }

    public Positioned(ObservableScalar<Node> content) {
        super(content);
    }

    public Positioned(Node content, Point position) {
        super(content);
        this.position.set(position);
    }

    public Positioned(ObservableScalar<Node> content, ObservableScalar<Point> position) {
        super(content);
        this.position.bindTo(position);
    }

    @Override
    protected ObservableScalar<? extends Node> paneContent() {
        return ObservableScalar.ofConstant(new OneChildOnePassLayout(content) {

            {
                dependsOn(Positioned.this.position);
            }

            @Override
            protected BoxConstraints childConstraints(BoxConstraints constraints) {
                return constraints.subtract(Positioned.this.position.get()).withMinimum(Size.ZERO);
            }

            @Override
            protected Size layout(BoxConstraints constraints, Node content, Size contentSize, boolean apply) {
                Point pos = Positioned.this.position.get();
                if (apply) {
                    content.position.set(pos);
                }
                return constraints.clamp(contentSize.add(pos));
            }
        });
    }
}
