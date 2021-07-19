package ui10.nodes;

import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;
import ui10.node.Node;

public class Centered extends SingleNodeLayoutNode {
    public Centered() {
    }

    public Centered(Node content) {
        super(content);
    }

    @Override
    protected Layout computeLayout(BoxConstraints constraints) {
        Node n = content().get();
        Layout contentLayout = n.layout(constraints.withMinimum(Size.ZERO));
        return new Layout(constraints, contentLayout.size) {

            @Override
            protected void apply() {
                applyChild(contentLayout, Rectangle.of(size).centered(contentLayout.size).topLeft());
            }
        };
    }
}
