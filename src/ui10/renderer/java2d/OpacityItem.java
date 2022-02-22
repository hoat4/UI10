package ui10.renderer6.java2d;

import ui10.graphics.Opacity;

import java.awt.*;

import static java.awt.AlphaComposite.SRC_OVER;

public class OpacityItem extends Item<Opacity> {

    private Item<?> contentItem;

    public OpacityItem(J2DRenderer renderer, Opacity node) {
        super(renderer, node);
    }

    @Override
    protected void validateImpl() {
        if (contentItem == null)
            contentItem = renderer.makeItem(node.content);
    }

    @Override
    protected void drawImpl(Graphics2D g) {
        Composite prevComposite = g.getComposite();

        // should multiply with previous alpha, not replace
        g.setComposite(AlphaComposite.getInstance(SRC_OVER,node.fraction.toFloat()));
        contentItem.draw(g);
        g.setComposite(prevComposite);
    }
}
