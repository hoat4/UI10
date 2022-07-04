package ui10.shell.renderer.java2d;

import ui10.base.*;
import ui10.binding9.Bindings;
import ui10.geom.Point;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.input.pointer.MouseEvent;
import ui10.layout.BoxConstraints;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import static ui10.binding9.Bindings.onInvalidated;

public abstract class AbstractJ2DContainer<E extends ElementModel> extends J2DRenderableElement<E> {

    private final List<J2DRenderableElement<?>> children = new ArrayList<>();
    private Shape shape2;

    public AbstractJ2DContainer(J2DRenderer renderer, E node) {
        super(renderer, node);
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

    protected abstract Element getContent();

    @Override
    protected void validateImpl() {
        if (shape2 == null)
            return;

        Element content = getContent();
        content.initParent(this);

        children.clear();

        Bindings.onInvalidated(() -> {
            new LayoutContext2(this) {

                @Override
                public void accept(RenderableElement e) {
                    if (e.parentRenderable() == null)
                        throw new IllegalStateException("no parent renderable set for: " + e);
                        // régi komment: this should not occur, but currently does because decoration
                        // e.parent = ui10.base.Container.this;

                    else if (e.parentRenderable() != AbstractJ2DContainer.this)
                        throw new IllegalStateException("not a child of " + AbstractJ2DContainer.this + ": " + e + ", instead child of " + e.parentRenderable());
                    children.add((J2DRenderableElement<?>) e);
                }
            }.placeElement(content, shape2);
        }, this::invalidateRenderableElementAndLayout);
    }


    @Override
    protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
        validateIfNeeded();
        Element content = getContent();
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
    public boolean captureMouseEvent(MouseEvent p, List<MouseTarget> l, EventContext eventContext) {
        for (J2DRenderableElement<?> item : children)
            if (item.shape.contains(J2DUtil.point(p.point())) && item.captureMouseEvent(p, l, eventContext))
                return true;
        return false;
    }

    @Override
    public ContentEditable.ContentPoint pickPosition(Point point) {
        return getContent().pickPosition(point);
    }

    @Override
    public Shape shapeOfSelection(ContentEditable.ContentRange<?> range) {
        return getContent().shapeOfSelection(range);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " (" + node + ")";
    }
}
