package ui10.shell.renderer.java2d;

import ui10.base.*;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.input.pointer.MouseEvent;
import ui10.layout.BoxConstraints;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class J2DContainer extends J2DRenderableElement<Container> implements Container.ContainerListener {

    private final List<J2DRenderableElement<?>> children = new ArrayList<>();
    private Shape shape2;

    public J2DContainer(J2DRenderer renderer, Container node) {
        super(renderer, node);
    }

    @Override
    public void contentChanged() {
        invalidateRendererData();
    }

    @Override
    protected void drawImpl(Graphics2D g) {
        AffineTransform t = g.getTransform();
        for (J2DRenderableElement<?> item : children) {
            assert item != this;
            // g.setClip
            //g.translate(item., item.y);
            item.draw(g);
            //g.setTransform(t);
        }
    }

    @Override
    protected void validateImpl() {
        if (shape2 == null)
            return;

        Element content = node.getContent();
        content.initParent(this);

        children.clear();

        // inter-container layout dependencies are not supported currently
        new LayoutContext2(this) {

            {
                dependencies.putAll(J2DContainer.this.layoutDependencies);
            }

            @Override
            public void accept(RenderableElement e) {
                if (e.parentRenderable() == null)
                    throw new IllegalStateException("no parent renderable set for: " + e);
                    // régi komment: this should not occur, but currently does because decoration
                    // e.parent = ui10.base.Container.this;

                else if (e.parentRenderable() != J2DContainer.this)
                    throw new IllegalStateException("not a child of " + J2DContainer.this + ": " + e + ", instead child of " + e.parentRenderable());
                children.add((J2DRenderableElement<?>) e);
            }
        }.placeElement(content, shape2);
    }


    @Override
    protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
        validateIfNeeded();
        Element content = node.getContent();
        content.initParent(this);
        return context.preferredSize(content, constraints);
    }

    @Override
    protected void onShapeApplied(Shape shape) {
        this.shape2 = shape;
        super.onShapeApplied(shape);

        validateIfNeeded();
    }

    @Override
    public boolean captureMouseEvent(MouseEvent p, List<InputHandler> l, EventContext eventContext) {
        if (node instanceof InputHandler c) {
            InputHandler.dispatchInputEvent(p, c, eventContext, true);
            if (eventContext.stopPropagation)
                return true;

            l.add(c);
        }

        for (J2DRenderableElement<?> item : children) {
            if (item.shape.contains(J2DUtil.point(p.point())) && item.captureMouseEvent(p, l, eventContext)) {
                return true;
                //return item.captureMouseEvent(p.subtract(new Point(item.x, item.y)), l);
            }
        }

        return node instanceof InputHandler;
    }

    @Override
    public String toString() {
        return "J2DContainer (" + node + ")";
    }
}
