package ui10.renderer6.java2d;

import ui10.geom.Rectangle;
import ui10.ui6.RenderableNode;
import ui10.ui6.RendererData;

import java.awt.Graphics2D;

public abstract class Item<N extends RenderableNode> implements RendererData {

    protected final J2DRenderer renderer;
    private boolean valid;
    public Rectangle bounds;
    protected final N node;

    public Item(J2DRenderer renderer, N node) {
        this.renderer = renderer;
        this.node = node;
    }

    @Override
    public void invalidate() {
        if (valid) {
            valid = false;
            renderer.requestRepaint();
        }
    }

    protected abstract void validate();

    public void draw(Graphics2D g) {
        if (!valid)
            validate();
        drawImpl(g);
        valid = true;
    }

    protected abstract void drawImpl(Graphics2D g);

}
