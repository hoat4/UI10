package ui10.ui6;

import ui10.geom.Rectangle;

public abstract class LayoutNode implements Node {

    public interface LayoutContext {

        void placeNode(Node node, Rectangle bounds);
    }
}
