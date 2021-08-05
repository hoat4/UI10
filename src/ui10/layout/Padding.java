package ui10.layout;

import ui10.binding.ObservableList;
import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.binding.Scope;
import ui10.geom.Num;
import ui10.geom.Point;
import ui10.geom.Size;
import ui10.nodes.LayoutNode;
import ui10.nodes.Node;
import ui10.nodes.Pane;

public class Padding extends Pane {

    public final ScalarProperty<Num> top = ScalarProperty.create();
    public final ScalarProperty<Num> right = ScalarProperty.create();
    public final ScalarProperty<Num> bottom = ScalarProperty.create();
    public final ScalarProperty<Num> left = ScalarProperty.create();
    public final ScalarProperty<Node> content = ScalarProperty.create();

    public Padding() {
    }

    public Padding(Num padding, Node content) {
        this(padding, padding, content);
    }

    public Padding(Num topBottom, Num leftRight, Node content) {
        this(topBottom, leftRight, topBottom, leftRight, content);
    }

    public Padding(Num top, Num right, Num bottom, Num left, Node content) {
        this.content.set(content);
        this.top.set(top);
        this.right.set(right);
        this.bottom.set(bottom);
        this.left.set(left);
    }

    public Padding(ObservableScalar<Num> padding, ObservableScalar<Node> content) {
        this(padding, padding, content);
    }

    public Padding(ObservableScalar<Num> topBottom, ObservableScalar<Num> leftRight, ObservableScalar<Node> content) {
        this(topBottom, leftRight, topBottom, leftRight, content);
    }

    public Padding(ObservableScalar<Num> top, ObservableScalar<Num> right,
                   ObservableScalar<Num> bottom, ObservableScalar<Num> left, ObservableScalar<Node> content) {
        this.content.bindTo(content);
        this.top.bindTo(top);
        this.right.bindTo(right);
        this.bottom.bindTo(bottom);
        this.left.bindTo(left);
    }

    @Override
    protected ObservableScalar<Node> paneContent() {
        return ObservableScalar.ofConstant(new OneChildOnePassLayout(content) {

            private Size all;

            {
                dependsOn(left, top, right, bottom);
            }

            @Override
            protected void initState() {
                all = new Size(left.get().add(right.get()), top.get().add(bottom.get()));
            }

            @Override
            protected BoxConstraints childConstraints(BoxConstraints constraints) {
                return new BoxConstraints(constraints.min().subtractOrClamp(all), constraints.max().subtract(all));
            }

            @Override
            protected Size layout(BoxConstraints constraints, Node content, Size contentSize, boolean apply) {
                if (apply)
                    content.position.set(new Point(left.get(), top.get()));
                return contentSize.add(all);
            }

        });
    }
}
