package ui10.nodes;

import ui10.binding.ScalarProperty;
import ui10.geom.Num;
import ui10.geom.Point;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;
import ui10.node.Node;

public class Padding extends SingleNodeLayoutNode {

    private Num top, right, bottom, left;

    public Padding() {
    }

    public Padding(Num padding, Node content) {
        this(padding, padding, content);
    }

    public Padding(Num topBottom, Num leftRight, Node content) {
        this(topBottom, leftRight, topBottom, leftRight, content);
    }

    public Padding(Num top, Num right, Num bottom, Num left, Node content) {
        super(content);
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public ScalarProperty<Num> top() {
        return property((Padding n) -> n.top, (n, v) -> n.top = v);
    }

    public ScalarProperty<Num> right() {
        return property((Padding n) -> n.right, (n, v) -> n.right = v);
    }

    public ScalarProperty<Num> bottom() {
        return property((Padding n) -> n.bottom, (n, v) -> n.bottom = v);
    }

    public ScalarProperty<Num> left() {
        return property((Padding n) -> n.left, (n, v) -> n.left = v);
    }

    @Override
    protected Layout computeLayout(BoxConstraints constraints) {
        Size all = new Size(left.add(right), top.add(bottom));
        Layout childLayout = content().get().layout(new BoxConstraints(
                constraints.min().subtractOrClamp(all), constraints.max().subtract(all)));


        return new Layout(constraints, childLayout.size.add(all)) {

            {
                top().subscribe(e->valid.set(false));
                right().subscribe(e->valid.set(false));
                bottom().subscribe(e->valid.set(false));
                left().subscribe(e->valid.set(false));
            }

            @Override
            protected void apply() {
                applyChild(childLayout, new Point(left, top));
            }
        };
    }
}
