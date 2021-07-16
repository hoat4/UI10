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
        return new Layout(constraints, constraints.max()) {

            @Override
            protected void apply() {
                Node n = content().get();
                Layout contentLayout = n.layout(constraints.withMinimum(Size.ZERO));
                applyChild(contentLayout, Rectangle.of(size).centered(contentLayout.size).topLeft());
            }
        };
    }
}
