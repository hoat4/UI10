package ui10.ui6;

import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;

import java.util.ArrayList;
import java.util.List;

import static ui10.geom.Point.ORIGO;

public abstract class DelegatingPersistentRenderableNode extends RenderableNode {

    public  final List<NodeAndPosition> children = new ArrayList<>();

    public abstract Node content();

    public record NodeAndPosition(RenderableNode node, Rectangle bounds) {
    }

    @Override
    public Size computeSize(BoxConstraints constraints) {
        return content().computeSize(constraints);
    }

    @Override
    public void applySize(Size size, LayoutNode.LayoutContext layoutContext) {
        LayoutNode.LayoutContext c = new LayoutContextImpl();
        content().applySize(size, c);
    }

    private class LayoutContextImpl implements LayoutNode.LayoutContext {

        private Point p = ORIGO;

        {
            children.clear();
        }

        @Override
        public void placeNode(Node node, Rectangle bounds) {
            if (node instanceof LayoutNode) {
                Point prev = p;
                p = p.add(bounds.topLeft());
                node.applySize(bounds.size(), this);
                p = prev;
            } else if (node instanceof DelegatingPersistentRenderableNode r) {
                children.add(new NodeAndPosition(r, bounds));
                node.applySize(bounds.size(), this);
            } else {
                children.add(new NodeAndPosition((RenderableNode) node, bounds));
            }
        }
    }
}
