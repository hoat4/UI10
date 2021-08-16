package ui10.renderer6.java2d;

import ui10.ui6.DelegatingPersistentRenderableNode;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class PersistentItem extends Item<DelegatingPersistentRenderableNode> {

    private final List<Item<?>> children = new ArrayList<>();

    public PersistentItem(J2DRenderer renderer, DelegatingPersistentRenderableNode node) {
        super(renderer, node);
    }

    @Override
    protected void validate() {
        node.applySize(bounds.size(), null);
        children.clear();
        for (DelegatingPersistentRenderableNode.NodeAndPosition n : node.children)
            children.add(renderer.makeItem(n));
    }

    @Override
    protected void drawImpl(Graphics2D g) {
        AffineTransform t = g.getTransform();
        for (Item<?> item : children){
            //g.translate(item.bounds.topLeft().x(), item.bounds.topLeft().y());
            item.draw(g);
            //g.setTransform(t);
        }
    }
}
