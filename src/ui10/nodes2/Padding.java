package ui10.nodes2;

import ui10.binding.ScalarProperty;
import ui10.geom.Num;
import ui10.geom.Point;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;

public class Padding extends AbstractSingleNodeLayoutPane {

    private Num top, right, bottom, left;

    public Padding() {
    }

    public Padding(Num padding, Pane content) {
        this(padding, padding, content);
    }

    public Padding(Num topBottom, Num leftRight, Pane content) {
        this(topBottom, leftRight, topBottom, leftRight, content);
    }

    public Padding(Num top, Num right, Num bottom, Num left, Pane content) {
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
    public AbstractLayout computeLayout(BoxConstraints constraints) {
        Size all = new Size(left.add(right), top.add(bottom));
        Frame.FrameAndLayout childLayout = currentContentFrame().layout(new BoxConstraints(
                constraints.min().subtractOrClamp(all), constraints.max().subtract(all)));

        return new AbstractLayout(constraints, childLayout.size().add(all)) {

            {
                top().subscribe(e -> invalidate());
                right().subscribe(e -> invalidate());
                bottom().subscribe(e -> invalidate());
                left().subscribe(e -> invalidate());
            }

            @Override
            public void apply() {
                applyChild(childLayout, new Point(left, top));
            }
        };
    }
}
