package ui10.nodes;

import ui10.binding.ObservableList;
import ui10.binding.ScalarProperty;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;
import ui10.node.Node;

import static ui10.geom.Point.ORIGO;

public class FixedSize extends SingleNodeLayoutNode {

    private Size size;

    public FixedSize() {
    }

    public FixedSize(Node content, Size size) {
        super(content);
        this.size = size;
    }

    public ScalarProperty<Size> size() {
        return property((FixedSize n) -> n.size, (n, v) -> n.size = v);
    }

    @Override
    protected Layout computeLayout(BoxConstraints constraints) {
        // TODO mi legyen, ha nem lehetséges a kért méret? dobjunk exceptiont?
        return new Layout(constraints, size) {
            @Override
            protected void apply() {
                applyChild(content().get().layout(BoxConstraints.fixed(size)), ORIGO);
            }
        };
    }
}
